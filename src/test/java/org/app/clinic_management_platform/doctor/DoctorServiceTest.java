package org.app.clinic_management_platform.doctor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorMapper doctorMapper;

    private DoctorService doctorService;

    @BeforeEach
    void setUp(){
        doctorService = new DoctorService(
                doctorRepository,
                doctorMapper
        );
    }

    @Test
    void createDoctor_shouldCreateDoctor(){

        CreateDoctorRequest request = new CreateDoctorRequest();
        request.setFirstName("Sarah");
        request.setLastName("Smith");
        request.setSpecialization("Cardiology");
        request.setLicenseNumber("DOC-1001");
        request.setPhone("9876543210");
        request.setEmail("sarah@clinic.com");

        Doctor doctor = new Doctor(
        "Sarah",
        "Smith",
        "Cardiology",
        "DOC-1001",
        "9876543210",
        "sarah@clinic.com"
        );

        DoctorResponse response = new DoctorResponse(
                1L,
                "Sarah",
                "Smith",
                "Cardiology",
                "DOC-1001",
                "9876543210",
                "sarah@clinic.com",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(doctorRepository.existsByLicenseNumber("DOC-1001"))
                .thenReturn(false);
        when(doctorMapper.toEntity(request))
                .thenReturn(doctor);
        when(doctorRepository.save(doctor))
                .thenReturn(doctor);
        when(doctorMapper.toResponse(doctor))
                .thenReturn(response);

        DoctorResponse result = doctorService.createDoctor(request);
        assertThat(result).isSameAs(response);

        verify(doctorRepository)
                .existsByLicenseNumber("DOC-1001");
        verify(doctorRepository)
                .save(doctor);
        verify(doctorMapper).toEntity(request);
        verify(doctorMapper).toResponse(doctor);
    }

    @Test
    void createDoctor_shouldThrowException_whenLicenseAlreadyExists(){
        CreateDoctorRequest request = new CreateDoctorRequest();
        request.setFirstName("Sarah");
        request.setLastName("Smith");
        request.setSpecialization("Cardiology");
        request.setLicenseNumber("DOC-1001");

        when(doctorRepository.existsByLicenseNumber("DOC-1001"))
                .thenReturn(true);

        assertThatThrownBy(()->doctorService.createDoctor(request))
                .isInstanceOf(DoctorAlreadyExistsException.class)
                .hasMessage(
                        "Doctor already exists with license number: DOC-1001"
                );
        verify(doctorRepository)
                .existsByLicenseNumber("DOC-1001");
        verify(doctorRepository, never())
                .save(any());
    }

    @Test
    void getDoctor_shouldReturnDoctor(){
        Doctor doctor = new Doctor(
                "Sarah",
                "Smith",
                "Cardiology",
                "DOC-1001",
                "9876543210",
                "sarah@clinic.com"
        );

        DoctorResponse response = new DoctorResponse(
                1L,
                "Sarah",
                "Smith",
                "Cardiology",
                "DOC-1001",
                "9876543210",
                "sarah@clinic.com",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(doctorRepository.findById(1L))
                .thenReturn(Optional.of(doctor));

        when(doctorMapper.toResponse(doctor))
                .thenReturn(response);
        DoctorResponse result = doctorService.getDoctor(1L);

        assertThat(result).isSameAs(response);

        verify(doctorRepository).findById(1L);
        verify(doctorMapper).toResponse(doctor);
    }

    @Test
    void getDoctor_shouldThrowException_whenDoctorDoesNotExist() {
        when(doctorRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(()-> doctorService.getDoctor(999L))
                .isInstanceOf(DoctorNotFoundException.class)
                .hasMessage("Doctor not found with id: 999");
        verify(doctorRepository).findById(999L);

        verify(doctorMapper, never())
                .toResponse(any());
        }
    }
