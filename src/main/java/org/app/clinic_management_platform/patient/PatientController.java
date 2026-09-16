package org.app.clinic_management_platform.patient;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService){
        this.patientService = patientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientResponse createPatient(
            @Valid @RequestBody CreatePatientRequest patient){
        return patientService.createPatient(patient);
    }

    @GetMapping("/{id}")
    public PatientResponse getPatient(@PathVariable Long id){
        return patientService.getPatient(id);
    }

    @GetMapping
    public Page<PatientResponse> getPatients(@RequestParam(required = false) String search, Pageable pageable){
        return patientService.getPatients(search, pageable);
    }

    @PutMapping("/{id}")
    public PatientResponse updatePatient(@PathVariable Long id, @Valid @RequestBody UpdatePatientRequest request){
        return patientService.updatePatient(id, request);
    }
}
