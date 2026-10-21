# Fluxo de cadastro de Novo Aluno — backend

Este roteiro mostra o caminho dos dados no Spring Boot. A arquitetura separa a entrada HTTP, as regras de negócio e a persistência.

## 1. O Angular envia o formulário

O frontend chama `POST /api/alunos` e envia nome, CPF, e-mail, telefone e foto opcional. A rota só permite o perfil `ADMINISTRADOR`.

## 2. O controller recebe a requisição

**Arquivo:** `AlunoController.java`, método `cadastrar`.

- `@PostMapping` indica que esta rota recebe um POST.
- `@Valid @RequestBody AlunoRequest` transforma o JSON em objeto e verifica as anotações de validação.
- `mapearParaDominio` transforma o DTO recebido em um `Aluno`.
- O controller chama `gerenciarAlunoUseCase.cadastrar(novoAluno)`.
- Se tudo der certo, devolve `201 Created` com um `AlunoResponse`.

O controller não gera matrícula nem grava diretamente no banco.

## 3. O serviço aplica as regras de cadastro

**Arquivo:** `GerenciarAlunoService.java`, método `cadastrar`.

1. Remove pontuação do CPF e do telefone.
2. Verifica os dígitos do CPF usando `ValidadorCpf`.
3. Confere se o telefone tem DDD e 10 ou 11 dígitos.
4. Valida a foto com `ValidadorFoto` (JPEG/PNG, até 5 MB).
5. Verifica CPF e e-mail duplicados por meio do repositório.
6. Gera uma matrícula usando `GeradorMatricula`.
7. Define `ATIVO`, marca o aluno como não excluído e deixa a versão inicial a cargo do banco.
8. Chama `repositorioAlunos.salvar(novoAluno)`.

`@Transactional` faz o trabalho ocorrer dentro de uma transação.

## 4. O repositório salva

**Arquivos:** `AlunoRepositoryPort.java`, `AlunoRepositoryAdapter.java`, `AlunoJpaRepository.java` e `AlunoEntity.java`.

- **Port:** define as operações que o serviço pode usar, sem conhecer o JPA.
- **Adapter:** converte `Aluno` para `AlunoEntity`, salva e converte o resultado de volta.
- **JPA repository:** executa as operações de banco e as consultas de duplicidade.
- **Entity:** representa a tabela `alunos`, com colunas e restrições.

## 5. A resposta volta ao Angular

O serviço devolve o aluno salvo, já com ID, matrícula e versão. O controller usa `AlunoResponse.de(alunoCadastrado)` e devolve o JSON com HTTP 201.

## 6. E se houver erro?

O `TratadorGlobalException` converte exceções em respostas HTTP:

- **400:** JSON malformado.
- **401/403:** autenticação ausente ou permissão insuficiente.
- **409:** conflito, como CPF ou e-mail duplicado.
- **422:** dados inválidos, com mensagens de validação.
- **404:** aluno não encontrado em consultas por ID.

## Como explicar em uma apresentação

> Quando eu clico em cadastrar, o Angular envia os dados para o endpoint POST. O controller recebe e valida o formato básico. Depois, ele chama o serviço, que concentra as regras: normaliza CPF e telefone, valida os campos, verifica duplicidades e gera a matrícula. Em seguida, o adapter converte o aluno para uma entidade JPA e salva no banco. Por fim, a API devolve o aluno cadastrado com status 201, e o frontend pode mostrar o resultado.

## Arquivos principais para estudar

1. `AlunoController.java`
2. `AlunoRequest.java` e `AlunoResponse.java`
3. `GerenciarAlunoService.java`
4. `ValidadorCpf.java`, `ValidadorFoto.java` e `GeradorMatricula.java`
5. `AlunoRepositoryPort.java`, `AlunoRepositoryAdapter.java` e `AlunoEntity.java`
6. `GerenciarAlunoServiceTest.java`
