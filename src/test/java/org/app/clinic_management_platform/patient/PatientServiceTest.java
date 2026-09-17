package org.app.clinic_management_platform.patient;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import static org.mockito.Mockito.*;

//Unit Tests
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
                .thenReturn(Optional.empty());

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
             .thenReturn(Optional.of(patient));

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

    @Test
    void updatePatient_shouldUpdateAndReturnResponse(){
        Long patientId = 1L;
        Patient patient = new Patient(
                "John",
                "Doe",
                java.time.LocalDate.of(1990, 1, 1),
                "Male",
                "9876543210",
                "john@example.com"
        );
        UpdatePatientRequest request = new UpdatePatientRequest();
        request.setFirstName("Jhonny");
        request.setLastName("Doe");
        request.setDateOfBirth(java.time.LocalDate.of(1990, 1, 1));
        request.setGender("Male");
        request.setPhone("9999999999");
        request.setEmail("johnny@example.com");

        PatientResponse response = new PatientResponse(
                patientId,
                "Johnny",
                "Doe",
                java.time.LocalDate.of(1990, 1, 1),
                "Male",
                "9999999999",
                "johnny@example.com",
                null,
                null
        );

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));
        when(patientMapper.toResponse(patient))
                .thenReturn(response);
        PatientResponse result = patientService.updatePatient(patientId, request);
        assertEquals("9999999999",
                result.getPhone());
        verify(patientRepository).findById(patientId);
        verify(patientMapper).updateEntity(patient, request);
        verify(patientMapper).toResponse(patient);
    }

    @Test
    void deletePatient_shouldDeletePatient_whenPatientExists(){
        Long patientId = 1L;

        Patient patient = new Patient(
                "John",
                "Doe",
                java.time.LocalDate.of(1990, 1, 1),
                "Male",
                "9876543210",
                "john@example.com"
        );
        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));
        patientService.deletePatient(patientId);
        verify(patientRepository).findById(patientId);
        verify(patientRepository).delete(patient);
    }

    @Test
    void deletePatient_shouldThrowException_whenPatientDoesNotExist(){
        Long patientId = 999L;
        when(patientRepository.findById(patientId))
                .thenReturn(Optional.empty());
        assertThrows(
                PatientNotFoundException.class,
                ()-> patientService.deletePatient(patientId)
        );

        verify(patientRepository).findById(patientId);
        verify(patientRepository, never()).delete(any());
    }
}
