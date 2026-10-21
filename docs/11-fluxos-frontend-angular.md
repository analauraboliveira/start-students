# Fluxos do frontend Angular — etapa 2

## Organização das telas

- `novo-aluno.component.ts/html`: cadastro, com formulário reativo.
- `formulario-aluno.component.ts/html`: edição de aluno existente.
- `detalhes-aluno.component.ts/html`: busca e apresentação de um aluno.
- `dados-aluno.component.ts/html`: exibição reutilizável dos dados.
- `foto-aluno.component.ts/html`: foto reutilizável nos dois formulários.
- `lista-alunos.component.ts/html`: busca, filtros, paginação e exclusão.
- `cpf-reactive.validator.ts`: regra de CPF para Reactive Forms.

## Fluxo principal: Novo Aluno

1. A rota `/alunos/novo` abre `NovoAlunoComponent`.
2. O componente verifica a permissão de administrador.
3. O `FormBuilder` cria os controles do formulário.
4. `Validators.required`, `email`, `minLength`, `maxLength` e `pattern` verificam os campos comuns.
5. `cpfReactiveValidator` usa a validação de CPF em um arquivo separado.
6. O HTML apresenta erros quando o campo é tocado ou quando a pessoa tenta salvar.
7. O componente de foto emite eventos de seleção e remoção.
8. `salvar()` impede envios inválidos ou duplicados.
9. `AlunoService.cadastrar()` faz a requisição POST para a API.
10. O backend gera a matrícula; o frontend mostra o retorno e navega para a lista.
11. Se houver erro, o formulário permanece aberto e apresenta as mensagens.
12. O guard `alteracoesPendentesGuard` pergunta antes de sair com dados não salvos.

## Fluxo secundário: Detalhes

1. A lista abre `/alunos/:id`.
2. `DetalhesAlunoComponent` lê o ID da rota e verifica se é válido.
3. `AlunoService.buscar(id)` consulta a API.
4. Enquanto a requisição acontece, a página mostra o estado de carregamento.
5. O componente `DadosAlunoComponent` apresenta nome, CPF, e-mail, telefone, matrícula, status e foto.
6. Em caso de erro, a página mostra uma mensagem.
7. O botão Voltar e a tecla Escape retornam para a listagem.

## Diferença entre cadastro e edição

O cadastro não recebe ID e envia um POST. A edição recebe o ID, carrega o registro, mantém CPF e matrícula somente para leitura e envia um PUT. Os dois usam formulários reativos, mas não compartilham as regras específicas do fluxo.

## Validação e testes

A validação no frontend melhora a experiência, mas o backend continua responsável por validar os dados recebidos. Existem testes de CPF, cadastro, rotas, listagem e detalhes. Para verificar localmente:

```bash
cd frontend
npm ci
npm run build
npm test -- --watch=false --browsers=ChromeHeadless
```

A execução dos comandos é necessária para confirmar o resultado da compilação e dos testes.
