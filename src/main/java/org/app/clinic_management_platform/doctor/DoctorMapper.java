package org.app.clinic_management_platform.doctor;

public class DoctorMapper {
    public Doctor toEntity(CreateDoctorRequest request){
        return new Doctor(
                request.getFirstName(),
                request.getLastName(),
                request.getSpecialization(),
                request.getLicenseNumber(),
                request.getPhone(),
                request.getEmail()
        );
    }
    public DoctorResponse toResponse(Doctor doctor){
        return new DoctorResponse(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialization(),
                doctor.getLicenseNumber(),
                doctor.getPhone(),
                doctor.getEmail(),
                doctor.getCreatedAt(),
                doctor.getUpdatedAt()
        );
    }
}
