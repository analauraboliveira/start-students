package br.com.startstudents.application.service;
import br.com.startstudents.application.ports.in.GerenciarAlunoUseCase;
import br.com.startstudents.application.ports.out.AlunoRepositoryPort;
import br.com.startstudents.domain.exception.AlunoNaoEncontradoException;
import br.com.startstudents.domain.exception.ConflitoException;
import br.com.startstudents.domain.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @RequiredArgsConstructor @Transactional
public class GerenciarAlunoService implements GerenciarAlunoUseCase {
 private final AlunoRepositoryPort repository;
 private final GeradorMatricula gerador;
 private final ValidadorCpf validadorCpf;

 // manda os filtros pro repositorio e devolve a pagina pronta
 @Transactional(readOnly=true)
 public Page<Aluno> listar(String n,String m,StatusAluno s,Pageable p){return repository.listar(limpar(n),limpar(m),s,p);}

 // aluno excluido logicamente nao pode aparecer nos detalhes
 @Transactional(readOnly=true)
 public Aluno buscar(Long id){return repository.buscarPorId(id).filter(a->!a.isUsuarioExcluido()).orElseThrow(AlunoNaoEncontradoException::new);}

 public Aluno cadastrar(Aluno a){
  // salva cpf e telefone sem mascara
  a.setCpf(digitos(a.getCpf())); a.setTelefone(digitos(a.getTelefone()));
  // valida antes de salvar
  if(!validadorCpf.valido(a.getCpf())) throw new IllegalArgumentException("CPF inválido.");
  validarConflitos(a,null);
  // matricula e status inicial sao definidos pelo backend
  a.setMatricula(gerador.gerar()); a.setStatus(StatusAluno.ATIVO); a.setUsuarioExcluido(false);
  return repository.salvar(a);
 }

 public Aluno editar(Long id,Aluno m){
  Aluno a=buscar(id);
  // cpf e matricula nao mudam na edicao
  a.setNomeCompleto(m.getNomeCompleto()); a.setEmail(m.getEmail()); a.setTelefone(digitos(m.getTelefone())); a.setFoto(m.getFoto());
  if(m.getStatus()!=null)a.setStatus(m.getStatus());
  // confere duplicidade ignorando o proprio aluno
  validarConflitos(a,id); return repository.salvar(a);
 }

 public void inativar(Long id){
  Aluno a=buscar(id);
  // nao apaga de verdade, so marca como excluido
  a.setUsuarioExcluido(true); a.setStatus(StatusAluno.INATIVO); repository.salvar(a);
 }

 private void validarConflitos(Aluno a,Long id){
  // cpf e email nao podem se repetir
  if(repository.existeCpf(a.getCpf(),id))throw new ConflitoException("CPF já cadastrado.");
  if(repository.existeEmail(a.getEmail(),id))throw new ConflitoException("E-mail já cadastrado.");
 }
 private String digitos(String v){return v==null?null:v.replaceAll("\\D","");}
 private String limpar(String v){return v==null?null:v.trim();}
}