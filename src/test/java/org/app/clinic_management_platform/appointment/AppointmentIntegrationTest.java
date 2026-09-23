package org.app.clinic_management_platform.appointment;

import org.app.clinic_management_platform.doctor.Doctor;
import org.app.clinic_management_platform.doctor.DoctorRepository;
import org.app.clinic_management_platform.patient.Patient;
import org.app.clinic_management_platform.patient.PatientRepository;
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


import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class AppointmentIntegrationTest {

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

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
    void createAppointment_thenGetAppointment_shouldWork()
            throws Exception {

        Patient patient = patientRepository.save(
                new Patient(
                        "Integration",
                        "Patient",
                        LocalDate.of(1990, 1, 1),
                        "Male",
                        "9000000001",
                        "integration.patient@test.com"
                )
        );

        Doctor doctor = doctorRepository.save(
                new Doctor(
                        "Integration",
                        "Doctor",
                        "Cardiology",
                        "INT-APP-DOC-1001",
                        "9000000002",
                        "integration.doctor@test.com"
                )
        );

        LocalDateTime appointmentDateTime =
                LocalDateTime.of(
                        2027,
                        1,
                        15,
                        10,
                        30
                );

        String requestBody = """
                {
                    "patientId": %d,
                    "doctorId": %d,
                    "appointmentDateTime": "%s",
                    "status": "SCHEDULED",
                    "reason": "Integration test appointment"
                }
                """.formatted(
                patient.getId(),
                doctor.getId(),
                appointmentDateTime
        );

        String response = mockMvc.perform(
                        post("/api/v1/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(
                        notNullValue()
                ))
                .andExpect(jsonPath("$.patientId")
                        .value(patient.getId()))
                .andExpect(jsonPath("$.doctorId")
                        .value(doctor.getId()))
                .andExpect(jsonPath("$.patientName")
                        .value("Integration Patient"))
                .andExpect(jsonPath("$.doctorName")
                        .value("Integration Doctor"))
                .andExpect(jsonPath("$.appointmentDateTime")
                        .value("2027-01-15T10:30:00"))
                .andExpect(jsonPath("$.status")
                        .value("SCHEDULED"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json =
                objectMapper
                        .readTree(response);

        long appointmentId =
                json.get("id").asLong();

        mockMvc.perform(
                        get("/api/v1/appointments/" + appointmentId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(appointmentId))
                .andExpect(jsonPath("$.patientId")
                        .value(patient.getId()))
                .andExpect(jsonPath("$.doctorId")
                        .value(doctor.getId()))
                .andExpect(jsonPath("$.status")
                        .value("SCHEDULED"));

        Appointment savedAppointment =
                appointmentRepository
                        .findById(appointmentId)
                        .orElseThrow();

        assertThat(savedAppointment.getPatient().getId())
                .isEqualTo(patient.getId());

        assertThat(savedAppointment.getDoctor().getId())
                .isEqualTo(doctor.getId());

        assertThat(savedAppointment.getAppointmentDateTime())
                .isEqualTo(appointmentDateTime);

        assertThat(savedAppointment.getStatus())
                .isEqualTo(AppointmentStatus.SCHEDULED);

        assertThat(savedAppointment.getReason())
                .isEqualTo("Integration test appointment");
    }

    @Test
    void createAppointment_shouldReturn409_whenSameDoctorAndTimeExists()
            throws Exception {

        Patient patient = patientRepository.save(
                new Patient(
                        "Duplicate",
                        "Patient",
                        LocalDate.of(1992, 5, 10),
                        "Female",
                        "9000000011",
                        "duplicate.patient@test.com"
                )
        );

        Doctor doctor = doctorRepository.save(
                new Doctor(
                        "Duplicate",
                        "Doctor",
                        "Neurology",
                        "INT-APP-DOC-1002",
                        "9000000012",
                        "duplicate.doctor@test.com"
                )
        );

        LocalDateTime appointmentDateTime =
                LocalDateTime.of(
                        2027,
                        2,
                        20,
                        14,
                        0
                );

        String firstRequest = """
                {
                    "patientId": %d,
                    "doctorId": %d,
                    "appointmentDateTime": "%s",
                    "status": "SCHEDULED",
                    "reason": "First appointment"
                }
                """.formatted(
                patient.getId(),
                doctor.getId(),
                appointmentDateTime
        );

        mockMvc.perform(
                        post("/api/v1/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(firstRequest)
                )
                .andExpect(status().isCreated());

        String secondRequest = """
                {
                    "patientId": %d,
                    "doctorId": %d,
                    "appointmentDateTime": "%s",
                    "status": "SCHEDULED",
                    "reason": "Second appointment"
                }
                """.formatted(
                patient.getId(),
                doctor.getId(),
                appointmentDateTime
        );

        mockMvc.perform(
                        post("/api/v1/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(secondRequest)
                )
                .andExpect(status().isConflict());
    }
}