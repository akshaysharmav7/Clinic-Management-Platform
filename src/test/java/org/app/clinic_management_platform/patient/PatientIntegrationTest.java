package org.app.clinic_management_platform.patient;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class PatientIntegrationTest {

    @Container
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;


    @Test
    void createPatient_thenGetPatient_shouldWork() throws Exception{
        String requestBody = """
                {
                    "firstName": "Integration",
                    "lastName": "Test",
                    "dateOfBirth": "1990-01-01",
                    "gender": "Male",
                    "phone": "9876543210",
                    "email": "integration@test.com"
                }
                """;
        String response = mockMvc.perform(
                post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Integration"))
                .andExpect(jsonPath("$.lastName").value("Test"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);

        long patientId = json.get("id").asLong();

        Patient savedPatient = patientRepository.findById(patientId)
                        .orElseThrow();
        assertThat(savedPatient.getFirstName()).isEqualTo("Integration");
        assertThat(savedPatient.getLastName()).isEqualTo("Test");
        assertThat(savedPatient.getEmail()).isEqualTo("integration@test.com");

        mockMvc.perform(
                        get("/api/v1/patients/" + patientId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId))
                .andExpect(jsonPath("$.firstName").value("Integration"))
                .andExpect(jsonPath("$.lastName").value("Test"))
                .andExpect(jsonPath("$.email").value("integration@test.com"));

    }

    @Test
    void updatePatient_shouldPersistChanges() throws Exception{
        String createReqeust = """
            {
                "firstName": "Before",
                "lastName": "Update",
                "dateOfBirth": "1990-01-01",
                "gender": "Male",
                "phone": "1111111111",
                "email": "before@test.com"
            }
            """;
        String createResponse = mockMvc.perform(
                post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createReqeust)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createJson = objectMapper.readTree(createResponse);

        long patientId = createJson.get("id").asLong();

        String updateRequest = """
                {
                    "firstName": "After",
                    "lastName": "Updated",
                    "dateOfBirth": "1990-01-01",
                    "gender": "Female",
                    "phone": "2222222222",
                    "email": "after@test.com"
                }
                """;
        mockMvc.perform(
                put("/api/v1/patients/" + patientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(patientId))
                .andExpect(jsonPath("$.firstName").value("After"))
                .andExpect(jsonPath("$.lastName").value("Updated"))
                .andExpect(jsonPath("$.email").value("after@test.com"));

        Patient updatedPatient = patientRepository.findById(patientId)
                .orElseThrow();
        assertThat(updatedPatient.getFirstName()).isEqualTo("After");
        assertThat(updatedPatient.getLastName()).isEqualTo("Updated");
        assertThat(updatedPatient.getGender()).isEqualTo("Female");
        assertThat(updatedPatient.getPhone()).isEqualTo("2222222222");
        assertThat(updatedPatient.getEmail()).isEqualTo("after@test.com");
    }

    @Test
    void updatePatient_shouldReturn404_whenPatientDoesNotExist()
            throws Exception {

        String updateRequest = """
            {
                "firstName": "Missing",
                "lastName": "Patient",
                "dateOfBirth": "1990-01-01",
                "email": "missing@test.com"
            }
            """;

        mockMvc.perform(
                        put("/api/v1/patients/999999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateRequest)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Patient not found with id: 999999"));
    }

    @Test
    void deletePatient_shouldRemovePatientFromDatabase() throws Exception{

        String createRequest = """
            {
                "firstName": "Delete",
                "lastName": "Test",
                "dateOfBirth": "1990-01-01",
                "email": "delete@test.com"
            }
            """;
        String createResponse = mockMvc.perform(
                post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createRequest)
        )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createJson = objectMapper.readTree(createResponse);
        long patientId = createJson.get("id").asLong();

        assertThat(patientRepository.findById(patientId))
                .isPresent();

        mockMvc.perform(
                delete("/api/v1/patients/"+patientId)
        )
                .andExpect(status().isNoContent());
        assertThat(patientRepository.findById(patientId))
                .isEmpty();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

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
}
