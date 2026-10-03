package women_safety_api.sos;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sos_incidents")
public class SosIncident {

    @Id
    private String id;

    private String userId;

    private String deviceId;

    private TriggerType trigger;

    private IncidentStatus status;

    private Instant startedAt;

    private Instant endedAt;

    private Double latestHeartRate;

    private Boolean fallDetected;

    private RiskLevel riskLevel;

    private Double riskScore;

    public enum TriggerType {
        BAND_BUTTON,
        APP_BUTTON,
        FALL_SENSOR
    }

    public enum IncidentStatus {
        CREATED,
        ACTIVE,
        ESCALATED,
        RESOLVED,
        CANCELLED
    }

    public enum RiskLevel {
        PENDING,
        LOW,
        MEDIUM,
        HIGH,
        UNKNOWN
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public TriggerType getTrigger() {
        return trigger;
    }

    public void setTrigger(TriggerType trigger) {
        this.trigger = trigger;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public Double getLatestHeartRate() {
        return latestHeartRate;
    }

    public void setLatestHeartRate(Double latestHeartRate) {
        this.latestHeartRate = latestHeartRate;
    }

    public Boolean getFallDetected() {
        return fallDetected;
    }

    public void setFallDetected(Boolean fallDetected) {
        this.fallDetected = fallDetected;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }
}