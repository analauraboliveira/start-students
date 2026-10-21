package br.com.startstudents.adapters.in.web;

import br.com.startstudents.adapters.in.web.dto.AlunoRequest;
import br.com.startstudents.adapters.in.web.dto.AlunoResponse;
import br.com.startstudents.application.ports.in.GerenciarAlunoUseCase;
import br.com.startstudents.domain.model.Aluno;
import br.com.startstudents.domain.model.StatusAluno;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    private final GerenciarAlunoUseCase gerenciarAlunoUseCase;

    /** Lista alunos aplicando os filtros e a paginação recebidos pela URL. */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','LEITOR')")
    public Page<AlunoResponse> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) StatusAluno status,
            @PageableDefault(size = 10, sort = "nomeCompleto", direction = Sort.Direction.ASC)
            Pageable paginacao) {

        return gerenciarAlunoUseCase.listar(nome, matricula, status, paginacao)
                .map(AlunoResponse::de);
    }

    /** Busca um aluno específico e devolve seus dados para a tela de detalhes. */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','LEITOR')")
    public AlunoResponse buscar(@PathVariable Long id) {
        return AlunoResponse.de(gerenciarAlunoUseCase.buscar(id));
    }

    /**
     * Cadastro: valida o JSON, converte para o domínio e chama o caso de uso.
     * A matrícula e o status inicial são definidos pelo backend, não pela tela.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<AlunoResponse> cadastrar(@Valid @RequestBody AlunoRequest requisicao) {
        Aluno novoAluno = mapearParaDominio(requisicao);
        Aluno alunoCadastrado = gerenciarAlunoUseCase.cadastrar(novoAluno);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AlunoResponse.de(alunoCadastrado));
    }

    /** A edição usa o mesmo DTO, mas mantém as regras próprias no serviço. */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public AlunoResponse editar(@PathVariable Long id, @Valid @RequestBody AlunoRequest requisicao) {
        Aluno dadosAtualizados = mapearParaDominio(requisicao);
        return AlunoResponse.de(gerenciarAlunoUseCase.editar(id, dadosAtualizados));
    }

    /** A exclusão é lógica: o registro continua no banco. */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        gerenciarAlunoUseCase.inativar(id);
        return ResponseEntity.noContent().build();
    }

    /** Separa o formato recebido pela API do modelo usado nas regras de negócio. */
    private Aluno mapearParaDominio(AlunoRequest requisicao) {
        return Aluno.builder()
                .nomeCompleto(requisicao.nomeCompleto())
                .email(requisicao.email())
                .cpf(requisicao.cpf())
                .telefone(requisicao.telefone())
                .foto(requisicao.foto())
                .status(requisicao.status())
                .version(requisicao.version())
                .build();
    }
}
