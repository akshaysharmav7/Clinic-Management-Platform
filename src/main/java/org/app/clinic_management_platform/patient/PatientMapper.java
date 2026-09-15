package org.app.clinic_management_platform.patient;

import org.springframework.stereotype.Component;

@Component
public class PatientMapper{
    public Patient toEntity(CreatePatientRequest request){
        return new Patient(
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getPhone(),
                request.getEmail()
        );
    }

    public PatientResponse toResponse(Patient patient){
        return new PatientResponse(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getPhone(),
                patient.getEmail(),
                patient.getCreatedAt(),
                patient.getUpdatedAt()
        );
    }
}