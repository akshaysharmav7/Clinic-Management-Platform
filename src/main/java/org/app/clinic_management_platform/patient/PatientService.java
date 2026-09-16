package org.app.clinic_management_platform.patient;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PatientService(PatientRepository patientRepository, PatientMapper patientMapper){
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
    }

    //CREATE Patient
    @Transactional
    public PatientResponse createPatient(CreatePatientRequest request){
        Patient patient = patientMapper.toEntity(request);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toResponse(savedPatient);
    }

    //Get Patient by ID
    @Transactional(readOnly = true)
    public PatientResponse getPatient(Long id){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(()-> new PatientNotFoundException(id));
        return patientMapper.toResponse(patient);
    }
}
