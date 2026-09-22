package org.app.clinic_management_platform.appointment;

import org.app.clinic_management_platform.doctor.Doctor;
import org.app.clinic_management_platform.doctor.DoctorNotFoundException;
import org.app.clinic_management_platform.doctor.DoctorRepository;
import org.app.clinic_management_platform.patient.Patient;
import org.app.clinic_management_platform.patient.PatientNotFoundException;
import org.app.clinic_management_platform.patient.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentMapper appointmentMapper;

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp(){
        appointmentService = new AppointmentService(
                appointmentRepository,
                patientRepository,
                doctorRepository,
                appointmentMapper
        );
    }
    @Test
    void createAppointment_shouldCreateAppointment() {

        Patient patient = new Patient(
                "John",
                "Patient",
                LocalDate.of(1990, 1, 1),
                "Male",
                "9876543210",
                "john@test.com"
        );

        Doctor doctor = new Doctor(
                "Jane",
                "Doctor",
                "Cardiology",
                "DOC-1001",
                "9876543211",
                "jane@test.com"
        );

        LocalDateTime appointmentDateTime =
                LocalDateTime.of(2026, 9, 25, 10, 30);

        CreateAppointmentRequest request =
                new CreateAppointmentRequest();

        request.setPatientId(1L);
        request.setDoctorId(2L);
        request.setAppointmentDateTime(appointmentDateTime);
        request.setStatus(AppointmentStatus.SCHEDULED);
        request.setReason("Regular consultation");

        Appointment appointment = new Appointment(
                patient,
                doctor,
                appointmentDateTime,
                AppointmentStatus.SCHEDULED,
                "Regular consultation"
        );

        AppointmentResponse response =
                mock(AppointmentResponse.class);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(doctorRepository.findById(2L))
                .thenReturn(Optional.of(doctor));

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateTime(
                        2L,
                        appointmentDateTime
                ))
                .thenReturn(false);

        when(appointmentRepository.save(any(Appointment.class)))
                .thenReturn(appointment);

        when(appointmentMapper.toResponse(appointment))
                .thenReturn(response);

        AppointmentResponse result =
                appointmentService.createAppointment(request);

        assertThat(result).isSameAs(response);

        verify(patientRepository).findById(1L);
        verify(doctorRepository).findById(2L);

        verify(appointmentRepository)
                .existsByDoctorIdAndAppointmentDateTime(
                        2L,
                        appointmentDateTime
                );

        verify(appointmentRepository)
                .save(any(Appointment.class));

        verify(appointmentMapper)
                .toResponse(appointment);
    }

    @Test
    void createAppointment_shouldThrow_whenPatientDoesNotExist() {

        CreateAppointmentRequest request =
                new CreateAppointmentRequest();

        request.setPatientId(999L);
        request.setDoctorId(1L);
        request.setAppointmentDateTime(
                LocalDateTime.of(2026, 9, 25, 10, 30)
        );
        request.setStatus(AppointmentStatus.SCHEDULED);

        when(patientRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                appointmentService.createAppointment(request)
        )
                .isInstanceOf(PatientNotFoundException.class);

        verify(patientRepository).findById(999L);

        verifyNoInteractions(doctorRepository);
        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void createAppointment_shouldThrow_whenDoctorDoesNotExist() {

        Patient patient = new Patient(
                "John",
                "Patient",
                LocalDate.of(1990, 1, 1),
                "Male",
                null,
                null
        );

        CreateAppointmentRequest request =
                new CreateAppointmentRequest();

        request.setPatientId(1L);
        request.setDoctorId(999L);
        request.setAppointmentDateTime(
                LocalDateTime.of(2026, 9, 25, 10, 30)
        );
        request.setStatus(AppointmentStatus.SCHEDULED);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(doctorRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                appointmentService.createAppointment(request)
        )
                .isInstanceOf(DoctorNotFoundException.class);

        verify(patientRepository).findById(1L);
        verify(doctorRepository).findById(999L);

        verifyNoInteractions(appointmentRepository);
    }

    @Test
    void createAppointment_shouldThrow_whenSlotAlreadyExists() {

        Patient patient = new Patient(
                "John",
                "Patient",
                LocalDate.of(1990, 1, 1),
                "Male",
                null,
                null
        );

        Doctor doctor = new Doctor(
                "Jane",
                "Doctor",
                "Cardiology",
                "DOC-1001",
                null,
                null
        );

        LocalDateTime appointmentDateTime =
                LocalDateTime.of(2026, 9, 25, 10, 30);

        CreateAppointmentRequest request =
                new CreateAppointmentRequest();

        request.setPatientId(1L);
        request.setDoctorId(2L);
        request.setAppointmentDateTime(appointmentDateTime);
        request.setStatus(AppointmentStatus.SCHEDULED);

        when(patientRepository.findById(1L))
                .thenReturn(Optional.of(patient));

        when(doctorRepository.findById(2L))
                .thenReturn(Optional.of(doctor));

        when(appointmentRepository
                .existsByDoctorIdAndAppointmentDateTime(
                        2L,
                        appointmentDateTime
                ))
                .thenReturn(true);

        assertThatThrownBy(() ->
                appointmentService.createAppointment(request)
        )
                .isInstanceOf(AppointmentAlreadyExistsException.class);

        verify(appointmentRepository)
                .existsByDoctorIdAndAppointmentDateTime(
                        2L,
                        appointmentDateTime
                );

        verify(appointmentRepository, never())
                .save(any());
    }

    @Test
    void getAppointment_shouldReturnAppointment() {

        Patient patient = new Patient(
                "John",
                "Patient",
                LocalDate.of(1990, 1, 1),
                "Male",
                null,
                null
        );

        Doctor doctor = new Doctor(
                "Jane",
                "Doctor",
                "Cardiology",
                "DOC-1001",
                null,
                null
        );

        LocalDateTime appointmentDateTime =
                LocalDateTime.of(2026, 9, 25, 10, 30);

        Appointment appointment = new Appointment(
                patient,
                doctor,
                appointmentDateTime,
                AppointmentStatus.SCHEDULED,
                "Checkup"
        );

        AppointmentResponse response =
                mock(AppointmentResponse.class);

        when(appointmentRepository.findById(1L))
                .thenReturn(Optional.of(appointment));

        when(appointmentMapper.toResponse(appointment))
                .thenReturn(response);

        AppointmentResponse result =
                appointmentService.getAppointment(1L);

        assertThat(result).isSameAs(response);

        verify(appointmentRepository)
                .findById(1L);

        verify(appointmentMapper)
                .toResponse(appointment);
    }

    @Test
    void getAppointment_shouldThrow_whenAppointmentDoesNotExist() {

        when(appointmentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                appointmentService.getAppointment(999L)
        )
                .isInstanceOf(AppointmentNotFoundException.class);

        verify(appointmentRepository)
                .findById(999L);
    }

}
