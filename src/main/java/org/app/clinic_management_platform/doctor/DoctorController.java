package org.app.clinic_management_platform.doctor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService){
        this.doctorService = doctorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorResponse createDoctor(@Valid @RequestBody CreateDoctorRequest request){
        return doctorService.createDoctor(request);
    }

    @GetMapping("/{id}")
    public DoctorResponse getDoctor(@PathVariable Long id){
        return doctorService.getDoctor(id);
    }

    @GetMapping
    public List<DoctorResponse> getDoctors(){
        return doctorService.getDoctors();
    }


}
