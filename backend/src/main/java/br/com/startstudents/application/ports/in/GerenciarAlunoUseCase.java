package br.com.startstudents.application.ports.in;
import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface GerenciarAlunoUseCase {
 Page<Aluno> listar(String nome,String matricula,StatusAluno status,Pageable pageable);
 Aluno buscar(Long id);
 Aluno cadastrar(Aluno aluno);
 Aluno editar(Long id,Aluno aluno);
 void inativar(Long id);
}