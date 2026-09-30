package br.com.startstudents.adapters.out.persistence;
import br.com.startstudents.application.ports.out.AlunoRepositoryPort;
import br.com.startstudents.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import java.util.Optional;

// faz a ponte entre a regra de negocio e o JPA
@Component @RequiredArgsConstructor
public class AlunoRepositoryAdapter implements AlunoRepositoryPort {
 private final AlunoJpaRepository jpa;

 // converte as entidades do banco para o modelo do dominio
 public Page<Aluno> listar(String n,String m,StatusAluno s,Pageable p){return jpa.filtrar(n,m,s,p).map(this::dominio);}
 public Optional<Aluno> buscarPorId(Long id){return jpa.findById(id).map(this::dominio);}

 // antes de salvar, converte o modelo para entidade JPA
 public Aluno salvar(Aluno a){return dominio(jpa.save(entidade(a)));}
 public boolean existeCpf(String v,Long id){return jpa.existeCpf(v,id);}
 public boolean existeEmail(String v,Long id){return jpa.existeEmail(v,id);}
 public boolean existeMatricula(String v){return jpa.existsByMatricula(v);}

 private Aluno dominio(AlunoEntity e){return Aluno.builder().id(e.getId()).nomeCompleto(e.getNomeCompleto()).email(e.getEmail()).cpf(e.getCpf()).telefone(e.getTelefone()).foto(e.getFoto()).matricula(e.getMatricula()).status(e.getStatus()).usuarioExcluido(e.isUsuarioExcluido()).build();}
 private AlunoEntity entidade(Aluno a){AlunoEntity e=new AlunoEntity();e.setId(a.getId());e.setNomeCompleto(a.getNomeCompleto());e.setEmail(a.getEmail());e.setCpf(a.getCpf());e.setTelefone(a.getTelefone());e.setFoto(a.getFoto());e.setMatricula(a.getMatricula());e.setStatus(a.getStatus());e.setUsuarioExcluido(a.isUsuarioExcluido());return e;}
}