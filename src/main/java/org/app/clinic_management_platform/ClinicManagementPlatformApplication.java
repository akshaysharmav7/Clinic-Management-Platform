package org.app.clinic_management_platform;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@EnableJpaAuditing
@SpringBootApplication
public class ClinicManagementPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicManagementPlatformApplication.class, args);
    }

}
