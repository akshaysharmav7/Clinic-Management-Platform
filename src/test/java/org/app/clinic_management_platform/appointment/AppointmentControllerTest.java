package org.app.clinic_management_platform.appointment;

import org.app.clinic_management_platform.error.GlobalExceptionHandler;
import org.app.clinic_management_platform.patient.PatientNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AppointmentController.class)
@Import(GlobalExceptionHandler.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AppointmentService appointmentService;

    @Test
    void createAppointment_shouldReturn201() throws Exception {

        AppointmentResponse response =
                new AppointmentResponse(
                        1L,
                        10L,
                        "John Patient",
                        20L,
                        "Jane Doctor",
                        LocalDateTime.of(
                                2026,
                                9,
                                25,
                                10,
                                30
                        ),
                        AppointmentStatus.SCHEDULED,
                        "Regular consultation",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(appointmentService.createAppointment(any()))
                .thenReturn(response);

        String requestBody = """
                {
                    "patientId": 10,
                    "doctorId": 20,
                    "appointmentDateTime": "2026-09-25T10:30:00",
                    "status": "SCHEDULED",
                    "reason": "Regular consultation"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(10))
                .andExpect(jsonPath("$.patientName")
                        .value("John Patient"))
                .andExpect(jsonPath("$.doctorId").value(20))
                .andExpect(jsonPath("$.doctorName")
                        .value("Jane Doctor"))
                .andExpect(jsonPath("$.status")
                        .value("SCHEDULED"));
    }

    @Test
    void createAppointment_shouldReturn400_whenRequestIsInvalid()
            throws Exception {

        String requestBody = """
                {
                    "patientId": null,
                    "doctorId": null,
                    "appointmentDateTime": null,
                    "status": null
                }
                """;

        mockMvc.perform(
                        post("/api/v1/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAppointment_shouldReturn400_whenDateIsInPast()
            throws Exception {

        String requestBody = """
                {
                    "patientId": 10,
                    "doctorId": 20,
                    "appointmentDateTime": "2020-01-01T10:30:00",
                    "status": "SCHEDULED"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createAppointment_shouldReturn404_whenPatientDoesNotExist()
            throws Exception {

        when(appointmentService.createAppointment(any()))
                .thenThrow(new PatientNotFoundException(999L));

        String requestBody = """
                {
                    "patientId": 999,
                    "doctorId": 20,
                    "appointmentDateTime": "2026-09-25T10:30:00",
                    "status": "SCHEDULED"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createAppointment_shouldReturn409_whenSlotAlreadyExists()
            throws Exception {

        when(appointmentService.createAppointment(any()))
                .thenThrow(
                        new AppointmentAlreadyExistsException(
                                20L,
                                LocalDateTime.of(
                                        2026,
                                        9,
                                        25,
                                        10,
                                        30
                                )
                        )
                );

        String requestBody = """
                {
                    "patientId": 10,
                    "doctorId": 20,
                    "appointmentDateTime": "2026-09-25T10:30:00",
                    "status": "SCHEDULED"
                }
                """;

        mockMvc.perform(
                        post("/api/v1/appointments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void getAppointment_shouldReturn200() throws Exception {

        AppointmentResponse response =
                new AppointmentResponse(
                        1L,
                        10L,
                        "John Patient",
                        20L,
                        "Jane Doctor",
                        LocalDateTime.of(
                                2026,
                                9,
                                25,
                                10,
                                30
                        ),
                        AppointmentStatus.SCHEDULED,
                        "Regular consultation",
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );

        when(appointmentService.getAppointment(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/v1/appointments/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patientId").value(10))
                .andExpect(jsonPath("$.doctorId").value(20))
                .andExpect(jsonPath("$.status")
                        .value("SCHEDULED"));
    }

    @Test
    void getAppointment_shouldReturn404_whenNotFound()
            throws Exception {

        when(appointmentService.getAppointment(999L))
                .thenThrow(new AppointmentNotFoundException(999L));

        mockMvc.perform(
                        get("/api/v1/appointments/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}