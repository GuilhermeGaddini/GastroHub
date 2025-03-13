# 🍽️ GastroHub

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F.svg?style=for-the-badge&logo=Spring-Boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-2496ED.svg?style=for-the-badge&logo=Docker&logoColor=white)](https://www.docker.com/)

> 🎓 Projeto desenvolvido como parte do curso de Pós-Graduação em Arquitetura e Desenvolvimento Java da FIAP.

Sistema de gestão de operações para restaurantes com funcionalidades para clientes, incluindo consultas de informações, avaliações e pedidos online.

## 📋 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias](#-tecnologias)
- [Funcionalidades](#-funcionalidades)
- [Começando](#-começando)
- [Documentação da API](#-documentação-da-api)
- [Contribuição](#-contribuição)
- [Licença](#-licença)
- [Contato](#-contato)

## 🚀 Sobre o Projeto

GastroHub é uma solução completa desenvolvida durante o curso de Pós-Graduação em Arquitetura e Desenvolvimento Java da FIAP. O projeto demonstra a aplicação prática de padrões de arquitetura, boas práticas de desenvolvimento e tecnologias modernas do ecossistema Java.

### 🎯 Objetivo Acadêmico

Este projeto serve como aplicação prática dos conceitos aprendidos durante o curso, incluindo:
- Arquitetura de Microsserviços
- Design Patterns
- Clean Code
- DevOps e Containerização
- APIs RESTful

## 🛠️ Tecnologias

- ![Java](https://img.shields.io/badge/Java-21-orange)
- ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
- ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
- ![Docker](https://img.shields.io/badge/Docker-latest-blue)
- ![Maven](https://img.shields.io/badge/Maven-3.8.4-red)

## ✨ Funcionalidades

### Para Restaurantes 🏪
- Gestão de cardápio
- Controle de pedidos
- Gestão de reservas
- Relatórios e análises

### Para Clientes 👥
- Consulta de cardápios
- Realização de pedidos online
- Sistema de avaliações
- Reserva de mesas

## 🏃‍♂️ Começando

### Pré-requisitos 📋

- Java Development Kit (JDK) 21
- Docker e Docker Compose
- Maven
- IDE de sua preferência (recomendamos IntelliJ IDEA ou Eclipse)

### 1️⃣ Clone o Repositório

```bash
git clone https://github.com/seu-usuario/gastrohub.git
cd gastrohub
```

### 2️⃣ Configuração do Docker

Para iniciar o ambiente de desenvolvimento:

```bash
# Parar containers em execução (se necessário)
docker-compose down

# Remover volumes antigos (se necessário)
docker-compose down -v

# Construir e iniciar os containers
docker-compose up --build -d

# Verificar logs
docker-compose logs -f

# Verificar status dos containers
docker ps
```

### 3️⃣ Configuração do Banco de Dados

O Docker Compose irá:
- Inicializar o MySQL na porta 3306
- Criar o banco de dados automaticamente
- Executar os scripts de inicialização do diretório `sql_scripts`

Para acessar o MySQL via terminal:
```bash
docker exec -it gastrohub-mysql mysql -uroot -proot
```

### 4️⃣ Executando o Projeto

1. Navegue até a pasta do projeto Java:
```bash
cd gastrohub
```

2. Execute o projeto usando Maven:
```bash
./mvnw spring-boot:run
```

O aplicativo estará disponível em `http://localhost:8080`

## 📚 Documentação da API

A documentação da API está disponível através do Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

## 🔍 Estrutura do Projeto

```
gastrohub/
├── 📂 docker/
│   ├── 📄 docker-compose.yml
│   └── 📄 Dockerfile
├── 📂 sql_scripts/
│   ├── 📄 01-schema.sql
│   └── 📄 02-testdata.sql
└── 📂 gastrohub/
    ├── 📂 src/
    │   ├── 📂 main/
    │   └── 📂 test/
    └── 📄 pom.xml
```

## 🤝 Contribuição

1. Faça um Fork do projeto
2. Crie sua Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a Branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

