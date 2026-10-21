# Start Students

> **Este é o repositório oficial do projeto Start Students, feito por Ana Laura Barboza Oliveira dos Santos para a CapGemini.** Antes, o projeto estava em um repositório de teste, e transferi para esse para deixar de uma maneira mais organizada.

Sistema de Gestão de Alunos desenvolvido para o desafio técnico **START STUDENTS — Protótipo 2.0**.

## Entregáveis mínimos

Os cinco entregáveis mínimos previstos no desafio estão resumidos abaixo para facilitar a consulta no repositório. A documentação detalhada, com explicações, decisões técnicas, regras, testes, endpoints e limitações, está disponível na pasta `docs/`.

### 1. Aplicação executável em ambiente local

- `backend/` — aplicação Spring Boot, API e regras de negócio.
- `frontend/` — aplicação Angular e interface do sistema.
- `database/` — scripts relacionados ao banco de dados.
- As instruções completas para execução local estão neste README, na seção **"Passo a passo para executar e testar do zero"**.

**Documentação detalhada:**
- `docs/01-arquitetura.txt`
- `docs/02-h2-e-banco.txt`
- `docs/07-limitacoes-e-proximos-passos.txt`

### 2. README com arquitetura, configuração e comandos

Este `README.md` apresenta de forma resumida:

- arquitetura e organização do projeto;
- tecnologias utilizadas;
- pré-requisitos;
- configuração do ambiente;
- comandos para build, execução e testes;
- usuários disponíveis para teste;
- endpoints e códigos HTTP.

As principais decisões de implementação estão detalhadas em:

- `docs/08-decisoes-de-codigo-backend.txt`
- `docs/09-decisoes-de-codigo-frontend.txt`

### 3. Documentação dos endpoints e respostas

Este README apresenta um resumo dos endpoints, perfis e códigos HTTP nas seções **"Endpoints"** e **"Códigos HTTP"**.

A documentação detalhada dos endpoints, incluindo entradas, saídas, permissões, códigos de erro e formatos das respostas está em:

- `docs/10-endpoints-e-saidas.txt`

Informações complementares sobre regras e fluxos estão em:

- `docs/03-regras-e-fluxos.txt`
- `docs/06-requisitos-e-aceite.txt`

### 4. Testes automatizados e instruções de execução

- **Backend:** `backend/src/test/`
  - testes unitários das regras de negócio;
  - testes de validação de CPF;
  - testes de integração dos endpoints e permissões.
- **Frontend:** arquivos `*.spec.ts` em `frontend/src/app/`.
- A descrição dos testes e da cobertura está em `docs/05-testes.txt`.
- Os comandos para executar os testes estão neste README, na seção **"Testes automatizados"**.

### 5. Registro de decisões, limitações e próximos passos

As decisões e informações complementares da implementação estão documentadas em:

- `docs/01-arquitetura.txt` — arquitetura do projeto.
- `docs/03-regras-e-fluxos.txt` — regras e fluxos implementados.
- `docs/06-requisitos-e-aceite.txt` — requisitos e critérios de aceite.
- `docs/07-limitacoes-e-proximos-passos.txt` — limitações conhecidas, funcionalidades futuras e itens fora do escopo.
- `docs/08-decisoes-de-codigo-backend.txt` — principais decisões de código do backend.
- `docs/09-decisoes-de-codigo-frontend.txt` — principais decisões de código do frontend.

> **Resumo:** este README apresenta uma visão geral dos cinco entregáveis mínimos. A versão detalhada da documentação está organizada nos arquivos da pasta `docs/`.

------------------------------

## Status

Entrega enxuta, pensada como aproximadamente 75% de um produto completo. O núcleo obrigatório do desafio foi priorizado e está funcional; itens de produção, opcionais e alguns refinamentos continuam listados em `docs/07-limitacoes-e-proximos-passos.txt`. Não foram adicionadas funcionalidades fora do escopo apenas para aumentar artificialmente a entrega.

## Stack

- Backend: Java 21 + Spring Boot 3
- Frontend: Angular 18 + TypeScript
- Banco: H2
- Segurança: Spring Security + JWT
- Testes: JUnit 5, Mockito, MockMvc e Jasmine
- Arquitetura: Hexagonal
- Build backend: Maven

## Organização do repositório

```text
.
├── backend/
│   ├── src/
│   │   ├── main/java/
│   │   │   └── br/com/startstudents/
│   │   │       ├── domain/              regras e modelos centrais
│   │   │       ├── application/
│   │   │       │   ├── ports/in/        contratos dos casos de uso
│   │   │       │   ├── ports/out/       contratos de infraestrutura
│   │   │       │   └── service/         implementação das regras
│   │   │       ├── adapters/
│   │   │       │   ├── in/web/          controllers e DTOs
│   │   │       │   └── out/persistence/ persistência com JPA e H2
│   │   │       ├── auth/                 autenticação, JWT e perfis
│   │   │       └── config/               configurações
│   │   └── test/                         testes unitários e de integração
│   └── pom.xml                           configuração do Maven
│
├── frontend/
│   ├── src/app/
│   │   ├── core/                         autenticação, guards, interceptor,
│   │   │                                  serviços e validadores
│   │   ├── models/                       modelos do frontend
│   │   └── pages/                        telas e fluxos da aplicação
│   └── package.json                      dependências e scripts
│
├── database/
│   └── start-students.sql                script SQL para consulta/teste manual
│
├── docs/
│   ├── 01-arquitetura.txt                arquitetura do projeto
│   ├── 02-h2-e-banco.txt                 banco H2 e persistência
│   ├── 03-regras-e-fluxos.txt            regras e fluxos do sistema
│   ├── 04-frontend-e-estados.txt         frontend e estados da interface
│   ├── 05-testes.txt                     estratégia e cobertura dos testes
│   ├── 06-requisitos-e-aceite.txt        requisitos e critérios de aceite
│   ├── 07-limitacoes-e-proximos-passos.txt
│   │                                      limitações e próximos passos
│   ├── 08-decisoes-de-codigo-backend.txt
│   │                                      decisões de implementação do backend
│   ├── 09-decisoes-de-codigo-frontend.txt
│   │                                      decisões de implementação do frontend
│   └── 10-endpoints-e-saidas.txt          endpoints, entradas, saídas e códigos HTTP
│
└── README.md                              documentação principal
```

## O que já foi priorizado

Autenticação, dois perfis, autorização no backend, listagem, paginação, ordenação, busca, filtro, cadastro, detalhes, edição, exclusão lógica, validações, matrícula automática, H2, estados principais da interface e testes mínimos.

## Usuários locais

| Perfil | Usuário | Senha |
|---|---|---|
| Administrador | adminuser | admin123 |
| Leitor | leitoruser | leitor123 |

Essas credenciais são somente para execução local do protótipo.

## Passo a passo para executar e testar do zero

Use esta sequência depois de clonar ou copiar o repositório para outro computador.

### 1. Conferir os pré-requisitos

No PowerShell:

```powershell
java -version
javac -version
node -v
npm -v
```

O projeto usa Java 21 no build. Não é necessário instalar Maven globalmente: o repositório possui Maven Wrapper e ele baixa a distribuição necessária na primeira execução.

### 2. Clonar ou atualizar o projeto

Para uma cópia nova:

```powershell
git clone https://github.com/analaurabos/start-students-oficial.git
cd start-students-oficial
```

Se o repositório já estiver no computador:

```powershell
git pull
```

### 3. Build e testes do backend

A partir da raiz:

```powershell
cd backend
.\mvnw.cmd clean test
```

Na primeira execução, aguarde o download do Maven. O resultado esperado ao final é `BUILD SUCCESS`.

Para também gerar o pacote da aplicação:

```powershell
.\mvnw.cmd clean package
```

### 4. Iniciar o backend

Ainda em `backend`:

```powershell
.\mvnw.cmd spring-boot:run
```

Mantenha esse terminal aberto.

- H2 Console: `http://localhost:8080/h2-console`

No H2:

```text
JDBC URL: jdbc:h2:file:./data/startstudents
User Name: sa
Password: deixe vazio
```

Para conferir os alunos carregados:

```sql
SELECT * FROM ALUNOS;
```

### 5. Instalar as dependências do frontend

Abra outro terminal na raiz do projeto e deixe o backend funcionando:

```powershell
cd frontend
npm install
```

`node_modules` não é salvo no Git, então o `npm install` precisa ser executado novamente em cada clone novo.

Avisos de pacotes deprecated ou vulnerabilidades não significam necessariamente que a instalação falhou. Não use `npm audit fix --force` sem revisar as alterações, pois ele pode trocar versões e quebrar o projeto.

### 6. Fazer o build do frontend

```powershell
npm run build
```

O comando deve terminar sem erros.

### 7. Iniciar o frontend

```powershell
npm start
```

Se o Angular perguntar sobre compartilhamento de dados de uso, responder `N` não interfere na aplicação.

Abra:

```text
http://localhost:4200
```

O fluxo esperado é login → autenticação → listagem de alunos.

### 8. Teste manual

Administrador:

```text
Usuário: adminuser
Senha: admin123
```

Teste login, listagem, busca, filtro, paginação, detalhes, cadastro, edição, exclusão lógica e logout.

Depois teste o perfil Leitor:

```text
Usuário: leitoruser
Senha: leitor123
```

O Leitor pode listar, buscar e consultar detalhes, mas não pode cadastrar, editar ou excluir.

### 9. Testes automatizados

Backend:

```powershell
cd backend
.\mvnw.cmd clean test
```

Frontend:

```powershell
cd frontend
npm install
npm test -- --watch=false
```

### 10. Erro "'ng' is not recognized"

Esse erro normalmente significa que as dependências do frontend ainda não estão instaladas. Dentro de `frontend`:

```powershell
npm install
npm start
```

Não é necessário instalar o Angular CLI globalmente.

## Endpoints

| Método | Endpoint | Perfil |
|---|---|---|
| POST | `/api/auth/login` | público |
| GET | `/api/alunos` | ADMINISTRADOR e LEITOR |
| GET | `/api/alunos/{id}` | ADMINISTRADOR e LEITOR |
| POST | `/api/alunos` | ADMINISTRADOR |
| PUT | `/api/alunos/{id}` | ADMINISTRADOR |
| DELETE | `/api/alunos/{id}` | ADMINISTRADOR |

A listagem aceita `nome`, `matricula`, `status`, `page`, `size` e `sort`.

A documentação detalhada dos endpoints, entradas, saídas, permissões e erros está em:

`docs/10-endpoints-e-saidas.txt`

## Códigos HTTP

Foram previstos os códigos obrigatórios: 200/204, 201, 400, 401, 403, 404, 409, 422 e 500.

A explicação detalhada de cada código, suas situações de uso e os formatos de erro está em:

`docs/10-endpoints-e-saidas.txt`

## Documentação

A pasta `docs/` concentra a documentação detalhada do projeto. O README apresenta um resumo para facilitar a consulta, enquanto os arquivos abaixo aprofundam cada tema:

- `01-arquitetura.txt` — arquitetura do projeto.
- `02-h2-e-banco.txt` — banco H2 e persistência.
- `03-regras-e-fluxos.txt` — regras e fluxos do sistema.
- `04-frontend-e-estados.txt` — frontend e estados da interface.
- `05-testes.txt` — estratégia e cobertura dos testes.
- `06-requisitos-e-aceite.txt` — requisitos e critérios de aceite.
- `07-limitacoes-e-proximos-passos.txt` — limitações, itens fora do escopo e próximos passos.
- `08-decisoes-de-codigo-backend.txt` — principais decisões de código do backend.
- `09-decisoes-de-codigo-frontend.txt` — principais decisões de código do frontend.
- `10-endpoints-e-saidas.txt` — endpoints, entradas, saídas, perfis e códigos HTTP.

## Banco

O Spring mantém o H2 em arquivo e carrega a massa inicial somente quando o banco está vazio. Cadastros, edições e exclusões lógicas permanecem após reiniciar o backend. Também existe `database/start-students.sql` para consulta/teste manual.

## Sobre o nível da entrega

O núcleo obrigatório do desafio foi priorizado e está funcional, mas o projeto não é o produto de produção completo, por conta do prazo de entrega. Persistência de usuários com senha em hash, configuração externa de segredos, banco de produção, Docker, CI/CD, cache, auditoria avançada, restauração e uma cobertura de testes mais ampla continuaram fora desta etapa. Por isso, acredito que tenha executado cerca de ~75% do projeto.
