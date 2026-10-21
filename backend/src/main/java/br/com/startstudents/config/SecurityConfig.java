package br.com.startstudents.config;

import br.com.startstudents.auth.JwtFiltro;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/** Configura JWT, CORS e as permissões das rotas. */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filter(HttpSecurity seguranca, JwtFiltro filtroJwt) throws Exception {
        seguranca
                .csrf(configuracao -> configuracao.disable())
                .cors(configuracao -> configuracao.configurationSource(cors()))
                .headers(configuracao -> configuracao.frameOptions(opcoes -> opcoes.sameOrigin()))
                .sessionManagement(configuracao ->
                        configuracao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configuracao -> configuracao
                        .authenticationEntryPoint((requisicao, resposta, erro) ->
                                responderJson(resposta, 401, "Não autenticado."))
                        .accessDeniedHandler((requisicao, resposta, erro) ->
                                responderJson(resposta, 403, "Você não tem permissão para esta ação.")))
                .authorizeHttpRequests(configuracao -> configuracao
                        .requestMatchers("/api/auth/login", "/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(filtroJwt, UsernamePasswordAuthenticationFilter.class);

        return seguranca.build();
    }

    /** Mantém as respostas de autenticação em JSON para o frontend. */
    private void responderJson(HttpServletResponse resposta, int status, String mensagem)
            throws IOException {
        resposta.setStatus(status);
        resposta.setContentType("application/json");
        resposta.getWriter().write("{\"mensagem\":\"" + mensagem + "\"}");
    }

    @Bean
    CorsConfigurationSource cors() {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOrigins(List.of("http://localhost:4200"));
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource origemCors = new UrlBasedCorsConfigurationSource();
        origemCors.registerCorsConfiguration("/**", configuracao);
        return origemCors;
    }
}
