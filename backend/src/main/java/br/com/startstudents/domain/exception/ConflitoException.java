package br.com.startstudents.domain.exception;

/** Indica conflito de CPF, e-mail ou versão de um registro. */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
