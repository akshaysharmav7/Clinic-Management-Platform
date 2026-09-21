package org.app.clinic_management_platform.appointment;


import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateTime(
            Long doctorId,
            LocalDateTime appointmentDateTime
    );
}