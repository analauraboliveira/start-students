package br.com.startstudents.adapters.in.web;

import br.com.startstudents.domain.exception.AlunoNaoEncontradoException;
import br.com.startstudents.domain.exception.ConflitoException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Transforma erros do backend em respostas que o frontend consegue mostrar. */
@RestControllerAdvice
public class TratadorGlobalException {

    @ExceptionHandler(AlunoNaoEncontradoException.class)
    ResponseEntity<?> naoEncontrado(AlunoNaoEncontradoException erro) {
        return resposta(404, erro.getMessage());
    }

    @ExceptionHandler({
            ConflitoException.class,
            ObjectOptimisticLockingFailureException.class,
            DataIntegrityViolationException.class
    })
    ResponseEntity<?> conflito(Exception erro) {
        return resposta(409, "Existe um conflito com os dados informados.");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<?> requisicaoMalformada(HttpMessageNotReadableException erro) {
        return resposta(400, "Requisição malformada.");
    }

    /** Devolve os erros de cada campo para o formulário mostrar ao usuário. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validacao(MethodArgumentNotValidException erro) {
        Map<String, String> camposInvalidos = new LinkedHashMap<>();

        erro.getBindingResult().getFieldErrors().forEach(erroCampo ->
                camposInvalidos.putIfAbsent(erroCampo.getField(), erroCampo.getDefaultMessage()));

        return ResponseEntity.unprocessableEntity().body(Map.of(
                "mensagem", "Confira os campos informados.",
                "campos", camposInvalidos
        ));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    ResponseEntity<?> semPermissao(AuthorizationDeniedException erro) {
        return resposta(403, "Você não tem permissão para esta ação.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> dadosInvalidos(IllegalArgumentException erro) {
        return resposta(422, erro.getMessage());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<?> erroInterno(Exception erro) {
        return resposta(500, "Não foi possível concluir a operação.");
    }

    private ResponseEntity<?> resposta(int status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("mensagem", mensagem));
    }
}
