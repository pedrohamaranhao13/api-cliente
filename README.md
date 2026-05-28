# ☕ API de Clientes

> API REST para gerenciamento de clientes construída com **Java 21**, **Spring Boot 4** e **PostgreSQL**, utilizando JDBC puro (sem ORM).

---

## 📋 Índice

- [Tecnologias](#-tecnologias)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [application.properties](#-applicationproperties)
- [Configuração do Banco de Dados](#-configuração-do-banco-de-dados)
- [Como Executar](#-como-executar)
- [Endpoints](#-endpoints)
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
| Bean Validation | 4.0.6 | Validação de dados de entrada |
| Maven | — | Gerenciador de dependências |

---

## 📁 Estrutura do Projeto

```
src/main/java/br/com/phamtecnologia/apiclientes/
│
├── configurations/
│   ├── CorsConfiguration.java    # Configuração de CORS
│   └── SwaggerConfiguration.java # Personalização do Swagger UI
│
├── dtos/
│   └── ClienteDto.java           # DTO com validações de entrada
│
├── enums/
│   ├── TipoCliente.java          # PESSOA_FISICA | PESSOA_JURIDICA
│   └── StatusCliente.java        # ATIVO | INATIVO | BLOQUEADO
│
├── entities/
│   ├── Cliente.java              # Modelo de dados
│   └── ClienteController.java    # Endpoints REST (CRUD completo)
│
├── factories/
│   └── ConnectionFactory.java    # Conexão JDBC com o PostgreSQL
│
├── repositories/
│   └── ClienteRepository.java    # Operações SQL (CRUD + findById)
│
└── sql/
    └── script.sql                # DDL: criação da tabela clientes

src/main/resources/
└── application.properties        # Configurações da aplicação
```

---

## ⚙️ application.properties

```properties
spring.application.name=api-clientes

server.port=8081

database.host=jdbc:postgresql://localhost:5432/bd-api-clientes
database.user=postgres
database.pass=root

cors.allowed=http://localhost:4200,http://localhost:3000
```

> ⚠️ **Atenção:** em produção, nunca commite credenciais reais. Use variáveis de ambiente.

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

    CONSTRAINT ck_tipo   CHECK (tipo   IN ('PESSOA_FISICA', 'PESSOA_JURIDICA')),
    CONSTRAINT ck_status CHECK (status IN ('ATIVO', 'INATIVO', 'BLOQUEADO'))
);
```

---

## ▶️ Como Executar

**Pré-requisitos:** Java 21, Maven e PostgreSQL instalados e rodando.

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd api-clientes

# 2. Crie o banco e execute o script.sql

# 3. Ajuste as credenciais em src/main/resources/application.properties

# 4. Execute a aplicação
./mvnw spring-boot:run
```

- API: `http://localhost:8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`

---

## 🔌 Endpoints

### `POST /api/clientes/criar`
Cadastra um novo cliente.

**Corpo (JSON):**
```json
{
  "nome":     "João da Silva",
  "email":    "joao@email.com",
  "telefone": "21999998888",
  "tipo":     "PESSOA_FISICA"
}
```

| Campo | Regra |
|---|---|
| `nome` | Obrigatório. Entre 3 e 150 caracteres. |
| `email` | Obrigatório. Formato de e-mail válido. |
| `telefone` | Obrigatório. 11 dígitos numéricos (DDD + número). |
| `tipo` | Obrigatório. `PESSOA_FISICA` ou `PESSOA_JURIDICA`. |

---

### `GET /api/clientes/consultar`
Retorna todos os clientes com status `ATIVO`, ordenados por nome.

**Resposta:**
```json
[
  { "id": 1, "nome": "João da Silva", "email": "joao@email.com", "telefone": "21999998888", "tipo": "PESSOA_FISICA", "status": "ATIVO" }
]
```

---

### `GET /api/clientes/obter/{id}`
Retorna um único cliente pelo id. Retorna `null` se não encontrado ou inativo.

```
GET /api/clientes/obter/1  →  { "id": 1, "nome": "João", ... }
GET /api/clientes/obter/99 →  null
```

---

### `PUT /api/clientes/atualizar/{id}`
Atualiza os dados de um cliente existente.

**Corpo (JSON):** mesmo formato do POST (sem `status`).

---

### `DELETE /api/clientes/excluir/{id}`
**Soft delete** — altera o status do cliente para `INATIVO`. O registro permanece no banco.

---

## 🏗 Camadas da Aplicação

```
ClienteController  →  ClienteRepository  →  ConnectionFactory  →  PostgreSQL
  (HTTP / DTO)       (CRUD / JDBC)           (@Component)          (Banco)
```

### `ClienteController`
```java
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    @Autowired private ClienteRepository clienteRepository;

    @PostMapping("criar")                                    // INSERT
    @GetMapping("consultar")                                 // SELECT todos
    @GetMapping("obter/{id}")                                // SELECT por id
    @PutMapping("atualizar/{id}")                            // UPDATE
    @DeleteMapping("excluir/{id}")                           // Soft DELETE
}
```

### `ClienteRepository`
```java
@Repository
public class ClienteRepository {
    @Autowired private ConnectionFactory connectionFactory;

    public void create(Cliente c)            // INSERT
    public List<Cliente> findAll()           // SELECT WHERE status='ATIVO' ORDER BY nome
    public Cliente findById(Integer id)      // SELECT WHERE id=? AND status='ATIVO'
    public boolean update(Cliente c)         // UPDATE SET nome, email, telefone, tipo
    public boolean delete(Integer id)        // UPDATE SET status='INATIVO'
}
```

> `update()` e `delete()` retornam `boolean` — `true` se alguma linha foi afetada.  
> `findById()` retorna `null` se o cliente não existir ou estiver inativo.

### `ClienteDto`
```java
@Data
public class ClienteDto {
    @NotEmpty @Size(min=3, max=150)                    private String nome;
    @NotEmpty @Email                                   private String email;
    @NotEmpty @Pattern(regexp="^\\d{11}$")             private String telefone;
    @NotEmpty @Pattern(regexp="^(PESSOA_FISICA|...)$") private String tipo;
}
```

### `ConnectionFactory`
```java
@Component
public class ConnectionFactory {
    @Value("${database.host}") private String host;
    @Value("${database.user}") private String user;
    @Value("${database.pass}") private String pass;

    public Connection getConnection() throws Exception {
        return DriverManager.getConnection(host, user, pass);
    }
}
```

### `SwaggerConfiguration`
```java
@Configuration
public class SwaggerConfiguration {
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info()
            .title("API de Clientes - Pham Tecnologia")
            .version("1.0.0")
            .description("Documentação da API de Clientes"));
    }
}
```

---

## 🔢 Enums

```java
public enum TipoCliente   { PESSOA_FISICA, PESSOA_JURIDICA }
public enum StatusCliente { ATIVO, INATIVO, BLOQUEADO }
```

---

## 🌐 Integração com o Frontend

A API aceita requisições do frontend Angular via `CorsConfiguration`. Origens configuradas em `application.properties`:

```properties
cors.allowed=http://localhost:4200,http://localhost:3000
```

---

## 📚 Conceitos Aprendidos

- **`@RestController` / `@RequestMapping`** — define a classe como controller REST com prefixo de rota
- **`@PostMapping` / `@GetMapping` / `@PutMapping` / `@DeleteMapping`** — mapeamento dos verbos HTTP
- **`@RequestBody`** — desserializa JSON do corpo para objeto Java
- **`@PathVariable`** — extrai variável dinâmica da URL (`/obter/{id}`)
- **`@Autowired`** — injeção de dependência automática pelo Spring
- **`@Component` / `@Repository`** — registro de beans no contexto Spring
- **`@Value("${chave}")`** — injeta propriedades do `application.properties`
- **`@Data` (Lombok)** — geração automática de getters/setters
- **`Bean Validation`** — `@NotEmpty`, `@Email`, `@Size`, `@Pattern`
- **`PreparedStatement`** — SQL parametrizado, prevenindo SQL Injection
- **`executeQuery()`** — executa SELECT, retorna ResultSet
- **`executeUpdate()`** — executa INSERT/UPDATE/DELETE, retorna linhas afetadas
- **`ResultSet`** — cursor para percorrer resultados; `if` para 1 resultado, `while` para N
- **`Soft Delete`** — exclusão lógica via `UPDATE SET status = 'INATIVO'`
- **`SwaggerConfiguration`** — personaliza título, versão e descrição do Swagger UI
- **`CORS`** — permite requisições cross-origin do Angular

---

*Pham Tecnologia · Fullstack Java -*