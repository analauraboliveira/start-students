# Start Students

> **Este é o repositório oficial do projeto Start Students.** Antes, o projeto estava em um repositório de teste. A partir desta entrega, este repositório passa a ser a referência oficial.

Sistema de Gestão de Alunos desenvolvido para o desafio técnico **START STUDENTS — Protótipo 2.0**.

## Status

Entrega parcial planejada (~75% do escopo obrigatório). A ideia aqui não é fingir que está 100%: a base principal está funcional e organizada, e o que ainda falta está listado em `docs/07-limitacoes-e-proximos-passos.txt`.

Não foram implementados itens fora do escopo obrigatório.

## Stack

- Backend: Java 21 + Spring Boot 3
- Frontend: Angular 18 + TypeScript
- Banco: H2
- Segurança: Spring Security + JWT
- Testes: JUnit 5, Mockito, MockMvc e Jasmine
- Arquitetura: Hexagonal
- Build backend: Maven

## Organização

```text
backend/
  domain/                  regras e modelos centrais
  application/
    ports/in/              casos de uso
    ports/out/             contratos que o domínio precisa
    service/               implementação das regras
  adapters/
    in/web/                controllers e DTOs
    out/persistence/       JPA e H2
  auth/                    login e JWT
  config/                  configurações

frontend/src/app/
  core/                    auth, guards, interceptor e services
  models/                  modelos do front
  pages/                   telas
  shared/                  coisas reaproveitáveis

database/                  script SQL
docs/                      explicações do projeto em linguagem simples
```

## O que já foi priorizado

Autenticação, dois perfis, autorização no backend, listagem, paginação, ordenação, busca, filtro, cadastro, detalhes, edição, exclusão lógica, validações, matrícula automática, H2, estados principais da interface e testes mínimos.

## Usuários locais

| Perfil | Usuário | Senha |
|---|---|---|
| Administrador | admin123 | admin123 |
| Leitor | leitor123 | leitor123 |

Essas credenciais são somente para execução local do protótipo.

## Executar o backend

Requer JDK 21 e Maven.

```bash
cd backend
mvn spring-boot:run
```

API: `http://localhost:8080/api`

Console H2: `http://localhost:8080/h2-console`

```text
JDBC URL: jdbc:h2:mem:startstudents
User Name: sa
Password: deixe vazio
```

## Executar o frontend

Requer Node 20+ e npm.

```bash
cd frontend
npm install
npm start
```

Abra `http://localhost:4200`.

## Endpoints

| Método | Endpoint | Perfil |
|---|---|---|
| POST | /api/auth/login | público |
| GET | /api/alunos | ADMINISTRADOR e LEITOR |
| GET | /api/alunos/{id} | ADMINISTRADOR e LEITOR |
| POST | /api/alunos | ADMINISTRADOR |
| PUT | /api/alunos/{id} | ADMINISTRADOR |
| DELETE | /api/alunos/{id} | ADMINISTRADOR |

A listagem aceita `nome`, `matricula`, `status`, `page`, `size` e `sort`.

## Códigos HTTP

Foram previstos os códigos obrigatórios: 200/204, 201, 400, 401, 403, 404, 409, 422 e 500.

## Testes

```bash
cd backend
mvn test
```

```bash
cd frontend
npm install
npm test -- --watch=false
```

## Documentação

A pasta `docs/` explica arquitetura, H2, regras, front, segurança, testes, critérios de aceite e o que ainda falta. O arquivo `docs/06-requisitos-e-aceite.txt` funciona como checklist da especificação.

## Banco

O Spring cria as tabelas via JPA e carrega uma massa inicial. Também existe `database/start-students.sql` para consulta/teste manual.

## Importante sobre os 75%

A entrega prioriza o núcleo avaliável do desafio. Alguns refinamentos de UX e acabamento visual continuam como próximos passos. Os opcionais (Docker, CI/CD, cache, auditoria avançada e restauração) não fazem parte desta etapa.
