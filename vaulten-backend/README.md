# 🔐 Vaulten Backend

Backend REST API for a password manager (Bitwarden-style) - Professional portfolio project.

## 📝 Description

Vaulten is a secure password manager backend built with **Spring Boot**, following **clean architecture** principles and **strict TDD** methodology. This project demonstrates professional development practices suitable for enterprise environments.

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot 3.2.4**
  - Spring Web
  - Spring Data JPA
  - Spring Security
  - Spring Validation
- **Maven** - Dependency management
- **Lombok** - Reduce boilerplate
- **JWT (jjwt 0.12.3)** - Authentication tokens
- **H2 Database** - In-memory database (dev & test)
- **Swagger/OpenAPI** - API documentation
- **JUnit 5 + Mockito** - Testing
- **Azure Integration** (Phase 4) - Cloud deployment

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+** (or use included Maven wrapper)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/wilopv/vaulten-backend.git
cd vaulten-backend
```

2. **Build the project**
```bash
./mvnw clean install
```

3. **Run the application** (dev profile by default)
```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### Running Tests

```bash
./mvnw test
```

## 📚 API Documentation

Once the application is running, access Swagger UI at:

**Swagger UI:** http://localhost:8080/swagger-ui.html

**OpenAPI JSON:** http://localhost:8080/api-docs

## 🗄️ H2 Console (Development Only)

Access H2 console in **dev** profile:

- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:vaulten-dev`
- **Username:** `sa`
- **Password:** *(empty)*

## 📂 Project Structure

```
src/main/java/com/wilove/vaulten/
├── config/          # Configuration classes (Swagger, etc.)
├── controller/      # REST Controllers (endpoints)
├── service/         # Business logic layer
├── repository/      # Data access layer (JPA)
├── model/           # Entities & DTOs
├── security/        # Security configuration (JWT, filters)
└── exception/       # Custom exceptions & handlers
```

## 🔄 Development Phases

This project follows a **phased development approach** with strict TDD:

- ✅ **Phase 0** - Initial setup (current)
- ⏳ **Phase 1** - Authentication & Security (JWT, Spring Security)
- ⏳ **Phase 2** - Vault/Password CRUD (AES encryption)
- ⏳ **Phase 3** - Synchronization (optional MVP)
- ⏳ **Phase 4** - Azure Cloud Integration
- ⏳ **Phase 5** - Complete testing & QA
- ⏳ **Phase 6** - Final polish for portfolio

See [task.md](.gemini/antigravity/brain/task.md) for detailed roadmap.

## 🧪 Testing Strategy

- **Unit Tests:** Mockito for service layer
- **Integration Tests:** H2 in-memory database
- **Security Tests:** MockMvc for endpoints
- **TDD Approach:** Tests written BEFORE implementation

Target coverage: **80%+** for services

## ⚙️ Profiles

- **dev** - Local development with H2 console enabled
- **test** - Automated testing (minimal logging)
- **prod** - Production configuration (Phase 4 - Azure SQL)

## 🔑 Environment Variables (Phase 4+)

Production deployment will use Azure Key Vault for secrets:
- `JWT_SECRET` - JWT signing key
- `AZURE_SQL_*` - Database credentials

## 🤝 Contributing

This is a portfolio project, but suggestions are welcome!

## 📄 License

MIT License - see [LICENSE](LICENSE) file for details

## 👤 Author

**Wilson Lopez**
- GitHub: [@wilopv](https://github.com/wilopv)

---

**⚠️ Note:** This is a portfolio/demonstration project. While following professional standards, it's not intended for production use without additional security hardening.
