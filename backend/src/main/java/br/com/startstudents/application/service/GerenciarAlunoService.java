package br.com.startstudents.application.service;

import br.com.startstudents.application.ports.in.GerenciarAlunoUseCase;
import br.com.startstudents.application.ports.out.AlunoRepositoryPort;
import br.com.startstudents.domain.exception.AlunoNaoEncontradoException;
import br.com.startstudents.domain.exception.ConflitoException;
import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reúne as regras de negócio dos alunos.
 * O controller recebe a requisição; este serviço decide o que pode ser salvo.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class GerenciarAlunoService implements GerenciarAlunoUseCase {

    private final AlunoRepositoryPort repositorioAlunos;
    private final GeradorMatricula geradorMatricula;
    private final ValidadorCpf validadorCpf;
    private final ValidadorFoto validadorFoto;

    @Override
    @Transactional(readOnly = true)
    public Page<Aluno> listar(String nome, String matricula, StatusAluno status, Pageable paginacao) {
        return repositorioAlunos.listar(limpar(nome), limpar(matricula), status, paginacao);
    }

    @Override
    @Transactional(readOnly = true)
    public Aluno buscar(Long identificadorAluno) {
        return repositorioAlunos.buscarPorId(identificadorAluno)
                .filter(alunoEncontrado -> !alunoEncontrado.isUsuarioExcluido())
                .orElseThrow(AlunoNaoEncontradoException::new);
    }

    /**
     * Fluxo principal do cadastro:
     * 1. Remove a máscara do CPF e do telefone.
     * 2. Valida CPF, telefone, foto e possíveis duplicidades.
     * 3. Gera matrícula, define status ATIVO e salva no banco.
     */
    @Override
    public Aluno cadastrar(Aluno novoAluno) {
        novoAluno.setCpf(apenasDigitos(novoAluno.getCpf()));
        novoAluno.setTelefone(apenasDigitos(novoAluno.getTelefone()));

        if (!validadorCpf.valido(novoAluno.getCpf())) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        validarTelefone(novoAluno.getTelefone());
        validadorFoto.validar(novoAluno.getFoto());
        validarConflitos(novoAluno, null);

        // Estes dados são controlados pelo backend, mesmo que venham na requisição.
        novoAluno.setMatricula(geradorMatricula.gerar());
        novoAluno.setStatus(StatusAluno.ATIVO);
        novoAluno.setUsuarioExcluido(false);
        novoAluno.setVersion(null);

        return repositorioAlunos.salvar(novoAluno);
    }

    /** Atualiza apenas os campos permitidos, preservando CPF e matrícula. */
    @Override
    public Aluno editar(Long identificadorAluno, Aluno dadosAtualizados) {
        Aluno alunoExistente = buscar(identificadorAluno);

        // Impede sobrescrever uma alteração feita a partir de outra versão.
        if (dadosAtualizados.getVersion() != null
                && !dadosAtualizados.getVersion().equals(alunoExistente.getVersion())) {
            throw new ConflitoException(
                    "O aluno foi alterado por outra operação. Atualize os dados e tente novamente.");
        }

        alunoExistente.setNomeCompleto(dadosAtualizados.getNomeCompleto());
        alunoExistente.setEmail(dadosAtualizados.getEmail());
        alunoExistente.setTelefone(apenasDigitos(dadosAtualizados.getTelefone()));
        alunoExistente.setFoto(dadosAtualizados.getFoto());

        if (dadosAtualizados.getStatus() != null) {
            alunoExistente.setStatus(dadosAtualizados.getStatus());
        }

        validarTelefone(alunoExistente.getTelefone());
        validadorFoto.validar(alunoExistente.getFoto());
        validarConflitos(alunoExistente, identificadorAluno);

        return repositorioAlunos.salvar(alunoExistente);
    }

    /** Marca o aluno como excluído sem remover seu registro do banco. */
    @Override
    public void inativar(Long identificadorAluno) {
        buscar(identificadorAluno);
        repositorioAlunos.inativar(identificadorAluno);
    }

    /** Verifica se CPF ou e-mail já pertencem a outro aluno. */
    private void validarConflitos(Aluno aluno, Long identificadorIgnorado) {
        if (repositorioAlunos.existeCpf(aluno.getCpf(), identificadorIgnorado)) {
            throw new ConflitoException("CPF já cadastrado.");
        }

        if (repositorioAlunos.existeEmail(aluno.getEmail(), identificadorIgnorado)) {
            throw new ConflitoException("E-mail já cadastrado.");
        }
    }

    private void validarTelefone(String telefone) {
        if (telefone == null || (telefone.length() != 10 && telefone.length() != 11)) {
            throw new IllegalArgumentException("Telefone deve ter DDD e 10 ou 11 dígitos.");
        }
    }

    /** Normaliza CPF e telefone antes de validar e armazenar. */
    private String apenasDigitos(String valor) {
        return valor == null ? null : valor.replaceAll("\\D", "");
    }

    private String limpar(String valor) {
        return valor == null ? null : valor.trim();
    }
}
