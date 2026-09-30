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
 private final ValidadorFoto validadorFoto;
 @Transactional(readOnly=true)
 public Page<Aluno> listar(String n,String m,StatusAluno s,Pageable p){return repository.listar(limpar(n),limpar(m),s,p);}
 @Transactional(readOnly=true)
 public Aluno buscar(Long id){return repository.buscarPorId(id).filter(a->!a.isUsuarioExcluido()).orElseThrow(AlunoNaoEncontradoException::new);}
 public Aluno cadastrar(Aluno a){
  // salva cpf e telefone sem mascara
  a.setCpf(digitos(a.getCpf()));a.setTelefone(digitos(a.getTelefone()));
  // valida os dados que tem regra propria
  if(!validadorCpf.valido(a.getCpf()))throw new IllegalArgumentException("CPF inválido.");
  validarTelefone(a.getTelefone());validadorFoto.validar(a.getFoto());validarConflitos(a,null);
  // matricula e status inicial sao definidos pelo backend
  a.setMatricula(gerador.gerar());a.setStatus(StatusAluno.ATIVO);a.setUsuarioExcluido(false);a.setVersion(null);
  return repository.salvar(a);
 }
 public Aluno editar(Long id,Aluno m){
  Aluno a=buscar(id);
  // evita salvar por cima de uma versao mais nova
  if(m.getVersion()!=null&&!m.getVersion().equals(a.getVersion()))throw new ConflitoException("O aluno foi alterado por outra operação. Atualize os dados e tente novamente.");
  // cpf e matricula nao mudam na edicao
  a.setNomeCompleto(m.getNomeCompleto());a.setEmail(m.getEmail());a.setTelefone(digitos(m.getTelefone()));a.setFoto(m.getFoto());
  if(m.getStatus()!=null)a.setStatus(m.getStatus());
  validarTelefone(a.getTelefone());validadorFoto.validar(a.getFoto());validarConflitos(a,id);
  return repository.salvar(a);
 }
 public void inativar(Long id){
  Aluno a=buscar(id);
  // nao apaga de verdade, so marca como excluido
  a.setUsuarioExcluido(true);a.setStatus(StatusAluno.INATIVO);repository.salvar(a);
 }
 private void validarConflitos(Aluno a,Long id){
  if(repository.existeCpf(a.getCpf(),id))throw new ConflitoException("CPF já cadastrado.");
  if(repository.existeEmail(a.getEmail(),id))throw new ConflitoException("E-mail já cadastrado.");
 }
 private void validarTelefone(String t){if(t==null||(t.length()!=10&&t.length()!=11))throw new IllegalArgumentException("Telefone deve ter DDD e 10 ou 11 dígitos.");}
 private String digitos(String v){return v==null?null:v.replaceAll("\\D","");}
 private String limpar(String v){return v==null?null:v.trim();}
}