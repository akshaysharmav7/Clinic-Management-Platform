package org.app.clinic_management_platform.patient;

public class PatientNotFoundException extends RuntimeException{
    public PatientNotFoundException(Long id){
        super("Patient not found with id: "+id);
    }
}
