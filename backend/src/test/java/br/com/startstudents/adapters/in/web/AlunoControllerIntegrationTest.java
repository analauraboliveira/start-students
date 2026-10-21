package br.com.startstudents.adapters.in.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import br.com.startstudents.auth.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Exercita as rotas HTTP e as permissões de acesso com o Spring iniciado. */
@SpringBootTest
@AutoConfigureMockMvc
class AlunoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String tokenAdministrador() {
        return "Bearer " + jwtService.gerar("adminuser", "ADMINISTRADOR");
    }

    private String tokenLeitor() {
        return "Bearer " + jwtService.gerar("leitoruser", "LEITOR");
    }

    @Test
    void rotaSemSessaoRetorna401() throws Exception {
        mockMvc.perform(get("/api/alunos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void leitorPodeListar() throws Exception {
        mockMvc.perform(get("/api/alunos").header("Authorization", tokenLeitor()))
                .andExpect(status().isOk());
    }

    @Test
    void leitorNaoPodeExcluir() throws Exception {
        mockMvc.perform(delete("/api/alunos/1").header("Authorization", tokenLeitor()))
                .andExpect(status().isForbidden());
    }

    @Test
    void leitorNaoPodeCadastrar() throws Exception {
        String dadosCadastro = """
                {"nomeCompleto":"Maria Silva","email":"maria.nova@email.com",
                 "cpf":"52998224725","telefone":"81999999999"}
                """;

        mockMvc.perform(post("/api/alunos")
                        .header("Authorization", tokenLeitor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosCadastro))
                .andExpect(status().isForbidden());
    }

    @Test
    void leitorNaoPodeEditar() throws Exception {
        // Busca um aluno existente para verificar que o leitor não consegue alterá-lo.
        String respostaLista = mockMvc.perform(get("/api/alunos")
                        .header("Authorization", tokenAdministrador()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectMapper conversorJson = new ObjectMapper();
        JsonNode alunoExistente = conversorJson.readTree(respostaLista).path("content").get(0);
        String enderecoAluno = "/api/alunos/" + alunoExistente.path("id").asLong();

        String dadosAntes = mockMvc.perform(get(enderecoAluno)
                        .header("Authorization", tokenAdministrador()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        ObjectNode alteracaoProibida = conversorJson.readTree(dadosAntes).deepCopy();
        alteracaoProibida.put("nomeCompleto", "Alteracao Proibida");

        mockMvc.perform(put(enderecoAluno)
                        .header("Authorization", tokenLeitor())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conversorJson.writeValueAsString(alteracaoProibida)))
                .andExpect(status().isForbidden());

        String dadosDepois = mockMvc.perform(get(enderecoAluno)
                        .header("Authorization", tokenAdministrador()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(conversorJson.readTree(dadosAntes), conversorJson.readTree(dadosDepois));
    }

    @Test
    void buscaPorNomeEhCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/alunos")
                        .param("nome", "aNa")
                        .header("Authorization", tokenAdministrador()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void alunoInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/alunos/999999")
                        .header("Authorization", tokenAdministrador()))
                .andExpect(status().isNotFound());
    }

    @Test
    void payloadInvalidoRetorna422() throws Exception {
        String dadosInvalidos = """
                {"nomeCompleto":"A","email":"email-invalido","cpf":"123","telefone":"1"}
                """;

        mockMvc.perform(post("/api/alunos")
                        .header("Authorization", tokenAdministrador())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(dadosInvalidos))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void loginInvalidoRetorna401ComMensagemGenerica() throws Exception {
        String credenciaisInvalidas = """
                {"usuario":"usuario99","senha":"senha999"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(credenciaisInvalidas))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Usuário ou senha inválidos."));
    }
}
