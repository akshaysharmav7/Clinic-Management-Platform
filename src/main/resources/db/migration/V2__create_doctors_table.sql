CREATE TABLE doctors (
     id BIGSERIAL PRIMARY KEY,
     first_name VARCHAR(100) NOT NULL,
     last_name VARCHAR(100) NOT NULL,
     specialization VARCHAR(100) NOT NULL,
     license_number VARCHAR(50) NOT NULL UNIQUE,
     phone VARCHAR(30),
     email VARCHAR(255),
     created_at TIMESTAMP NOT NULL,
     updated_at TIMESTAMP NOT NULL
);