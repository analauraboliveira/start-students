package br.com.startstudents.config;
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

import br.com.startstudents.auth.JwtFiltro;
import jakarta.servlet.http.HttpServletResponse;


// configura JWT, CORS e quais rotas precisam de login
// configura as rotas públicas, JWT e permissões
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean SecurityFilterChain filter(HttpSecurity h,JwtFiltro jwt)throws Exception{
  h.csrf(c->c.disable()).cors(c->c.configurationSource(cors())).headers(x->x.frameOptions(f->f.sameOrigin()))
  .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
  .exceptionHandling(e->e.authenticationEntryPoint((q,r,x)->json(r,401,"Não autenticado.")).accessDeniedHandler((q,r,x)->json(r,403,"Você não tem permissão para esta ação.")))
  .authorizeHttpRequests(a->a.requestMatchers("/api/auth/login","/h2-console/**").permitAll().anyRequest().authenticated())
  .addFilterBefore(jwt,UsernamePasswordAuthenticationFilter.class);
  return h.build();
 }
 private void json(HttpServletResponse r,int status,String mensagem)throws IOException{r.setStatus(status);r.setContentType("application/json");r.getWriter().write("{\"mensagem\":\""+mensagem+"\"}");}
 @Bean CorsConfigurationSource cors(){CorsConfiguration c=new CorsConfiguration();c.setAllowedOrigins(List.of("http://localhost:4200"));c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("*"));UrlBasedCorsConfigurationSource s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
}