package org.app.clinic_management_platform.patient;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @Test
    void getPatient_shouldReturn200_whenPatientExists() throws Exception {

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

        when(patientService.getPatient(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/patients/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getPatient_shouldReturn404_whenPatientDeosNotExists() throws Exception{
        when(patientService.getPatient(999L))
                .thenThrow(new PatientNotFoundException(999L));
        mockMvc.perform(
                get("/api/v1/patients/999")
        )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Patient not found with id: 999"));
    }
    @Test
    void createPatient_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        String requestBody = """
            {
                "firstName": "",
                "lastName": "",
                "dateOfBirth": "2030-01-01",
                "email": "not-an-email"
            }
            """;

        mockMvc.perform(
                        post("/api/v1/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.dateOfBirth").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }
}