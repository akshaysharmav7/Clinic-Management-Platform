package org.app.clinic_management_platform.doctor;

public class DoctorNotFoundException extends RuntimeException{
    public DoctorNotFoundException(Long id){
        super("Doctor not found with id: " + id);
    }
}
