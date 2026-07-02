# 🏗️ Documentação de Arquitetura - Library API

## 1. Visão Geral Arquitetural

Library API segue uma **arquitetura em camadas (N-Tier)** com separação clara de responsabilidades, favorecendo manutenibilidade, testabilidade e escalabilidade.

```
┌─────────────────────────────────────────────┐
│           Presentation Layer                 │
│   (REST Controllers, Exception Handlers)     │
└────────────────┬────────────────────────────┘
                 │
┌────────────────┴────────────────────────────┐
│          Business Logic Layer                │
│   (Services, Validators, Business Rules)    │
└────────────────┬────────────────────────────┘
                 │
┌────────────────┴────────────────────────────┐
│       Data Access Layer                      │
│   (JPA Repositories, Specifications)        │
└────────────────┬────────────────────────────┘
                 │
┌────────────────┴────────────────────────────┐
│      Database Layer                          │
│   (PostgreSQL, Entity Mapping)               │
└─────────────────────────────────────────────┘
```

## 2. Componentes Principais

### 2.1 Camada de Apresentação (Presentation Layer)

**Responsabilidade**: Expor endpoints REST e gerenciar requisições HTTP.

**Componentes:**

```
controller/
├── BookController        # CRUD de livros
├── AuthorController      # CRUD de autores
├── UserController        # Gerenciamento de usuários
├── LoginViewController   # Login e OAuth2
└── GenericController     # Interface base com helpers
```

**Exemplo de Padrão:**
```java
@RestController
@RequestMapping("books")
@PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER')")
public class BookController {
    // Endpoints com validação e tratamento de erro centralizado
}
```

**Sub-pacote: DTO (Data Transfer Objects)**
- `SaveBookDTO`: Dados para criar/atualizar livro
- `FindResultBookDTO`: Resposta com dados de livro
- `ResponseError`: Estrutura padronizada de erro
- Mappers: Conversão automática com MapStruct

### 2.2 Camada de Lógica de Negócio (Business Logic Layer)

**Responsabilidade**: Implementar regras de negócio e orquestração.

**Componentes:**

```
service/
├── BookService      # Lógica de livros (validação, duplicação)
├── AuthorService    # Lógica de autores
└── UserService      # Autenticação, roles, permissões

validator/
├── BookValidator    # Regras específicas de livro
└── AuthorValidator  # Regras específicas de autor
```

**Padrão Service:**
```java
@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository repository;
    private final BookValidator validator;
    
    public void save(Book book) {
        validator.validate(book);  // Validação de negócio
        repository.save(book);
    }
}
```

### 2.3 Camada de Acesso a Dados (Data Access Layer)

**Responsabilidade**: Abstrair acesso a dados e queries.

**Componentes:**

```
repository/
├── BookRepository       # Extends JpaRepository
├── AuthorRepository     # Extends JpaRepository
├── UserRepository       # Extends JpaRepository
└── specs/
    └── BookSpecs       # Query specifications para filtros
```

**Exemplo com Specifications:**
```java
public interface BookRepository extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
    // Queries customizadas quando necessário
}

// BookSpecs fornece predicados reutilizáveis para filtros dinâmicos
```

### 2.4 Camada de Modelo (Domain Layer)

**Responsabilidade**: Definir entidades JPA e regras de persistência.

**Componentes:**

```
model/
├── Book         # Entidade com auditoria
├── Author       # Entidade com relacionamento
├── User         # Entidade com segurança
└── BookGenre    # Enumeração de gêneros
```

**Características:**
- ✅ Auditoria automática (`@CreatedDate`, `@LastModifiedDate`)
- ✅ UUID gerado automaticamente no banco
- ✅ Relacionamentos tipados (ManyToOne, OneToMany)
- ✅ Validações de coluna (nullable, length)

## 3. Fluxo de Requisição (Request Flow)

```
┌─────────────────────────────────────────────────────────┐
│ 1. Requisição HTTP chega em BookController              │
│    POST /books { titulo, isbn, ... }                    │
└────────────┬────────────────────────────────────────────┘
             │
┌────────────┴────────────────────────────────────────────┐
│ 2. @Valid dispara Bean Validation                       │
│    - Verifica @NotNull, @Size, etc                      │
│    - GlobalExceptionHandler captura erros               │
└────────────┬────────────────────────────────────────────┘
             │
┌────────────┴────────────────────────────────────────────┐
│ 3. BookMapper.toEntity() converte SaveBookDTO → Book    │
└────────────┬────────────────────────────────────────────┘
             │
┌────────────┴────────────────────────────────────────────┐
│ 4. BookService.save() executa:                          │
│    - BookValidator.validate(book)                       │
│    - Verifica duplicação de ISBN                        │
│    - Aplica regras de negócio                           │
└────────────┬────────────────────────────────────────────┘
             │
┌────────────┴────────────────────────────────────────────┐
│ 5. BookRepository.save() persiste no PostgreSQL         │
│    - Hibernate mapeia Book → tabela book                │
│    - Auditoria registra data de criação                 │
│    - UUID gerado pelo banco (native)                    │
└────────────┬────────────────────────────────────────────┘
             │
┌────────────┴────────────────────────────────────────────┐
│ 6. Controller retorna ResponseEntity.created(uri)       │
│    HTTP 201 CREATED com Location header                 │
└─────────────────────────────────────────────────────────┘
```

## 4. Segurança (Security Architecture)

```
┌──────────────────────────────────────────────────┐
│           Authentication Flow                    │
└──────────────────────────────────────────────────┘

Opção 1: Login Tradicional
  ┌─────────────────────────────────┐
  │ POST /login { login, password }  │
  └────────────┬────────────────────┘
               │
        ┌──────┴──────────────┐
        │                     │
   ┌────▼─────────────┐  ┌────▼────────────────┐
   │ CustomUserDetail │  │ CustomAuthentication│
   │ Service          │  │ Provider            │
   └────┬─────────────┘  └────┬────────────────┘
        │                     │
        └──────────┬──────────┘
                   │
          ┌────────▼──────────┐
          │ Spring Security   │
          │ (Token/Session)   │
          └────────┬──────────┘
                   │
          ┌────────▼──────────┐
          │ HTTP 200 OK       │
          │ com JWT/Cookie    │
          └───────────────────┘

Opção 2: Login Social (Google OAuth2)
  ┌─────────────────────────────────┐
  │ GET /oauth2/authorization/google│
  └────────────┬────────────────────┘
               │
        ┌──────▼──────────────┐
        │ Redirect to Google   │
        │ OAuth2 Provider      │
        └──────┬───────────────┘
               │
        ┌──────▼──────────────┐
        │ User autentica com  │
        │ Google              │
        └──────┬───────────────┘
               │
        ┌──────▼──────────────┐
        │ Google retorna auth │
        │ code                │
        └──────┬───────────────┘
               │
        ┌──────▼────────────────────┐
        │ Spring OAuth2Client       │
        │ troca code por token      │
        └──────┬────────────────────┘
               │
        ┌──────▼──────────────┐
        │ SocialLoginSuccessH │
        │ andler cria usuário │
        └──────┬───────────────┘
               │
        ┌──────▼──────────────┐
        │ Redirect para home  │
        │ com autenticação    │
        └──────────────────────┘
```

**Autorização por Roles:**
```
┌─────────────────────────────────┐
│ @PreAuthorize Annotations       │
├─────────────────────────────────┤
│ OPERATOR → Leitura/Criação      │
│ MANAGER  → CRUD Completo        │
│ ADMIN    → Gerenciar Usuários   │
└─────────────────────────────────┘
```

## 5. Padrões de Design Utilizados

### 5.1 **Padrão Repository**
```java
// Abstração de acesso a dados
public interface BookRepository extends JpaRepository<Book, UUID> { }
```

### 5.2 **Padrão Service**
```java
// Encapsulamento de lógica de negócio
@Service
public class BookService { }
```

### 5.3 **Padrão DTO (Data Transfer Object)**
```java
// Segregação entre entidade e dados transferidos
public record SaveBookDTO(String isbn, String title, ...) { }
```

### 5.4 **Padrão Mapper (com MapStruct)**
```java
// Conversão automática type-safe
@Mapper
public interface BookMapper {
    Book toEntity(SaveBookDTO dto);
    FindResultBookDTO toDTO(Book book);
}
```

### 5.5 **Padrão Strategy (Validators)**
```java
// Validação plugável
public interface Validator {
    void validate(Book book);
}
```

### 5.6 **Padrão Specification (JPA)**
```java
// Queries dinâmicas reutilizáveis
public class BookSpecs {
    public static Specification<Book> byGenre(BookGenre genre) { ... }
}
```

## 6. Dependências e Responsabilidades

```
BookController
    ↓ (depende de)
    ├── BookService
    │   ├── BookValidator
    │   └── BookRepository
    │       └── Book Entity
    │
    ├── BookMapper
    │   └── BookDTO classes
    │
    └── Security
        └── CustomUserDetailService
```

## 7. Tratamento de Erros

**Centralizado com GlobalExceptionHandler:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseError> handleValidationError(...) {
        // Mapeia erros de validação para resposta estruturada
    }
    
    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ResponseError> handleDuplicateError(...) {
        // Trata duplicação de chaves únicas
    }
}
```

**Estrutura de Resposta:**
```json
{
    "timestamp": "2024-01-15T10:30:00Z",
    "status": 400,
    "message": "Erro de validação",
    "errors": [
        {
            "field": "isbn",
            "message": "ISBN não pode estar vazio"
        }
    ]
}
```

## 8. Configurações de Ambiente

**Hierarquia de Configurações:**
```
application.yaml (base)
  ├── application-dev.yaml (desenvolvimento)
  ├── application-prod.yaml (produção)
  └── application-test.yaml (testes)
```

**Variáveis Externas (Environment):**
- `POSTGRES_DATABASE_URL`: Conexão PostgreSQL
- `POSTGRES_DATABASE_USERNAME`: Usuário BD
- `POSTGRES_DATABASE_PASSWORD`: Senha BD
- `GOOGLE_CLIENT_ID`: Google OAuth2 Client ID
- `GOOGLE_CLIENT_SECRET`: Google OAuth2 Secret

## 9. Diagrama de Relacionamento entre Entidades

```
┌──────────────┐
│   Author     │
├──────────────┤
│ id (UUID)    │
│ name         │
│ nationality  │
└──────┬───────┘
       │
       │ 1 (um autor)
       │
       │ n (muitos livros)
       │
       ▼
┌──────────────┐
│   Book       │
├──────────────┤
│ id (UUID)    │
│ isbn         │
│ title        │
│ genre        │
│ price        │
│ author_id (FK)
└──────────────┘

┌──────────────┐
│   User       │
├──────────────┤
│ id (UUID)    │
│ login        │
│ password     │
│ roles        │
└──────────────┘
```

## 10. Pontos de Melhoria Futuros

- [ ] Implementar Rate Limiting (Bucket4j)
- [ ] Cache com Redis (dados frequentes)
- [ ] Logging centralizado (ELK Stack)
- [ ] Metrics com Micrometer/Prometheus
- [ ] API Documentation com SpringDoc OpenAPI/Swagger
- [ ] Testes de Integração com TestContainers
- [ ] Containerização com Docker
- [ ] Deployment em Kubernetes
- [ ] GraphQL como alternativa REST
- [ ] Message Queue (RabbitMQ) para operações assíncronas

---

**Documento atualizado**: 2024
**Versão**: 1.0
