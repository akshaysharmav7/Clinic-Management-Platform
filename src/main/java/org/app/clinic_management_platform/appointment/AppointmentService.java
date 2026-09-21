package org.app.clinic_management_platform.appointment;

import org.app.clinic_management_platform.doctor.Doctor;
import org.app.clinic_management_platform.doctor.DoctorNotFoundException;
import org.app.clinic_management_platform.doctor.DoctorRepository;
import org.app.clinic_management_platform.patient.Patient;
import org.app.clinic_management_platform.patient.PatientNotFoundException;
import org.app.clinic_management_platform.patient.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentMapper appointmentMapper
    ){
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentMapper = appointmentMapper;
    }

    @Transactional
    public AppointmentResponse createAppointment(CreateAppointmentRequest request){
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(()-> new PatientNotFoundException(request.getPatientId()));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(()-> new DoctorNotFoundException(request.getDoctorId()));

        if(appointmentRepository.existsByDoctorIdAndAppointmentDateTime(
                request.getDoctorId(), request.getAppointmentDateTime())){
            throw new AppointmentAlreadyExistsException(
                    request.getDoctorId(),
                    request.getAppointmentDateTime()
            );
        }
        Appointment appointment = new Appointment(
                patient,
                doctor,
                request.getAppointmentDateTime(),
                request.getStatus(),
                request.getReason()
        );

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment(Long id){

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(()-> new AppointmentNotFoundException(id));

        return appointmentMapper.toResponse(appointment);

    }
}
