package org.app.clinic_management_platform.appointment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService){
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse createAppointment(@Valid @RequestBody CreateAppointmentRequest request){
        return appointmentService.createAppointment(request);
    }

    @GetMapping("/{id}")
    public AppointmentResponse getAppointment(
            @PathVariable Long id
    ){
        return appointmentService.getAppointment(id);
    }
}
