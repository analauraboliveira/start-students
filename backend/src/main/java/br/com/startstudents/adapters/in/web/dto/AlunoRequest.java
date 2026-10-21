package br.com.startstudents.adapters.in.web.dto;

import br.com.startstudents.domain.model.StatusAluno;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dados recebidos pelo cadastro e pela edição.
 * O Spring valida estes campos antes de chamar o controller.
 */
public record AlunoRequest(
        @NotBlank
        @Size(min = 3, max = 120)
        @Pattern(regexp = "^[\\p{L} .'-]+$")
        String nomeCompleto,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String cpf,

        @NotBlank
        @Pattern(regexp = "\\D*(?:\\d\\D*){10,11}")
        String telefone,

        // A foto é opcional e chega como uma data URL.
        String foto,

        // No cadastro, o serviço define o status inicial como ATIVO.
        StatusAluno status,

        // Na edição, permite verificar se os dados estão desatualizados.
        Long version
) {
}
