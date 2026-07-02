# 📚 Library API

Uma API REST robusta para gerenciamento de biblioteca, desenvolvida com **Spring Boot 4.1**, aplicando princípios de arquitetura limpa, segurança avançada e boas práticas de desenvolvimento Java moderno.

## 🎯 Visão Geral

Library API é uma aplicação full-stack para gerenciamento completo de acervo bibliotecário, oferecendo funcionalidades de cadastro de livros, gerenciamento de autores, controle de usuários e autenticação segura com suporte a OAuth 2.0.

## ✨ Principais Características

- **API REST Completa**: Endpoints para gerenciar livros, autores e usuários com validação robusta
- **Autenticação & Autorização**: Segurança de nível enterprise com Spring Security e OAuth2
- **Login Social**: Integração com Google OAuth 2.0 para autenticação simplificada
- **Banco de Dados Relacional**: PostgreSQL com Hibernate para persistência de dados
- **Auditoria Automática**: Rastreamento de datas de criação e modificação em todas as entidades
- **Validação em Camadas**: Validação de dados com Jakarta Validation e custom validators
- **Mapping de Entidades**: MapStruct para conversão eficiente entre DTOs e entidades JPA
- **Tratamento de Exceções Centralizado**: GlobalExceptionHandler para resposta consistente de erros

## 🏗️ Arquitetura

Projeto organizado em camadas bem definidas seguindo o padrão **N-Tier Architecture**:

```
src/main/java/br/com/pedro/libraryapi/
├── controller/          # Camada de apresentação (REST endpoints)
├── service/             # Lógica de negócio
├── repository/          # Acesso a dados (JPA Repositories)
├── model/               # Entidades JPA
├── security/            # Configurações de segurança
├── config/              # Configurações gerais da aplicação
├── exceptions/          # Exceções customizadas
├── validator/           # Validadores de negócio
└── common/              # Componentes compartilhados
```

### Stack Técnico

**Backend:**
- ☕ Java 25
- 🍃 Spring Boot 4.1.0
- 🔐 Spring Security + OAuth2 Client
- 📦 Spring Data JPA + Hibernate
- 🐘 PostgreSQL 15+
- 🗺️ MapStruct 1.6.3 (Object Mapping)
- 🎯 Lombok 1.18.38 (Code Generation)

**Validação & Segurança:**
- Jakarta Validation API
- BouncyCastle 1.84 (Cryptography)
- Bean Validation (JSR-380)

**Build & Deploy:**
- Apache Maven 3.8+
- Spring Boot Maven Plugin

## 🚀 Quick Start

### Pré-requisitos

- Java 25+
- Maven 3.8+
- PostgreSQL 15+
- Google OAuth2 Credentials (para login social)

### Instalação

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/libraryapi.git
cd libraryapi
```

2. **Configure as variáveis de ambiente**

Crie um arquivo `.env` ou configure as seguintes variáveis:

```env
POSTGRES_DATABASE_URL=jdbc:postgresql://localhost:5432/libraryapi
POSTGRES_DATABASE_USERNAME=seu_usuario
POSTGRES_DATABASE_PASSWORD=sua_senha
GOOGLE_CLIENT_ID=seu_google_client_id
GOOGLE_CLIENT_SECRET=seu_google_client_secret
```

3. **Compile e execute**
```bash
mvn clean install
mvn spring-boot:run
```

A aplicação será iniciada em `http://localhost:8080`

## 📋 Endpoints Principais

### Livros
- `POST /books` - Criar novo livro
- `GET /books/{id}` - Obter livro por ID
- `PUT /books/{id}` - Atualizar livro
- `DELETE /books/{id}` - Deletar livro
- `GET /books?search=...&genre=...` - Listar livros com filtros

### Autores
- `POST /authors` - Criar novo autor
- `GET /authors/{id}` - Obter autor por ID
- `GET /authors` - Listar autores

### Usuários
- `POST /users` - Registrar novo usuário
- `GET /users` - Listar usuários (Admin only)

### Autenticação
- `POST /login` - Autenticação por credenciais
- `GET /oauth2/authorization/google` - Login com Google

## 🔐 Segurança

- **Spring Security**: Proteção de endpoints com roles e permissões
- **Autorização Baseada em Roles**: Operadores e Gerenciadores com permissões diferenciadas
- **OAuth2**: Integração com Google para autenticação federada
- **CORS**: Configuração segura de Cross-Origin Resource Sharing
- **Criptografia**: BouncyCastle para algoritmos criptográficos avançados
- **HTTPS Ready**: Preparado para deploy seguro em produção

## 📊 Modelo de Dados

### Entidades Principais

**Book (Livro)**
- ID (UUID)
- ISBN (String, unique)
- Título
- Data de Publicação
- Gênero (Enum)
- Preço (BigDecimal)
- Autor (Foreign Key)
- Data de Criação
- Data de Modificação

**Author (Autor)**
- ID (UUID)
- Nome
- Nacionalidade
- Relação One-to-Many com Livros

**User (Usuário)**
- ID (UUID)
- Login (unique)
- Senha (hasheada)
- Roles (Operador, Gerenciador)
- Data de Criação

## 🔄 Fluxo de Desenvolvimento

O projeto segue as melhores práticas:

- **Padrão DTO**: Segregação entre entidades JPA e dados de transfer
- **Mapeamento com MapStruct**: Conversion automática e type-safe entre DTOs e entidades
- **Validação em Camadas**: 
  - Nível 1: Validação de Bean (anotações)
  - Nível 2: Validação de Negócio (custom validators)
- **Tratamento de Erros**: Centralizado com GlobalExceptionHandler
- **JPA Auditing**: Rastreamento automático de auditoria nas entidades

## 🧪 Testes

Preparado com dependências para testes:
- JUnit 5
- Spring Boot Test
- Spring Security Test (em desenvolvimento)

Para rodar os testes:
```bash
mvn test
```

## 📦 Configurações por Perfil

A aplicação suporta múltiplos perfis Spring:
- `development`: Ambiente local com logging detalhado
- `production`: Otimizado para ambiente produtivo
- `test`: Configurações para testes automatizados

Configure via `application-{profile}.yaml`

## 🌐 Deploy

### Docker (Em breve)
```dockerfile
# Dockerfile será adicionado para containerização
FROM eclipse-temurin:25-jdk-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Cloud Deployment
- ☁️ AWS: RDS (PostgreSQL) + EC2/ECS
- ☁️ Azure: Azure SQL Database + App Service
- ☁️ Google Cloud: Cloud SQL + Cloud Run

## 📈 Performance & Otimização

- **Connection Pooling**: HikariCP para gerenciamento eficiente de conexões
- **Lazy Loading**: Configuração de Hibernate para evitar N+1 queries
- **Índices de Banco**: ISBN e Login indexados para buscas rápidas
- **Paginação**: Suporte a Page<T> do Spring Data para grandes datasets

## 🔄 CI/CD (Sugestão)

Sugerido configurar:
```yaml
- GitHub Actions para build automático
- SonarQube para análise de código
- Checkstyle para padrões de código
- Dependabot para atualizações de dependências
```

## 📚 Aprendizados Principais

Este projeto demonstra:

✅ Arquitetura em camadas com Spring Boot
✅ Segurança enterprise com Spring Security
✅ Autenticação OAuth2 federada
✅ Persistência com Spring Data JPA
✅ Validação robusta de dados
✅ Tratamento centralizado de exceções
✅ Mapeamento eficiente de objetos
✅ Código limpo e reutilizável

## 📝 Licença

Este projeto é licenciado sob a MIT License - veja o arquivo LICENSE para detalhes.

## 👤 Autor

**Pedro Langeli Neto** - Desenvolvedor Java 

- GitHub: [@PedrinL27](https://github.com/PedrinL27)
- LinkedIn: [Perfil-Linkedin](https://www.linkedin.com/in/pedro-langeli-neto-0612822b8/)
- Email: pedrolangelineto0@gmail.com

---

**⭐ Se este projeto foi útil, considere deixar uma estrela no GitHub!**

## 📧 Contato & Contribuições

Dúvidas ou sugestões? Abra uma issue ou entre em contato!

**Desenvolvido com ❤️ usando Spring Boot**
