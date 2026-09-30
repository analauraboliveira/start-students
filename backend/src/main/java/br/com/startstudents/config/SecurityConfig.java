package br.com.startstudents.config;
import br.com.startstudents.auth.JwtFiltro;import jakarta.servlet.http.HttpServletResponse;import org.springframework.context.annotation.*;import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;import org.springframework.security.config.annotation.web.builders.HttpSecurity;import org.springframework.security.config.http.SessionCreationPolicy;import org.springframework.security.web.*;import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;import org.springframework.web.cors.*;import java.io.IOException;import java.util.List;

// configura as rotas publicas, JWT e permissoes
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