package org.app.clinic_management_platform.doctor;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class DoctorIntegrationTest {

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DoctorRepository doctorRepository;

    @DynamicPropertySource
    static void configureProperties(
            DynamicPropertyRegistry registry
    ) {
        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                postgres::getUsername
        );

        registry.add(
                "spring.datasource.password",
                postgres::getPassword
        );
    }

    @Test
    void createDoctor_thenGetDoctor_shouldWork()
            throws Exception {

        String requestBody = """
                {
                    "firstName": "Integration",
                    "lastName": "Doctor",
                    "specialization": "Cardiology",
                    "licenseNumber": "INT-DOC-1001",
                    "phone": "9876543210",
                    "email": "integration.doctor@test.com"
                }
                """;

        String response = mockMvc.perform(
                        post("/api/v1/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(
                        org.hamcrest.Matchers.notNullValue()
                ))
                .andExpect(jsonPath("$.firstName")
                        .value("Integration"))
                .andExpect(jsonPath("$.lastName")
                        .value("Doctor"))
                .andExpect(jsonPath("$.licenseNumber")
                        .value("INT-DOC-1001"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        long doctorId = json.get("id").asLong();

        mockMvc.perform(
                        get("/api/v1/doctors/" + doctorId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(doctorId))
                .andExpect(jsonPath("$.firstName")
                        .value("Integration"))
                .andExpect(jsonPath("$.specialization")
                        .value("Cardiology"));

        Doctor savedDoctor = doctorRepository
                .findById(doctorId)
                .orElseThrow();

        assertThat(savedDoctor.getFirstName())
                .isEqualTo("Integration");

        assertThat(savedDoctor.getLicenseNumber())
                .isEqualTo("INT-DOC-1001");

        assertThat(savedDoctor.getSpecialization())
                .isEqualTo("Cardiology");
    }

    @Test
    void createDoctor_shouldReturn409_whenLicenseAlreadyExists()
            throws Exception {

        String firstRequest = """
            {
                "firstName": "First",
                "lastName": "Doctor",
                "specialization": "Cardiology",
                "licenseNumber": "DUPLICATE-1001",
                "email": "first@test.com"
            }
            """;

        mockMvc.perform(
                        post("/api/v1/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(firstRequest)
                )
                .andExpect(status().isCreated());

        String secondRequest = """
            {
                "firstName": "Second",
                "lastName": "Doctor",
                "specialization": "Neurology",
                "licenseNumber": "DUPLICATE-1001",
                "email": "second@test.com"
            }
            """;

        mockMvc.perform(
                        post("/api/v1/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondRequest)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Doctor already exists with license number: DUPLICATE-1001"
                        ));
    }
}