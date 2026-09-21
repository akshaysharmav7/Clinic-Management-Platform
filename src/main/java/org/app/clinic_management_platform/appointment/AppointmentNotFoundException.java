package org.app.clinic_management_platform.appointment;

public class AppointmentNotFoundException extends RuntimeException{
    public AppointmentNotFoundException(Long id){
        super("Appointment not found with id: " + id);
    }
}
