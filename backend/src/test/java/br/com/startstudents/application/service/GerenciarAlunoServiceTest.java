package br.com.startstudents.application.service;
import br.com.startstudents.application.ports.out.AlunoRepositoryPort;import br.com.startstudents.domain.exception.ConflitoException;import br.com.startstudents.domain.model.Aluno;import org.junit.jupiter.api.*;import org.mockito.*;import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
class GerenciarAlunoServiceTest {
 @Mock AlunoRepositoryPort repository; @Mock GeradorMatricula gerador; @Mock ValidadorCpf cpf; @InjectMocks GerenciarAlunoService service;
 @BeforeEach void abrir(){MockitoAnnotations.openMocks(this);}
 @Test void naoDeveCadastrarCpfDuplicado(){Aluno a=Aluno.builder().cpf("52998224725").telefone("81999999999").email("a@a.com").build();when(cpf.valido(any())).thenReturn(true);when(repository.existeCpf("52998224725",null)).thenReturn(true);assertThrows(ConflitoException.class,()->service.cadastrar(a));verify(repository,never()).salvar(any());}
}