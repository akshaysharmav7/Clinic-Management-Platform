package org.app.clinic_management_platform.doctor;

public class DoctorAlreadyExistsException extends RuntimeException{
    public DoctorAlreadyExistsException(String licenseNumber){
        super("Doctor already exists with license number: " + licenseNumber);
    }
}
