package br.com.startstudents.adapters.in.web;
import br.com.startstudents.adapters.in.web.dto.*;
import br.com.startstudents.application.ports.in.GerenciarAlunoUseCase;
import br.com.startstudents.domain.model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// recebe as requisicoes da tela e chama os casos de uso
@RestController @RequestMapping("/api/alunos") @RequiredArgsConstructor
public class AlunoController {
 private final GerenciarAlunoUseCase useCase;
 @GetMapping @PreAuthorize("hasAnyRole('ADMINISTRADOR','LEITOR')")
 public Page<AlunoResponse> listar(@RequestParam(required=false)String nome,@RequestParam(required=false)String matricula,@RequestParam(required=false)StatusAluno status,@PageableDefault(size=10,sort="nomeCompleto",direction=Sort.Direction.ASC)Pageable p){return useCase.listar(nome,matricula,status,p).map(AlunoResponse::de);}
 @GetMapping("/{id}") @PreAuthorize("hasAnyRole('ADMINISTRADOR','LEITOR')")
 public AlunoResponse buscar(@PathVariable Long id){return AlunoResponse.de(useCase.buscar(id));}
 @PostMapping @PreAuthorize("hasRole('ADMINISTRADOR')")
 public ResponseEntity<AlunoResponse> cadastrar(@Valid @RequestBody AlunoRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(AlunoResponse.de(useCase.cadastrar(mapear(r))));}
 @PutMapping("/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
 public AlunoResponse editar(@PathVariable Long id,@Valid @RequestBody AlunoRequest r){return AlunoResponse.de(useCase.editar(id,mapear(r)));}
 @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMINISTRADOR')")
 public ResponseEntity<Void> excluir(@PathVariable Long id){useCase.inativar(id);return ResponseEntity.noContent().build();}
 private Aluno mapear(AlunoRequest r){return Aluno.builder().nomeCompleto(r.nomeCompleto()).email(r.email()).cpf(r.cpf()).telefone(r.telefone()).foto(r.foto()).status(r.status()).version(r.version()).build();}
}