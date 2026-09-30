package br.com.startstudents.adapters.in.web;
import br.com.startstudents.auth.JwtService;import org.junit.jupiter.api.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.test.web.servlet.MockMvc;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class AlunoControllerIntegrationTest {
 @Autowired MockMvc mvc; @Autowired JwtService jwt;
 @Test void rotaSemSessaoRetorna401()throws Exception{mvc.perform(get("/api/alunos")).andExpect(status().isUnauthorized());}
 @Test void leitorPodeListar()throws Exception{mvc.perform(get("/api/alunos").header("Authorization","Bearer "+jwt.gerar("leitor123","LEITOR"))).andExpect(status().isOk());}
 @Test void leitorNaoPodeExcluir()throws Exception{mvc.perform(delete("/api/alunos/1").header("Authorization","Bearer "+jwt.gerar("leitor123","LEITOR"))).andExpect(status().isForbidden());}
}