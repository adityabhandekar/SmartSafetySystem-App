package women_safety_api.auth;

import java.io.IOException;
import java.util.Collections;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final FirebaseUserService firebaseUserService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            FirebaseUserService firebaseUserService) {

        this.jwtService = jwtService;
        this.firebaseUserService = firebaseUserService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Device SOS is intentionally public for the current ESP32 prototype
        if (request.getRequestURI().equals("/api/v1/device/sos")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // No JWT -> continue normally.
        // Spring Security will reject protected endpoints later.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // Extract user ID from JWT
            String userId = jwtService.extractUserId(token);

            if (userId == null || userId.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            // Find user from Firestore
            User user = firebaseUserService.findById(userId);

            if (user == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Validate JWT against the user
            if (jwtService.isTokenValid(token, user)) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user.getEmail(),
                                null,
                                Collections.emptyList()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                System.out.println(
                        "JWT authentication successful for: "
                                + user.getEmail()
                );
            }

        } catch (Exception e) {

            // Do not crash the request.
            // Spring Security will handle protected endpoints
            // as unauthenticated.
            System.out.println(
                    "JWT authentication failed: "
                            + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }
}