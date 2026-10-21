package br.com.startstudents.application.ports.out;

import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Contrato de acesso aos alunos sem depender diretamente do JPA. */
public interface AlunoRepositoryPort {

    Page<Aluno> listar(String nome, String matricula, StatusAluno status, Pageable paginacao);

    Optional<Aluno> buscarPorId(Long identificadorAluno);

    Aluno salvar(Aluno aluno);

    void inativar(Long identificadorAluno);

    boolean existeCpf(String cpf, Long identificadorIgnorado);

    boolean existeEmail(String email, Long identificadorIgnorado);

    boolean existeMatricula(String matricula);
}
