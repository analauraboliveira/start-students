package br.com.startstudents;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

// o projeto usa autenticacao JWT propria, então cria usuário padrão do Spring
@SpringBootApplication(exclude=UserDetailsServiceAutoConfiguration.class)
public class StartStudentsApplication {
 public static void main(String[] args){SpringApplication.run(StartStudentsApplication.class,args);}
}