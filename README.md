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
| Bean Validation | 4.0.6 | Validação de dados de entrada |
| Maven | — | Gerenciador de dependências |

---

## 📁 Estrutura do Projeto

```
src/main/java/br/com/phamtecnologia/apiclientes/
│
├── configurations/
│   └── CorsConfiguration.java  # Configuração de CORS
│
├── dtos/
│   └── ClienteDto.java         # DTO com validações de entrada
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

src/main/resources/
└── application.properties      # Configurações da aplicação
```

---

## ⚙️ application.properties

Todas as configurações ficam centralizadas em `src/main/resources/application.properties`:

```properties
spring.application.name=api-clientes

server.port=8081

database.host=jdbc:postgresql://localhost:5432/bd-api-clientes
database.user=postgres
database.pass=root

cors.allowed=http://localhost:4200,http://localhost:3000
```

> ⚠️ **Atenção:** em produção, nunca commite credenciais reais. Use variáveis de ambiente ou um cofre de segredos.

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

---

## ▶️ Como Executar

**Pré-requisitos:** Java 21, Maven e PostgreSQL instalados e rodando.

```bash
# 1. Clone o repositório
git clone <url-do-repositorio>
cd api-clientes

# 2. Crie o banco de dados no PostgreSQL
# (execute o script.sql mostrado acima)

# 3. Ajuste as credenciais em src/main/resources/application.properties

# 4. Execute a aplicação
./mvnw spring-boot:run
```

A API estará disponível em: `http://localhost:8081`  
Documentação Swagger: `http://localhost:8081/swagger-ui.html`

---

## 🔌 Endpoint Disponível

### `POST /api/clientes/criar`

Cadastra um novo cliente no banco de dados.

**Corpo da requisição (JSON):**

```json
{
  "nome":     "João da Silva",
  "email":    "joao@email.com",
  "telefone": "21999998888",
  "tipo":     "PESSOA_FISICA"
}
```

**Regras de validação:**

| Campo | Regra |
|---|---|
| `nome` | Obrigatório. Entre 3 e 150 caracteres. |
| `email` | Obrigatório. Deve estar em formato de e-mail válido. |
| `telefone` | Obrigatório. Somente números: 2 dígitos de DDD + 9 dígitos (ex: `21999998888`). |
| `tipo` | Obrigatório. Deve ser `PESSOA_FISICA` ou `PESSOA_JURIDICA`. |

> O campo `status` é definido automaticamente como `ATIVO` no momento do cadastro.

**Respostas:**

```
✅ 200 OK → "Cliente cadastrado com sucesso"
❌ 200 OK → "Erro ao cadastrar o cliente: <mensagem>"
```

---

## 🏗 Camadas da Aplicação

```
ClienteController  →  ClienteRepository  →  ConnectionFactory  →  PostgreSQL
  (HTTP / DTO)          (SQL/JDBC)            (@Component)          (Banco)
```

### `ClienteController`
Recebe o JSON da requisição via `@RequestBody`, usa `@Autowired` para injetar o repositório.

```java
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @PostMapping("criar")
    public String criar(@RequestBody ClienteDto dto) { ... }
}
```

### `ClienteDto`
DTO com Bean Validation — representa e valida o corpo da requisição antes de chegar ao domínio.

```java
@Data
public class ClienteDto {
    @NotEmpty @Size(min = 3, max = 150)
    private String nome;

    @NotEmpty @Email
    private String email;

    @NotEmpty @Pattern(regexp = "^\\d{2}\\d{9}$")
    private String telefone;

    @NotEmpty @Pattern(regexp = "^(PESSOA_FISICA|PESSOA_JURIDICA)$")
    private String tipo;
}
```

### `ClienteRepository`
Executa o `INSERT` com `PreparedStatement` via JDBC. A `ConnectionFactory` é injetada pelo Spring.

```java
@Repository
public class ClienteRepository {

    @Autowired
    private ConnectionFactory connectionFactory;

    public void create(Cliente cliente) throws Exception {
        try (var connection = connectionFactory.getConnection()) { ... }
    }
}
```

### `ConnectionFactory`
Componente Spring que lê as credenciais do `application.properties` e fornece a conexão JDBC.

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

---

## 🌐 Integração com o Frontend

A API aceita requisições do frontend Angular (`web-clientes`) via `CorsConfiguration`. As origens permitidas são configuradas no `application.properties`:

```properties
cors.allowed=http://localhost:4200,http://localhost:3000
```

```java
@Configuration
@EnableWebMvc
public class CorsConfiguration implements WebMvcConfigurer {

    @Value("${cors.allowed}")
    private String[] corsAllowed;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsAllowed)
                .allowedMethods("POST", "PUT", "DELETE", "GET")
                .allowedHeaders("*");
    }
}
```

---

## 📚 Conceitos Aprendidos

- **`@RestController`** — combina `@Controller` + `@ResponseBody`
- **`@RequestMapping` / `@PostMapping`** — mapeamento de rotas HTTP
- **`@RequestBody`** — desserializa o corpo JSON da requisição para um objeto Java
- **`@Component` / `@Repository`** — registra classes como beans gerenciados pelo Spring
- **`@Autowired`** — injeção de dependência automática pelo Spring (IoC)
- **`@Value("${chave}")`** — injeta propriedades do `application.properties`
- **`@Data` (Lombok)** — gera getters, setters, `equals`, `hashCode` e `toString`
- **`DTO`** — separa o contrato da API do modelo de domínio interno
- **`Bean Validation`** — validações declarativas com `@NotEmpty`, `@Email`, `@Size`, `@Pattern`
- **`PreparedStatement`** — SQL parametrizado, prevenindo SQL Injection
- **`try-with-resources`** — fechamento automático da conexão JDBC
- **`CORS`** — configuração para permitir requisições cross-origin do Angular
- **`CONSTRAINT CHECK`** — validação de valores diretamente no PostgreSQL

---

*Pham Tecnologia · Fullstack Java — *