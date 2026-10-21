package br.com.startstudents.adapters.in.web.dto;

import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;

/** Formato dos dados devolvidos pela API para o frontend. */
public record AlunoResponse(
        Long id,
        String nomeCompleto,
        String email,
        String cpf,
        String telefone,
        String foto,
        String matricula,
        StatusAluno status,
        Long version
) {
    /** Converte o modelo de domínio para a resposta HTTP. */
    public static AlunoResponse de(Aluno aluno) {
        return new AlunoResponse(
                aluno.getId(),
                aluno.getNomeCompleto(),
                aluno.getEmail(),
                aluno.getCpf(),
                aluno.getTelefone(),
                aluno.getFoto(),
                aluno.getMatricula(),
                aluno.getStatus(),
                aluno.getVersion()
        );
    }
}
