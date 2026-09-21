CREATE TABLE appointments (
      id BIGSERIAL PRIMARY KEY,

      patient_id BIGINT NOT NULL,
      doctor_id BIGINT NOT NULL,

      appointment_date_time TIMESTAMP NOT NULL,

      status VARCHAR(30) NOT NULL,

      reason VARCHAR(500),

      created_at TIMESTAMP NOT NULL,
      updated_at TIMESTAMP NOT NULL,

      CONSTRAINT fk_appointments_patient
          FOREIGN KEY (patient_id)
              REFERENCES patients(id),

      CONSTRAINT fk_appointments_doctor
          FOREIGN KEY (doctor_id)
              REFERENCES doctors(id)
);

CREATE INDEX idx_appointments_patient_id
    ON appointments(patient_id);

CREATE INDEX idx_appointments_doctor_id
    ON appointments(doctor_id);

CREATE INDEX idx_appointments_doctor_datetime
    ON appointments(doctor_id, appointment_date_time);