package br.com.startstudents.adapters.out.persistence;

import br.com.startstudents.application.ports.out.AlunoRepositoryPort;
import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Faz a ponte entre as regras de negócio e o banco de dados.
 * O serviço trabalha com Aluno; o JPA trabalha com AlunoEntity.
 */
@Component
@RequiredArgsConstructor
public class AlunoRepositoryAdapter implements AlunoRepositoryPort {

    private final AlunoJpaRepository repositorioJpa;

    @Override
    public Page<Aluno> listar(String nome, String matricula, StatusAluno status, Pageable paginacao) {
        return repositorioJpa.filtrar(nome, matricula, status, paginacao)
                .map(this::paraDominio);
    }

    @Override
    public Optional<Aluno> buscarPorId(Long identificadorAluno) {
        return repositorioJpa.findById(identificadorAluno).map(this::paraDominio);
    }

    /** Converte o aluno para entidade, salva e devolve os dados persistidos. */
    @Override
    public Aluno salvar(Aluno aluno) {
        AlunoEntity entidadeSalva = repositorioJpa.saveAndFlush(paraEntidade(aluno));
        return paraDominio(entidadeSalva);
    }

    @Override
    public void inativar(Long identificadorAluno) {
        repositorioJpa.inativar(identificadorAluno);
    }

    @Override
    public boolean existeCpf(String cpf, Long identificadorIgnorado) {
        return repositorioJpa.existeCpf(cpf, identificadorIgnorado);
    }

    @Override
    public boolean existeEmail(String email, Long identificadorIgnorado) {
        return repositorioJpa.existeEmail(email, identificadorIgnorado);
    }

    @Override
    public boolean existeMatricula(String matricula) {
        return repositorioJpa.existsByMatricula(matricula);
    }

    /** Mapeia os dados do banco para o modelo usado no serviço. */
    private Aluno paraDominio(AlunoEntity entidade) {
        return Aluno.builder()
                .id(entidade.getId())
                .nomeCompleto(entidade.getNomeCompleto())
                .email(entidade.getEmail())
                .cpf(entidade.getCpf())
                .telefone(entidade.getTelefone())
                .foto(entidade.getFoto())
                .matricula(entidade.getMatricula())
                .status(entidade.getStatus())
                .usuarioExcluido(entidade.isUsuarioExcluido())
                .version(entidade.getVersion())
                .build();
    }

    /** Prepara uma entidade JPA para inclusão ou atualização no banco. */
    private AlunoEntity paraEntidade(Aluno aluno) {
        AlunoEntity entidade = new AlunoEntity();
        entidade.setId(aluno.getId());
        entidade.setNomeCompleto(aluno.getNomeCompleto());
        entidade.setEmail(aluno.getEmail());
        entidade.setCpf(aluno.getCpf());
        entidade.setTelefone(aluno.getTelefone());
        entidade.setFoto(aluno.getFoto());
        entidade.setMatricula(aluno.getMatricula());
        entidade.setStatus(aluno.getStatus());
        entidade.setUsuarioExcluido(aluno.isUsuarioExcluido());
        entidade.setVersion(aluno.getVersion());
        return entidade;
    }
}
