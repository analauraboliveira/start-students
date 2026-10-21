package br.com.startstudents.application.ports.in;

import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/** Operações que o controller pode solicitar às regras de negócio. */
public interface GerenciarAlunoUseCase {

    Page<Aluno> listar(String nome, String matricula, StatusAluno status, Pageable paginacao);

    Aluno buscar(Long identificadorAluno);

    /** Valida os dados, gera matrícula e cadastra um novo aluno. */
    Aluno cadastrar(Aluno novoAluno);

    Aluno editar(Long identificadorAluno, Aluno dadosAtualizados);

    void inativar(Long identificadorAluno);
}
