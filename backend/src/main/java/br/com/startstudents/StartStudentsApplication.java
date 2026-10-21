package br.com.startstudents;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/** Ponto de entrada da API Spring Boot. */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class StartStudentsApplication {

    public static void main(String[] args) {
        // O projeto usa JWT próprio e dispensa o usuário padrão do Spring.
        SpringApplication.run(StartStudentsApplication.class, args);
    }
}
