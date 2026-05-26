# ☕ API de Clientes

> API REST para gerenciamento de clientes construída com **Java 21**, **Spring Boot 4** e **PostgreSQL**, utilizando JDBC puro (sem ORM).

---

## 📋 Índice

- [Tecnologias](#-tecnologias)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Configuração do Banco de Dados](#-configuração-do-banco-de-dados)
- [Como Executar](#-como-executar)
- [Endpoint Disponível](#-endpoint-disponível)
- [Camadas da Aplicação](#-camadas-da-aplicação)
- [Enums](#-enums)
- [Conceitos Aprendidos](#-conceitos-aprendidos)

---

## 🛠 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 4.0.6 | Framework web |
| PostgreSQL | — | Banco de dados relacional |
| Lombok | — | Geração de código boilerplate |
| Springdoc OpenAPI | 3.0.3 | Documentação Swagger automática |
| Maven | — | Gerenciador de dependências |

---

## 📁 Estrutura do Projeto

```
src/main/java/br/com/phamtecnologia/apiclientes/
│
├── enums/
│   ├── TipoCliente.java        # PESSOA_FISICA | PESSOA_JURIDICA
│   └── StatusCliente.java      # ATIVO | INATIVO | BLOQUEADO
│
├── entities/
│   ├── Cliente.java            # Modelo de dados
│   └── ClienteController.java  # Endpoint REST
│
├── factories/
│   └── ConnectionFactory.java  # Conexão JDBC com o PostgreSQL
│
├── repositories/
│   └── ClienteRepository.java  # Operações SQL (INSERT)
│
└── sql/
    └── script.sql              # DDL: criação da tabela clientes
```

---

## 🗄 Configuração do Banco de Dados

Execute o script abaixo no PostgreSQL antes de iniciar a aplicação:

```sql
CREATE TABLE clientes (
    id               SERIAL        PRIMARY KEY,
    nome             VARCHAR(150)  NOT NULL,
    email            VARCHAR(100)  NOT NULL,
    telefone         VARCHAR(20)   NOT NULL,
    tipo             VARCHAR(20)   NOT NULL,
    status           VARCHAR(20)   NOT NULL,
    datahoracadastro TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_tipo
        CHECK (tipo IN ('PESSOA_FISICA', 'PESSOA_JURIDICA')),
    CONSTRAINT ck_status
        CHECK (status IN ('ATIVO', 'INATIVO', 'BLOQUEADO'))
);
```

A conexão está configurada em `ConnectionFactory.java`:

```java
var host = "jdbc:postgresql://localhost:5432/bd-api-clientes";
var user = "postgres";
var pass = "root";
```

> ⚠️ **Atenção:** em produção, nunca deixe credenciais no código. Use variáveis de ambiente ou `application.properties`.

---

## ▶️ Como Executar

**Pré-requisitos:** Java 21, Maven e PostgreSQL instalados e rodando.

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd api-clientes

# 2. Crie o banco de dados no PostgreSQL
# (execute o script.sql mostrado acima)

# 3. Execute a aplicação
./mvnw spring-boot:run
```

A API estará disponível em: `http://localhost:8080`  
Documentação Swagger: `http://localhost:8080/swagger-ui.html`

---

## 🔌 Endpoint Disponível

### `POST /api/clientes/criar`

Cadastra um novo cliente no banco de dados.

**Parâmetros (query string ou form):**

| Parâmetro | Tipo | Obrigatório | Exemplo |
|---|---|---|---|
| `nome` | String | ✅ | `João Silva` |
| `email` | String | ✅ | `joao@email.com` |
| `telefone` | String | ✅ | `11999998888` |
| `tipo` | String | ✅ | `PESSOA_FISICA` |

> O campo `status` é definido automaticamente como `ATIVO` no momento do cadastro.

**Exemplo de chamada:**

```
POST http://localhost:8080/api/clientes/criar?nome=João Silva&email=joao@email.com&telefone=11999998888&tipo=PESSOA_FISICA
```

**Respostas:**

```
✅ 200 OK → "Cliente cadastrado com sucesso"
❌ 200 OK → "Erro ao cadastrar o cliente: <mensagem>"
```

---

## 🏗 Camadas da Aplicação

```
ClienteController  →  ClienteRepository  →  ConnectionFactory  →  PostgreSQL
     (HTTP)              (SQL/JDBC)            (Conexão)            (Banco)
```

### `ClienteController`
Recebe a requisição HTTP, monta o objeto `Cliente` e delega ao repositório.

```java
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @PostMapping("criar")
    public String criar(@RequestParam String nome, ...) { ... }
}
```

### `ClienteRepository`
Executa o `INSERT` usando `PreparedStatement` — protegido contra SQL Injection.

```java
var statement = connection.prepareStatement("""
    INSERT INTO clientes (nome, email, telefone, tipo, status)
    VALUES (?, ?, ?, ?, ?)
""");
```

### `ConnectionFactory`
Centraliza a criação da conexão JDBC seguindo o padrão *Factory Method*.

```java
public static Connection getConnection() throws Exception {
    return DriverManager.getConnection(host, user, pass);
}
```

---

## 🔢 Enums

### `TipoCliente`
```java
public enum TipoCliente {
    PESSOA_FISICA,
    PESSOA_JURIDICA
}
```

### `StatusCliente`
```java
public enum StatusCliente {
    ATIVO,
    INATIVO,
    BLOQUEADO
}
```

> Enums garantem que apenas valores válidos sejam atribuídos, gerando erro em tempo de compilação caso um valor inexistente seja usado.

---

## 📚 Conceitos Aprendidos

- **`@RestController`** — combina `@Controller` + `@ResponseBody`; o retorno do método vira o corpo da resposta HTTP
- **`@RequestMapping`** — define o prefixo de rota da classe
- **`@PostMapping`** — mapeia requisições `HTTP POST` para um método
- **`@RequestParam`** — lê parâmetros enviados na URL ou no corpo do formulário
- **`@Data` (Lombok)** — gera getters, setters, `equals`, `hashCode` e `toString` automaticamente
- **`PreparedStatement`** — executa SQL parametrizado, prevenindo SQL Injection
- **`try-with-resources`** — garante o fechamento automático da conexão mesmo em caso de exceção
- **`CONSTRAINT CHECK`** — validação de valores diretamente no banco de dados
- **`SERIAL`** — tipo PostgreSQL para IDs auto-incrementados

---

*Pham Tecnologia · Fullstack Java 