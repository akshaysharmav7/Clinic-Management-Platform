package org.app.clinic_management_platform.appointment;

import java.time.LocalDateTime;

public class AppointmentResponse {

    private final Long id;
    private final Long patientId;
    private final String patientName;
    private final Long doctorId;
    private final String doctorName;
    private final LocalDateTime appointmentDateTime;
    private final AppointmentStatus status;
    private final String reason;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public AppointmentResponse(
            Long id,
            Long patientId,
            String patientName,
            Long doctorId,
            String doctorName,
            LocalDateTime appointmentDateTime,
            AppointmentStatus status,
            String reason,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
        this.reason = reason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}