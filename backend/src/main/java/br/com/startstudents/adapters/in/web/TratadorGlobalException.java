package br.com.startstudents.adapters.in.web;
import br.com.startstudents.domain.exception.*;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
// transforma os erros em respostas HTTP faceis de tratar no front
@RestControllerAdvice
public class TratadorGlobalException {
 @ExceptionHandler(AlunoNaoEncontradoException.class) ResponseEntity<?> naoEncontrado(AlunoNaoEncontradoException e){return resposta(404,e.getMessage());}
 @ExceptionHandler({ConflitoException.class,ObjectOptimisticLockingFailureException.class,DataIntegrityViolationException.class}) ResponseEntity<?> conflito(Exception e){return resposta(409,"Existe um conflito com os dados informados.");}
 // json quebrado ou que nao pode ser lido
 @ExceptionHandler(HttpMessageNotReadableException.class) ResponseEntity<?> malformada(HttpMessageNotReadableException e){return resposta(400,"Requisição malformada.");}
 // validacao dos campos vira 422
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<?> validacao(MethodArgumentNotValidException e){
  Map<String,String> campos=new LinkedHashMap<>();
  e.getBindingResult().getFieldErrors().forEach(x->campos.putIfAbsent(x.getField(),x.getDefaultMessage()));
  return ResponseEntity.unprocessableEntity().body(Map.of("mensagem","Confira os campos informados.","campos",campos));
 }
 @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<?> semantico(IllegalArgumentException e){return resposta(422,e.getMessage());}
 @ExceptionHandler(Exception.class) ResponseEntity<?> interno(Exception e){return resposta(500,"Não foi possível concluir a operação.");}
 private ResponseEntity<?> resposta(int status,String mensagem){return ResponseEntity.status(status).body(Map.of("mensagem",mensagem));}
}