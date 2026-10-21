package br.com.startstudents.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import br.com.startstudents.application.ports.out.AlunoRepositoryPort;
import br.com.startstudents.domain.exception.ConflitoException;
import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Testa as regras de cadastro e edição sem precisar iniciar o banco. */
@ExtendWith(MockitoExtension.class)
class GerenciarAlunoServiceTest {

    @Mock
    private AlunoRepositoryPort repositorioAlunos;

    @Mock
    private GeradorMatricula geradorMatricula;

    @Mock
    private ValidadorCpf validadorCpf;

    @Mock
    private ValidadorFoto validadorFoto;

    @InjectMocks
    private GerenciarAlunoService gerenciarAlunoService;

    private Aluno criarAlunoValido() {
        return Aluno.builder()
                .nomeCompleto("Ana Silva")
                .cpf("52998224725")
                .telefone("(81) 99999-9999")
                .email("ana@email.com")
                .build();
    }

    @Test
    void naoDeveCadastrarCpfDuplicado() {
        Aluno novoAluno = criarAlunoValido();
        when(validadorCpf.valido(any())).thenReturn(true);
        when(repositorioAlunos.existeCpf("52998224725", null)).thenReturn(true);

        assertThrows(ConflitoException.class, () -> gerenciarAlunoService.cadastrar(novoAluno));
        verify(repositorioAlunos, never()).salvar(any());
    }

    @Test
    void naoDeveCadastrarEmailDuplicado() {
        Aluno novoAluno = criarAlunoValido();
        when(validadorCpf.valido(any())).thenReturn(true);
        when(repositorioAlunos.existeEmail("ana@email.com", null)).thenReturn(true);

        assertThrows(ConflitoException.class, () -> gerenciarAlunoService.cadastrar(novoAluno));
        verify(repositorioAlunos, never()).salvar(any());
    }

    @Test
    void cadastroNormalizaDadosEGeraMatricula() {
        Aluno novoAluno = criarAlunoValido();
        when(validadorCpf.valido("52998224725")).thenReturn(true);
        when(geradorMatricula.gerar()).thenReturn("20260001");
        when(repositorioAlunos.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        // Confere o resultado depois das validações e da geração de matrícula.
        Aluno alunoSalvo = gerenciarAlunoService.cadastrar(novoAluno);

        assertEquals("52998224725", alunoSalvo.getCpf());
        assertEquals("81999999999", alunoSalvo.getTelefone());
        assertEquals("20260001", alunoSalvo.getMatricula());
        assertEquals(StatusAluno.ATIVO, alunoSalvo.getStatus());
        assertFalse(alunoSalvo.isUsuarioExcluido());
        assertNull(alunoSalvo.getVersion());
    }

    @Test
    void naoDeveAceitarTelefoneInvalido() {
        Aluno novoAluno = criarAlunoValido();
        novoAluno.setTelefone("81999");
        when(validadorCpf.valido(any())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> gerenciarAlunoService.cadastrar(novoAluno));
        verify(repositorioAlunos, never()).salvar(any());
    }

    @Test
    void naoDeveAceitarCpfInvalido() {
        Aluno novoAluno = criarAlunoValido();
        when(validadorCpf.valido(any())).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> gerenciarAlunoService.cadastrar(novoAluno));
        verify(repositorioAlunos, never()).salvar(any());
    }

    @Test
    void editarMantemCpfEMatricula() {
        Aluno alunoExistente = Aluno.builder()
                .id(1L).nomeCompleto("Ana Silva").cpf("52998224725")
                .telefone("81999999999").email("ana@email.com")
                .matricula("20260001").status(StatusAluno.ATIVO).version(2L)
                .build();
        Aluno dadosAtualizados = Aluno.builder()
                .nomeCompleto("Ana Souza").cpf("11111111111")
                .telefone("(81) 98888-7777").email("nova@email.com")
                .matricula("OUTRA").status(StatusAluno.ATIVO).version(2L)
                .build();

        when(repositorioAlunos.buscarPorId(1L)).thenReturn(Optional.of(alunoExistente));
        when(repositorioAlunos.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

        Aluno alunoSalvo = gerenciarAlunoService.editar(1L, dadosAtualizados);

        assertEquals("52998224725", alunoSalvo.getCpf());
        assertEquals("20260001", alunoSalvo.getMatricula());
        assertEquals("Ana Souza", alunoSalvo.getNomeCompleto());
    }

    @Test
    void deveDetectarConflitoDeVersao() {
        Aluno alunoExistente = Aluno.builder()
                .id(1L).cpf("52998224725").telefone("81999999999")
                .email("ana@email.com").matricula("20260001")
                .status(StatusAluno.ATIVO).version(3L)
                .build();
        Aluno dadosAtualizados = Aluno.builder().version(2L).build();

        when(repositorioAlunos.buscarPorId(1L)).thenReturn(Optional.of(alunoExistente));

        assertThrows(ConflitoException.class,
                () -> gerenciarAlunoService.editar(1L, dadosAtualizados));
        verify(repositorioAlunos, never()).salvar(any());
    }

    @Test
    void excluirEhLogico() {
        Aluno alunoExistente = Aluno.builder().id(1L).status(StatusAluno.ATIVO).build();
        when(repositorioAlunos.buscarPorId(1L)).thenReturn(Optional.of(alunoExistente));

        gerenciarAlunoService.inativar(1L);

        verify(repositorioAlunos).inativar(1L);
        verify(repositorioAlunos, never()).salvar(any());
    }
}
