package org.app.clinic_management_platform.appointment;

import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(Appointment appointment) {

        String patientName =
                appointment.getPatient().getFirstName()
                        + " "
                        + appointment.getPatient().getLastName();

        String doctorName =
                appointment.getDoctor().getFirstName()
                        + " "
                        + appointment.getDoctor().getLastName();

        return new AppointmentResponse(
                appointment.getId(),
                appointment.getPatient().getId(),
                patientName,
                appointment.getDoctor().getId(),
                doctorName,
                appointment.getAppointmentDateTime(),
                appointment.getStatus(),
                appointment.getReason(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }
}