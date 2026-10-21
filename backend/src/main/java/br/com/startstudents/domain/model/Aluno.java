package br.com.startstudents.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Modelo usado pelas regras de negócio, independente do banco e da API. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Aluno {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String cpf;
    private String telefone;
    private String foto;

    // Gerada automaticamente durante o cadastro.
    private String matricula;
    private StatusAluno status;

    // Permite exclusão lógica sem apagar o registro.
    private boolean usuarioExcluido;

    // Usada para identificar edições feitas sobre versões antigas.
    private Long version;
}
