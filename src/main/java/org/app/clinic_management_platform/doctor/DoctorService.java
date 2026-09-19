package org.app.clinic_management_platform.doctor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;

    public DoctorService(
            DoctorRepository doctorRepository,
            DoctorMapper doctorMapper
    ) {
        this.doctorRepository = doctorRepository;
        this.doctorMapper = doctorMapper;
    }

    @Transactional
    public DoctorResponse createDoctor(CreateDoctorRequest request) {

        if (doctorRepository.existsByLicenseNumber(
                request.getLicenseNumber()
        )) {
            throw new DoctorAlreadyExistsException(
                    request.getLicenseNumber()
            );
        }

        Doctor doctor = doctorMapper.toEntity(request);

        Doctor savedDoctor = doctorRepository.save(doctor);

        return doctorMapper.toResponse(savedDoctor);
    }

    @Transactional(readOnly = true)
    public DoctorResponse getDoctor(Long id) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));

        return doctorMapper.toResponse(doctor);
    }
}