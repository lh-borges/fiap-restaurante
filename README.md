# 🍽️ API Restaurante FIAP - Sistema de Gestão de Usuários


[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)


Sistema backend desenvolvido para o **Tech Challenge - Fase 01** do curso de Pós-Graduação em Arquitetura de Software da FIAP. Implementa um sistema completo de gestão de usuários para um grupo de restaurantes, com autenticação JWT, controle de acesso baseado em roles e APIs REST versionadas.

---


## 📋 Índice



- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [Documentação da API](#documentação-da-api)
- [Endpoints Principais](#endpoints-principais)
- [Testes com Postman](#testes-com-postman)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Estrutura do Banco de Dados](#estrutura-do-banco-de-dados)
- [Autores](#autores)



---


## 📖 Sobre o Projeto


A **API Restaurante FIAP** é uma aplicação backend que gerencia usuários em um sistema de restaurantes. O sistema suporta diferentes perfis de usuário (Dono de Restaurante, Cliente e Master), oferecendo:


- ✅ Autenticação segura com JWT (JSON Web Tokens)
- ✅ Controle de acesso baseado em roles (RBAC)
- ✅ Operações CRUD completas para usuários
- ✅ Validação de dados com Bean Validation
- ✅ Tratamento de erros padronizado (RFC 7807 - Problem Details)
- ✅ Soft delete para exclusões lógicas
- ✅ Documentação automática com Swagger/OpenAPI
- ✅ Containerização com Docker



---



## 🚀 Tecnologias Utilizadas



### Backend

- **Java 21** - Linguagem de programação

- **Spring Boot 3.5.7** - Framework principal

- **Spring Security** - Autenticação e autorização

- **Spring Data JPA** - Persistência de dados

- **Hibernate** - ORM (Object-Relational Mapping)

- **Bean Validation** - Validação de dados



### Segurança

- **JWT (JSON Web Token)** - Autenticação stateless

- **BCrypt** - Hash de senhas



### Banco de Dados

- **MySQL 8.0** - Banco de dados relacional

- **Flyway** - Controle de versão de migrations



### Documentação

- **Swagger/OpenAPI 3** - Documentação interativa da API

- **Springdoc OpenAPI** - Geração automática de documentação



### DevOps

- **Docker** - Containerização

- **Docker Compose** - Orquestração de containers

- **Maven** - Gerenciamento de dependências e build



---



## ⚙️ Funcionalidades



### Gestão de Usuários

- ✅ **Cadastro de usuários** com validação completa de dados

- ✅ **Autenticação** via login e senha com geração de token JWT

- ✅ **Atualização de perfil** (nome, telefone, endereço)

- ✅ **Troca de senha** com validação da senha atual

- ✅ **Busca de usuários** por ID, e-mail ou nome

- ✅ **Listagem paginada** de usuários ativos

- ✅ **Exclusão lógica** (soft delete) de usuários



### Segurança

- ✅ **Controle de acesso baseado em roles:**

    - `MASTER` - Acesso total ao sistema

    - `DONO_RESTAURANTE` - Gestão de usuários

    - `CLIENTE` - Acesso limitado ao próprio perfil

- ✅ **Autenticação JWT** com expiração configurável

- ✅ **Proteção de endpoints** com Spring Security

- ✅ **Validação de ownership** (usuário só altera próprios dados)



### Validações

- ✅ **E-mail único** no sistema

- ✅ **Login único** no sistema

- ✅ **Validação de senha forte** (mínimo 8 caracteres, letras, números e símbolos)

- ✅ **Validação de telefone** (padrão brasileiro)

- ✅ **Validação de campos obrigatórios**



---



## 🏗️ Arquitetura



O projeto segue uma **arquitetura em camadas** (Layered Architecture):



```

src/main/java/com/restaurantefiap/

│
├── controller/          # Camada de apresentação (REST Controllers)
│   ├── AuthController.java
│   └── UsuarioController.java
│
├── service/             # Camada de negócio (Business Logic)
│   ├── UsuarioService.java
│   └── JwtService.java
│
├── repository/          # Camada de persistência (Data Access)
│   └── UsuarioRepository.java
│
├── entities/            # Entidades JPA
│   ├── usuario/
│   │   └── Usuario.java
│   └── endereco/
│       └── Endereco.java
│
├── dto/                 # Data Transfer Objects
│   ├── request/
│   │   ├── UsuarioRequestDTO.java
│   │   ├── UsuarioUpdateDTO.java
│   │   ├── AlterarSenhaRequestDTO.java
│   │   └── AuthRequest.java
│   └── response/
│       ├── UsuarioResponseDTO.java
│       └── AuthResponse.java
│
├── security/            # Configuração de segurança
│   ├── SecurityConfig.java
│   ├── JwtAuthenticationFilter.java
│   └── JpaUserDetailsService.java
│
├── exception/           # Tratamento de exceções
│   ├── GlobalExceptionHandler.java
│   └── [Custom Exceptions]
│
├── enums/               # Enumerações
│   └── Role.java
│
└── config/              # Configurações
    └── SwaggerConfig.java

```

---

## 📦 Pré-requisitos

### Opção 1: Execução com Docker (Recomendado)
- [Docker](https://www.docker.com/get-started) 20.10+
- [Docker Compose](https://docs.docker.com/compose/install/) 2.0+

### Opção 2: Execução Local

- [Java JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven 3.9+](https://maven.apache.org/download.cgi)
- [MySQL 8.0+](https://dev.mysql.com/downloads/)

---

## 🐳 Instalação e Execução

### **Opção 1: Docker Compose (Recomendado)**
#### 1. Clone o repositório
```bash

git clone https://github.com/lh-borges/fiap-restaurante.git
cd fiap-restaurante
```

#### 2. Execute com Docker Compose
```bash
docker-compose up --build
```
#### 3. Aguarde a inicialização
- O MySQL será inicializado primeiro (com healthcheck)
- A API será iniciada após o MySQL estar pronto
- A aplicação estará disponível em: **http://localhost:8080**



#### 4. Acesse a documentação Swagger
```
http://localhost:8080/swagger-ui.html
```

#### 5. Para parar a aplicação
```bash
docker-compose down
```

#### 6. Para limpar volumes (resetar banco de dados)
```bash
docker-compose down -v
```
---

### **Opção 2: Execução Local (Desenvolvimento)**

#### 1. Configure o MySQL
```sql
CREATE DATABASE fiap_restaurante;
CREATE USER 'fiap_user'@'localhost' IDENTIFIED BY '123456';
GRANT ALL PRIVILEGES ON fiap_restaurante.* TO 'fiap_user'@'localhost';
FLUSH PRIVILEGES;
```

#### 2. Configure as variáveis de ambiente
```bash
export DB_HOST=localhost
export DB_PORT=3306
export MYSQL_DATABASE=fiap_restaurante
export MYSQL_USER=fiap_user
export MYSQL_PASSWORD=123456
export JWT_SECRET=Z5Km3kKvn4wRyK5t7iyW0Xn4AfFY8KpHkDy5FS3o8l0=
export SPRING_PROFILES_ACTIVE=dev
```
#### 3. Compile e execute
```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```



#### 4. A aplicação estará disponível em:
```
http://localhost:8080
```

---



## 📚 Documentação da API

### Swagger UI (Documentação Interativa)
```
http://localhost:8080/swagger-ui.html
```
### OpenAPI JSON
```
http://localhost:8080/v3/api-docs
```



A documentação Swagger permite:
- ✅ Visualizar todos os endpoints disponíveis
- ✅ Testar requisições diretamente no navegador
- ✅ Ver exemplos de requisições e respostas
- ✅ Entender os modelos de dados (schemas)

---

## 🔌 Endpoints Principais

### **Autenticação**

#### Login

```http
POST /auth/login
Content-Type: application/json
{
  "login": "admin",
  "password": "senhaParaTeste@2025"
}
```

**Resposta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### **Usuários**

> **⚠️ Nota:** Todos os endpoints de usuários exigem autenticação via token JWT.
>
> **Header obrigatório:**
> ```
> Authorization: Bearer {seu-token-jwt}
> ```

#### Criar Usuário
```http
POST /usuarios
Authorization: Bearer {token}
Content-Type: application/json
{
  "login": "cliente01",
  "email": "cliente01@email.com",
  "nome": "João da Silva",
  "telefone": "11987654321",
  "role": "CLIENTE",
  "password": "Senha@123",
  "endereco": {
    "logradouro": "Rua das Flores",
    "numero": "123",
    "complemento": "Apto 45",
    "bairro": "Centro",
    "cep": "01234567",
    "cidade": "São Paulo",
    "uf": "SP"
  }
}

```
#### Buscar Usuário por ID
```http
GET /usuarios/{id}
Authorization: Bearer {token}
```

#### Buscar Usuários por Nome

```http
GET /usuarios/buscar?nome=João
Authorization: Bearer {token}
```

#### Listar Usuários (Paginado)

```http
GET /usuarios/page?page=0&size=10
Authorization: Bearer {token}
```

#### Atualizar Usuário

```http
PUT /usuarios/{id}
Authorization: Bearer {token}
Content-Type: application/json
{
  "nome": "João Silva Santos",
  "telefone": "11999887766"
}

```

#### Alterar Senha

```http
PUT /usuarios/{id}/senha
Authorization: Bearer {token}
Content-Type: application/json
{
  "senhaAtual": "Senha@123",
  "novaSenha": "NovaSenha@456"
}

```

#### Excluir Usuário (Soft Delete)
```http
DELETE /usuarios/{id}
Authorization: Bearer {token}
```

---

## 📮 Testes com Postman

### Importar Coleção

1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo `Api Restaurante.json` (na raiz do projeto)
4. Configure as variáveis de ambiente:
    - `baseUrl`: `http://localhost:8080`
    - `token`: (será preenchido automaticamente após login)

### Fluxo de Teste Recomendado

#### 1. Autenticar
```
POST {{baseUrl}}/auth/login
```
- Copie o token retornado
- Configure a variável `{{token}}` no Postman

#### 2. Criar Usuário

```
POST {{baseUrl}}/usuarios
```

#### 3. Buscar Usuário

```
GET {{baseUrl}}/usuarios/1
```

#### 4. Atualizar Usuário
```
PUT {{baseUrl}}/usuarios/1
```

#### 5. Alterar Senha
```
PUT {{baseUrl}}/usuarios/1/senha
```


#### 6. Buscar por Nome

```
GET {{baseUrl}}/usuarios/buscar?nome=João
```

---

## 🔐 Variáveis de Ambiente

### Variáveis Disponíveis no Docker Compose

| Variável | Descrição | Valor Padrão |
|----------|-----------|--------------|
| `MYSQL_ROOT_PASSWORD` | Senha do root do MySQL | `root` |
| `MYSQL_DATABASE` | Nome do banco de dados | `fiap_restaurante` |
| `MYSQL_USER` | Usuário do banco | `fiap_user` |
| `MYSQL_PASSWORD` | Senha do usuário | `123456` |
| `JWT_SECRET` | Chave secreta para JWT | (gerada) |
| `JWT_EXPIRATION` | Tempo de expiração do token (ms) | `86400000` (24h) |
| `SERVER_PORT` | Porta da aplicação | `8080` |
| `SPRING_PROFILES_ACTIVE` | Profile do Spring | `docker` |


### Exemplo de Arquivo `.env`

Você pode criar um arquivo `.env` na raiz do projeto:

```env
MYSQL_ROOT_PASSWORD=minha_senha_root
MYSQL_DATABASE=restaurante_db
MYSQL_USER=app_user
MYSQL_PASSWORD=app_password
JWT_SECRET=sua-chave-secreta-super-segura-aqui
JWT_EXPIRATION=86400000
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=docker
```

---

## 🗄️ Estrutura do Banco de Dados

### Tabela: `usuarios`

| Campo | Tipo | Constraints | Descrição |
|-------|------|-------------|-----------|
| `id` | BIGINT | PK, AUTO_INCREMENT | Identificador único |
| `login` | VARCHAR(100) | NOT NULL, UNIQUE | Login do usuário |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | E-mail do usuário |
| `nome` | VARCHAR(255) | NOT NULL | Nome completo |
| `telefone` | VARCHAR(20) | NOT NULL | Telefone (padrão BR) |
| `role` | VARCHAR(30) | NOT NULL | Perfil (MASTER, DONO_RESTAURANTE, CLIENTE) |
| `password` | VARCHAR(255) | NOT NULL | Senha hasheada (BCrypt) |
| `logradouro` | VARCHAR(255) | NULL | Endereço - Logradouro |
| `numero` | VARCHAR(20) | NULL | Endereço - Número |
| `complemento` | VARCHAR(100) | NULL | Endereço - Complemento |
| `bairro` | VARCHAR(100) | NULL | Endereço - Bairro |
| `cep` | VARCHAR(8) | NULL | Endereço - CEP |
| `cidade` | VARCHAR(100) | NULL | Endereço - Cidade |
| `uf` | VARCHAR(2) | NULL | Endereço - UF |
| `criado_em` | DATETIME | NOT NULL | Data de criação |
| `atualizado_em` | DATETIME | NOT NULL | Data da última atualização |
| `deletado_em` | DATETIME | NULL | Data de exclusão (soft delete) |



### Índices

- `idx_usuarios_deletado_em` - Otimiza queries de usuários ativos

---



## 👥 Autores



**Equipe A - Tech Challenge Fase 01**

- - **Gilmar** - Desenvolvimento backend
- **Thiago de Jesus** - Desenvolvimento backend
- **Danilo Fernando** - Desenvolvimento backend, Segurança
- **Juliana Olio** - Desenvolvimento backend, Controllers
- **Luiz Borges** - Desenvolvimento backend

---
## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 📞 Suporte

Para dúvidas ou sugestões, entre em contato:
- 
- **E-mail:** restaurante@fiap.com.br

- **Website:** [https://www.fiap.com.br](https://www.fiap.com.br)


---

## 🎯 Roadmap de melhorias pendentes


- [ ] Implementação de testes automatizados (JUnit 5 + Mockito)
- [ ] Integração com serviço de e-mail (confirmação de cadastro)
- [ ] Cache com Redis
- [ ] Métricas com Spring Actuator + Prometheus
- [ ] CI/CD com GitHub Actions
- [ ] Deploy em Cloud (AWS/Azure/GCP/OCI)

---

## 🙏 Agradecimentos

- **FIAP** - Pela oportunidade de aprendizado
- **Professores e Mentores** - Pelo suporte e orientação
- **Equipe** - Pela colaboração e dedicação

---

**Desenvolvido com ☕ e 💻 pela Equipe A - FIAP 2025**