package br.com.startstudents.application.service;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.startstudents.application.ports.out.AlunoRepositoryPort;
import br.com.startstudents.domain.exception.ConflitoException;
import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;
@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class GerenciarAlunoServiceTest {
 @Mock AlunoRepositoryPort repository;@Mock GeradorMatricula gerador;@Mock ValidadorCpf cpf;@Mock ValidadorFoto foto;
 @InjectMocks GerenciarAlunoService service;
 private Aluno valido(){return Aluno.builder().nomeCompleto("Ana Silva").cpf("52998224725").telefone("(81) 99999-9999").email("ana@email.com").build();}
 
 // testes
 // 1. não deve cadastrar cpf duplicado, 2. não deve cadastrar email duplicado
 // 3. cadastro normalizado e gera matrícula, 4. não deve aceitar telefone inválido
 // 5. editar mantém cpf e matrícula, 6. deve detectar conflito de versão
 // 7. excluir lógico
 
 @Test void naoDeveCadastrarCpfDuplicado()

 {Aluno a=valido();when(cpf.valido(any())).thenReturn(true);when(repository.existeCpf("52998224725",null)).thenReturn(true);assertThrows(ConflitoException.class,()->service.cadastrar(a));verify(repository,never()).salvar(any());}

 @Test void naoDeveCadastrarEmailDuplicado(){Aluno a=valido();when(cpf.valido(any())).thenReturn(true); when(repository.existeEmail("ana@email.com",null)).thenReturn(true);assertThrows(ConflitoException.class,()->service.cadastrar(a));verify(repository,never()).salvar(any());}

 @Test void cadastroNormalizaDadosEGeraMatricula(){Aluno a=valido();when(cpf.valido("52998224725")).thenReturn(true);when(gerador.gerar()).thenReturn("20260001");when(repository.salvar(any())).thenAnswer(i->i.getArgument(0));Aluno salvo=service.cadastrar(a);assertEquals("52998224725",salvo.getCpf());assertEquals("81999999999",salvo.getTelefone());assertEquals("20260001",salvo.getMatricula());assertEquals(StatusAluno.ATIVO,salvo.getStatus());assertFalse(salvo.isUsuarioExcluido());}

 @Test void naoDeveAceitarTelefoneInvalido(){Aluno a=valido();a.setTelefone("81999");when(cpf.valido(any())).thenReturn(true);assertThrows(IllegalArgumentException.class,()->service.cadastrar(a));verify(repository,never()).salvar(any());}

 @Test void editarMantemCpfEMatricula(){Aluno atual=Aluno.builder().id(1L).nomeCompleto("Ana Silva").cpf("52998224725").telefone("81999999999").email("ana@email.com").matricula("20260001").status(StatusAluno.ATIVO).version(2L).build();Aluno mudanca=Aluno.builder().nomeCompleto("Ana Souza").cpf("11111111111").telefone("(81) 98888-7777").email("nova@email.com").matricula("OUTRA").status(StatusAluno.ATIVO).version(2L).build();when(repository.buscarPorId(1L)).thenReturn(Optional.of(atual));when(repository.salvar(any())).thenAnswer(i->i.getArgument(0));Aluno salvo=service.editar(1L,mudanca);assertEquals("52998224725",salvo.getCpf());assertEquals("20260001",salvo.getMatricula());assertEquals("Ana Souza",salvo.getNomeCompleto());}

 @Test void deveDetectarConflitoDeVersao(){Aluno atual=Aluno.builder().id(1L).cpf("52998224725").telefone("81999999999").email("ana@email.com").matricula("20260001").status(StatusAluno.ATIVO).version(3L).build();Aluno mudanca=Aluno.builder().version(2L).build();when(repository.buscarPorId(1L)).thenReturn(Optional.of(atual));assertThrows(ConflitoException.class,()->service.editar(1L,mudanca));verify(repository,never()).salvar(any());}

 @Test void excluirEhLogico(){Aluno atual=Aluno.builder().id(1L).status(StatusAluno.ATIVO).build();when(repository.buscarPorId(1L)).thenReturn(Optional.of(atual));service.inativar(1L);verify(repository).inativar(1L);verify(repository,never()).salvar(any());}
}
