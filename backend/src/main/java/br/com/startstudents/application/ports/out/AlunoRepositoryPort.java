package br.com.startstudents.application.ports.out;
import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
public interface AlunoRepositoryPort {
 Page<Aluno> listar(String nome,String matricula,StatusAluno status,Pageable pageable);
 Optional<Aluno> buscarPorId(Long id);
 Aluno salvar(Aluno aluno);
 void inativar(Long id);
 boolean existeCpf(String cpf,Long ignorarId);
 boolean existeEmail(String email,Long ignorarId);
 boolean existeMatricula(String matricula);
}