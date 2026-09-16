package org.app.clinic_management_platform.patient;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

class PatientServiceTest {

    private final PatientRepository patientRepository =
            mock(PatientRepository.class);

    private final PatientMapper patientMapper =
            mock(PatientMapper.class);

    private final PatientService patientService =
            new PatientService(patientRepository, patientMapper);

    @Test
    void getPatient_shouldThrowException_whenPatientDoesNotExist() {

        when(patientRepository.findById(999L))
                .thenReturn(java.util.Optional.empty());

        assertThrows(
                PatientNotFoundException.class,
                () -> patientService.getPatient(999L)
        );

        verify(patientRepository).findById(999L);
        verifyNoInteractions(patientMapper);
    }

    @Test
    void getPatient_shouldReturnPatient_whenPatientExists(){

        Patient patient = new Patient(
                "Jhon",
                "Doe",
                java.time.LocalDate.of(1999,1,1),
                "Male",
                "9823423434",
                "John@exmail.com"
        );
        PatientResponse response = new PatientResponse(
                1L,
                "John",
                "Doe",
                java.time.LocalDate.of(1990, 1, 1),
                "Male",
                "9876543210",
                "john@example.com",
                null,
                null
        );

     when(patientRepository.findById(1L))
             .thenReturn(java.util.Optional.of(patient));

     when(patientMapper.toResponse(patient))
             .thenReturn(response);

     PatientResponse result = patientService.getPatient(1L);

     assertEquals("John",
             result.getFirstName());
     assertEquals("Doe",
                 result.getLastName()
        );
     verify(patientRepository).findById(1L);
     verify(patientMapper).toResponse(patient);
    }

    @Test
    void createPatient_shouldSavePatientAndReturnResponse() {

        CreatePatientRequest request = new CreatePatientRequest();

        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setPhone("9876543210");
        request.setEmail("john@example.com");

        Patient patient = new Patient(
                "John",
                "Doe",
                java.time.LocalDate.of(1990, 1, 1),
                "Male",
                "9876543210",
                "john@example.com"
        );

        Patient savedPatient = patient;

        PatientResponse response = new PatientResponse(
                1L,
                "John",
                "Doe",
                java.time.LocalDate.of(1990, 1, 1),
                "Male",
                "9876543210",
                "john@example.com",
                null,
                null
        );

        when(patientMapper.toEntity(request))
                .thenReturn(patient);

        when(patientRepository.save(patient))
                .thenReturn(savedPatient);

        when(patientMapper.toResponse(savedPatient))
                .thenReturn(response);

        PatientResponse result =
                patientService.createPatient(request);

        org.junit.jupiter.api.Assertions.assertEquals(
                "John",
                result.getFirstName()
        );

        org.junit.jupiter.api.Assertions.assertEquals(
                "Doe",
                result.getLastName()
        );

        verify(patientMapper).toEntity(request);
        verify(patientRepository).save(patient);
        verify(patientMapper).toResponse(savedPatient);
    }

}
