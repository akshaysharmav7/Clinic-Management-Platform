package org.app.clinic_management_platform.appointment;

import java.time.LocalDateTime;

public class AppointmentAlreadyExistsException extends RuntimeException {
    public AppointmentAlreadyExistsException(Long doctorId, LocalDateTime appointmentDateTime) {
        super(
                "Doctor already has an appointment at "
                + appointmentDateTime
                + "with id: "
                + doctorId
        );
    }
}
