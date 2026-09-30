package br.com.startstudents.adapters.in.web;
import br.com.startstudents.auth.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest @AutoConfigureMockMvc
class AlunoControllerIntegrationTest {
 @Autowired MockMvc mvc; @Autowired JwtService jwt;
 private String admin(){return "Bearer "+jwt.gerar("adminuser","ADMINISTRADOR");}
 private String leitor(){return "Bearer "+jwt.gerar("leitoruser","LEITOR");}
 @Test void rotaSemSessaoRetorna401()throws Exception{mvc.perform(get("/api/alunos")).andExpect(status().isUnauthorized());}
 @Test void leitorPodeListar()throws Exception{mvc.perform(get("/api/alunos").header("Authorization",leitor())).andExpect(status().isOk());}
 @Test void leitorNaoPodeExcluir()throws Exception{mvc.perform(delete("/api/alunos/1").header("Authorization",leitor())).andExpect(status().isForbidden());}
 @Test void leitorNaoPodeCadastrar()throws Exception{mvc.perform(post("/api/alunos").header("Authorization",leitor()).contentType(MediaType.APPLICATION_JSON).content("{\"nomeCompleto\":\"Maria Silva\",\"email\":\"maria.nova@email.com\",\"cpf\":\"52998224725\",\"telefone\":\"81999999999\"}")).andExpect(status().isForbidden());}
 @Test void leitorNaoPodeEditar()throws Exception{
  // Captura um aluno existente para verificar que a tentativa não altera dados.
  String lista=mvc.perform(get("/api/alunos").header("Authorization",admin())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  com.fasterxml.jackson.databind.ObjectMapper mapper=new com.fasterxml.jackson.databind.ObjectMapper();
  com.fasterxml.jackson.databind.JsonNode aluno=mapper.readTree(lista).path("content").get(0);
  String url="/api/alunos/"+aluno.path("id").asLong();
  String antes=mvc.perform(get(url).header("Authorization",admin())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  com.fasterxml.jackson.databind.node.ObjectNode payload=mapper.readTree(antes).deepCopy();
  payload.put("nomeCompleto","Alteracao Proibida");
  mvc.perform(put(url).header("Authorization",leitor()).contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(payload))).andExpect(status().isForbidden());
  String depois=mvc.perform(get(url).header("Authorization",admin())).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
  org.junit.jupiter.api.Assertions.assertEquals(mapper.readTree(antes),mapper.readTree(depois));
 }
 @Test void buscaPorNomeEhCaseInsensitive()throws Exception{mvc.perform(get("/api/alunos").param("nome","aNa").header("Authorization",admin())).andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray());}
 @Test void alunoInexistenteRetorna404()throws Exception{mvc.perform(get("/api/alunos/999999").header("Authorization",admin())).andExpect(status().isNotFound());}
 @Test void payloadInvalidoRetorna422()throws Exception{mvc.perform(post("/api/alunos").header("Authorization",admin()).contentType(MediaType.APPLICATION_JSON).content("{\"nomeCompleto\":\"A\",\"email\":\"email-invalido\",\"cpf\":\"123\",\"telefone\":\"1\"}")).andExpect(status().isUnprocessableEntity());}
 @Test void loginInvalidoRetorna401ComMensagemGenerica()throws Exception{mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON).content("{\"usuario\":\"usuario99\",\"senha\":\"senha999\"}")).andExpect(status().isUnauthorized()).andExpect(jsonPath("$.mensagem").value("Usuário ou senha inválidos."));}
 @Test void leitorNaoPodeEditar()throws Exception{mvc.perform(put("/api/alunos/1").header("Authorization",leitor()).contentType(MediaType.APPLICATION_JSON).content("{\\"nomeCompleto\\":\\"William Leal Gomes\\"}")).andExpect(status().isForbidden());}
}