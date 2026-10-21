package br.com.startstudents.domain.exception;

/** Indica que o aluno solicitado não existe ou foi excluído logicamente. */
public class AlunoNaoEncontradoException extends RuntimeException {

    public AlunoNaoEncontradoException() {
        super("Aluno não encontrado.");
    }
}
