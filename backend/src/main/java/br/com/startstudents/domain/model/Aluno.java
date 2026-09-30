package br.com.startstudents.domain.model;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Aluno {
 private Long id;
 private String nomeCompleto;
 private String email;
 private String cpf;
 private String telefone;
 private String foto;
 private String matricula;
 private StatusAluno status;
 private boolean usuarioExcluido;
}