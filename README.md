# Start Students

> **Este é o repositório oficial do projeto Start Students.** Antes, o projeto estava em um repositório de teste. A partir desta entrega, este repositório passa a ser a referência oficial.

Sistema de Gestão de Alunos desenvolvido para o desafio técnico **START STUDENTS — Protótipo 2.0**.

## Status

Entrega enxuta, pensada como aproximadamente 75% de um produto completo. O núcleo obrigatório do desafio foi priorizado e está funcional; itens de produção, opcionais e alguns refinamentos continuam listados em `docs/07-limitacoes-e-proximos-passos.txt`.

Não foram adicionadas funcionalidades fora do escopo apenas para aumentar artificialmente a entrega.

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

database/                  script SQL
docs/                      explicações do projeto em linguagem simples
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
git clone https://github.com/analauraboliveira/start-students.git
cd start-students
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

- Backend: `http://localhost:8080`
- API: `http://localhost:8080/api`
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
.\mvnw.cmd test
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

### 11. Antes de fazer commit

Na raiz:

```powershell
git status
```

O `.gitignore` evita o versionamento de `frontend/node_modules/`, `frontend/.angular/`, `frontend/dist/`, `backend/target/` e da distribuição do Maven baixada pelo wrapper. Para parar backend ou frontend, use `Ctrl + C` no terminal correspondente.

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
./mvnw test
```

```bash
cd frontend
npm install
npm test -- --watch=false
```

## Documentação

A pasta `docs/` explica arquitetura, H2, regras, front, segurança, testes, critérios de aceite e o que ainda falta. O arquivo `docs/06-requisitos-e-aceite.txt` funciona como checklist da especificação.

## Banco

O Spring mantém o H2 em arquivo e carrega a massa inicial somente quando o banco está vazio. Cadastros, edições e exclusões lógicas permanecem após reiniciar o backend. Também existe `database/start-students.sql` para consulta/teste manual.

## Sobre o nível da entrega

O núcleo obrigatório do desafio foi priorizado e está funcional, mas o projeto continua propositalmente enxuto: não é apresentado como produto de produção completo. Persistência de usuários com senha em hash, configuração externa de segredos, banco de produção, Docker, CI/CD, cache, auditoria avançada, restauração e uma cobertura de testes mais ampla continuam fora desta etapa. Por isso, a referência de ~75% diz respeito ao produto completo imaginado, e não a apenas 75% das regras obrigatórias.
