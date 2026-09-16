package org.app.clinic_management_platform.patient;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    //Get Patients (Old)
//    @Transactional(readOnly = true)
//    public Page<PatientResponse> getPatients(Pageable pageable){
//        return patientRepository.findAll(pageable)
//                .map(patientMapper::toResponse);
//    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> getPatients(String search, Pageable pageable){
        Page<Patient> patients;
        if(search == null || search.isBlank()){
            patients = patientRepository.findAll(pageable);
        } else {
            patients = patientRepository.searchPatients(search, pageable);
        }

        return patients.map(patientMapper::toResponse);
    }

    //Update Patient
    @Transactional
    public PatientResponse updatePatient(Long id, UpdatePatientRequest request){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(()-> new PatientNotFoundException(id));
        patientMapper.updateEntity(patient, request);
        return patientMapper.toResponse(patient);
    }

    //Delete Patient
    @Transactional
    public void deletePatient(Long id){
        Patient patient = patientRepository.findById(id)
                .orElseThrow(()->new PatientNotFoundException(id));
        patientRepository.delete(patient);
    }
}
