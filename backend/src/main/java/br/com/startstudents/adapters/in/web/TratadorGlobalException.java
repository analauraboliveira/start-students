package br.com.startstudents.adapters.in.web;
import br.com.startstudents.domain.exception.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;


// transforma os erros em respostas HTTP mais faceis de tratar no front
// transforma erros da aplicacao em respostas HTTP mais claras
@RestControllerAdvice
public class TratadorGlobalException {
 @ExceptionHandler(AlunoNaoEncontradoException.class) ResponseEntity<?> naoEncontrado(AlunoNaoEncontradoException e){return resposta(404,e.getMessage());}
 @ExceptionHandler(ConflitoException.class) ResponseEntity<?> conflito(ConflitoException e){return resposta(409,e.getMessage());}
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<?> semantico(IllegalArgumentException e){return resposta(422,e.getMessage());}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validacao(MethodArgumentNotValidException e){Map<String,String> campos=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->campos.putIfAbsent(x.getField(),x.getDefaultMessage()));return ResponseEntity.badRequest().body(Map.of("mensagem","Confira os campos informados.","campos",campos));}
 @ExceptionHandler(Exception.class) ResponseEntity<?> interno(Exception e){return resposta(500,"Não foi possível concluir a operação.");}
 private ResponseEntity<?> resposta(int status,String mensagem){return ResponseEntity.status(status).body(Map.of("mensagem",mensagem));}
}