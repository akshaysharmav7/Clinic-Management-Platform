package org.app.clinic_management_platform.doctor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoctorController.class)
public class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;

    @Test
    void getDoctor_shouldReturn200() throws Exception {

        DoctorResponse response = new DoctorResponse(
                1L,
                "Sarah",
                "Smith",
                "Cardiology",
                "DOC-1001",
                "9876543210",
                "sarah@clinic.com",
                null,
                null
        );

        when(doctorService.getDoctor(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/doctors/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Sarah"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"))
                .andExpect(jsonPath("$.licenseNumber").value("DOC-1001"));
    }

    @Test
    void createDoctor_shouldReturn201() throws Exception{
        DoctorResponse response = new DoctorResponse(
                1L,
                "Sarah",
                "Smith",
                "Cardiology",
                "DOC-1001",
                "9876543210",
                "sarah@clinic.com",
                null,
                null
        );

        when(doctorService.createDoctor(any(CreateDoctorRequest.class)))
                .thenReturn(response);
        String requestBody = """
                {
                    "firstName": "Sarah",
                    "lastName": "Smith",
                    "specialization": "Cardiology",
                    "licenseNumber": "DOC-1001",
                    "phone": "9876543210",
                    "email": "sarah@clinic.com"
                }
                """;
        mockMvc.perform(
                        post("/api/v1/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)

                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Sarah"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"))
                .andExpect(jsonPath("$.licenseNumber").value("DOC-1001"));
    }

    @Test
    void createDoctor_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        String requestBody = """
                {
                    "firstName": "",
                    "lastName": "",
                    "specialization": "",
                    "licenseNumber": "",
                    "email": "invalid-email"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.lastName").exists())
                .andExpect(jsonPath("$.errors.specialization").exists())
                .andExpect(jsonPath("$.errors.licenseNumber").exists())
                .andExpect(jsonPath("$.errors.email").exists());
    }
    @Test
    void createDoctor_shouldReturn409_whenLicenseAlreadyExists()
            throws Exception {

        when(doctorService.createDoctor(
                org.mockito.ArgumentMatchers.any(CreateDoctorRequest.class)
        ))
                .thenThrow(
                        new DoctorAlreadyExistsException("DOC-1001")
                );

        String requestBody = """
                {
                    "firstName": "Sarah",
                    "lastName": "Smith",
                    "specialization": "Cardiology",
                    "licenseNumber": "DOC-1001"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Doctor already exists with license number: DOC-1001"
                        ));
    }
}
