# Vaulten

**[English](#english) · [Español](#español)**

---

## English

A self-hosted password manager for Android. Store and autofill credentials securely, synced with your own backend — no third-party services, full control over your data.

### Features

- **Credential vault** — create, edit, delete credentials with search and advanced filters
- **Autofill** — fills login forms automatically in browsers and native apps (Android 8.0+; Credential Manager API 34+)
- **Password generator** — configurable length, uppercase, lowercase, numbers, symbols
- **Biometric lock** — locks the app after inactivity, unlocks with fingerprint or PIN
- **Trash & restore** — soft-deleted credentials recoverable from the trash
- **Export / Import** — CSV (unencrypted) or AES-256 encrypted JSON with PBKDF2 key derivation
- **Password health** — detects weak and duplicate passwords
- **Offline-first** — Room cache keeps credentials accessible without network

### Architecture

```
android-app/          Kotlin · Jetpack Compose · Hilt · Room · Retrofit
vaulten-backend/      Spring Boot 3.2 · Java 17 · Spring Security · JPA
infra/                Terraform · Azure Container Apps · Azure SQL Database
.github/workflows/    GitHub Actions CI/CD
```

**Security:** JWT tokens stored in `EncryptedSharedPreferences`. Credentials encrypted at rest with AES-256-GCM. Passwords never logged.

### Requirements

- Android 8.0+ (API 26) — autofill via `AutofillService`
- Android 14+ (API 34) — autofill via `CredentialProviderService`
- Backend: Docker or Java 17 + Maven

### Local Development

**Backend with Docker (recommended)**

```bash
cp .env.example .env        # uses safe placeholder credentials
docker compose up -d db

# Create the database (first time only)
docker compose exec db /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "Tu_Password_Segura1!" -C \
  -Q "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name='vaulten') CREATE DATABASE vaulten"

docker compose up api
```

API at `http://localhost:8080/api` · Swagger UI at `http://localhost:8080/api/swagger-ui.html`

**Backend without Docker**

```bash
cd vaulten-backend && ./mvnw spring-boot:run
```

Uses H2 in-memory database (dev profile). No setup required.

**Android app**

Open `android-app/` in Android Studio and run on a device or emulator.

**Tests**

```bash
cd vaulten-backend && ./mvnw test        # backend unit tests
cd android-app && ./gradlew :app:test    # Android unit tests
```

### Deploy to Azure

**Prerequisites:** [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli) · [Terraform](https://developer.hashicorp.com/terraform/install) >= 1.3 · Docker

**1. Authenticate**
```bash
az login
```

**2. Configure variables**
```bash
cd infra
cp terraform.tfvars.example terraform.tfvars
# Edit terraform.tfvars — generate keys with:
openssl rand -base64 64   # → jwt_secret
openssl rand -base64 32   # → vault_encryption_key
```

**3. Provision infrastructure**
```bash
terraform init && terraform apply
```

Creates: Resource Group · Container Registry · Azure SQL Database (serverless, free tier eligible) · Container Apps (scales to zero).

**4. Push initial Docker image**
```bash
ACR=$(terraform output -raw acr_login_server)
docker login $ACR -u $(terraform output -raw acr_admin_username) -p $(terraform output -raw acr_admin_password)
docker build -t $ACR/vaulten:latest ../vaulten-backend
docker push $ACR/vaulten:latest
```

**5. Configure GitHub Secrets**

Add in **Settings → Secrets and variables → Actions**:

| Secret | Source |
|--------|--------|
| `ACR_LOGIN_SERVER` | `terraform output acr_login_server` |
| `ACR_USERNAME` | `terraform output -raw acr_admin_username` |
| `ACR_PASSWORD` | `terraform output -raw acr_admin_password` |
| `AZURE_CONTAINER_APP_NAME` | `terraform output container_app_name` |
| `AZURE_RESOURCE_GROUP` | `terraform output resource_group_name` |
| `AZURE_CREDENTIALS` | JSON from `az ad sp create-for-rbac --sdk-auth` |

**6. Verify**
```bash
terraform output container_app_url
# Open https://<url>/api/swagger-ui.html
```

From this point, every push to `main` touching `vaulten-backend/` triggers an automatic deploy.

**Tear down**
```bash
terraform destroy
```

---

## Español

Gestor de contraseñas autoalojado para Android. Almacena y autocompleta credenciales de forma segura, sincronizadas con tu propio backend — sin servicios de terceros, control total sobre tus datos.

### Funcionalidades

- **Bóveda de credenciales** — crear, editar y eliminar credenciales con búsqueda y filtros avanzados
- **Autocompletado** — rellena formularios de login automáticamente en navegadores y apps nativas (Android 8.0+; Credential Manager API 34+)
- **Generador de contraseñas** — longitud configurable, mayúsculas, minúsculas, números y símbolos
- **Bloqueo biométrico** — bloquea la app tras inactividad, desbloquea con huella dactilar o PIN
- **Papelera y restauración** — las credenciales eliminadas se pueden recuperar desde la papelera
- **Exportar / Importar** — CSV (sin cifrado) o JSON cifrado con AES-256 y derivación de clave PBKDF2
- **Salud de contraseñas** — detecta contraseñas débiles y duplicadas
- **Offline-first** — la caché Room mantiene las credenciales accesibles sin conexión

### Arquitectura

```
android-app/          Kotlin · Jetpack Compose · Hilt · Room · Retrofit
vaulten-backend/      Spring Boot 3.2 · Java 17 · Spring Security · JPA
infra/                Terraform · Azure Container Apps · Azure SQL Database
.github/workflows/    GitHub Actions CI/CD
```

**Seguridad:** tokens JWT almacenados en `EncryptedSharedPreferences`. Credenciales cifradas en reposo con AES-256-GCM. Las contraseñas nunca se registran en logs.

### Requisitos

- Android 8.0+ (API 26) — autocompletado vía `AutofillService`
- Android 14+ (API 34) — autocompletado vía `CredentialProviderService`
- Backend: Docker o Java 17 + Maven

### Desarrollo Local

**Backend con Docker (recomendado)**

```bash
cp .env.example .env        # usa credenciales de ejemplo seguras
docker compose up -d db

# Crear la base de datos (solo la primera vez)
docker compose exec db /opt/mssql-tools18/bin/sqlcmd \
  -S localhost -U sa -P "Tu_Password_Segura1!" -C \
  -Q "IF NOT EXISTS (SELECT name FROM sys.databases WHERE name='vaulten') CREATE DATABASE vaulten"

docker compose up api
```

API en `http://localhost:8080/api` · Swagger UI en `http://localhost:8080/api/swagger-ui.html`

**Backend sin Docker**

```bash
cd vaulten-backend && ./mvnw spring-boot:run
```

Usa base de datos H2 en memoria (perfil dev). Sin configuración adicional.

**App Android**

Abre `android-app/` en Android Studio y ejecuta en un dispositivo o emulador.

**Tests**

```bash
cd vaulten-backend && ./mvnw test        # tests unitarios del backend
cd android-app && ./gradlew :app:test    # tests unitarios Android
```

### Despliegue en Azure

**Prerequisitos:** [Azure CLI](https://docs.microsoft.com/cli/azure/install-azure-cli) · [Terraform](https://developer.hashicorp.com/terraform/install) >= 1.3 · Docker

**1. Autenticarse**
```bash
az login
```

**2. Configurar variables**
```bash
cd infra
cp terraform.tfvars.example terraform.tfvars
# Editar terraform.tfvars — generar claves con:
openssl rand -base64 64   # → jwt_secret
openssl rand -base64 32   # → vault_encryption_key
```

**3. Provisionar infraestructura**
```bash
terraform init && terraform apply
```

Crea: Resource Group · Container Registry · Azure SQL Database (serverless, elegible para free tier) · Container Apps (escala a cero).

**4. Push inicial de imagen Docker**
```bash
ACR=$(terraform output -raw acr_login_server)
docker login $ACR -u $(terraform output -raw acr_admin_username) -p $(terraform output -raw acr_admin_password)
docker build -t $ACR/vaulten:latest ../vaulten-backend
docker push $ACR/vaulten:latest
```

**5. Configurar Secrets en GitHub**

Añadir en **Settings → Secrets and variables → Actions**:

| Secret | Origen |
|--------|--------|
| `ACR_LOGIN_SERVER` | `terraform output acr_login_server` |
| `ACR_USERNAME` | `terraform output -raw acr_admin_username` |
| `ACR_PASSWORD` | `terraform output -raw acr_admin_password` |
| `AZURE_CONTAINER_APP_NAME` | `terraform output container_app_name` |
| `AZURE_RESOURCE_GROUP` | `terraform output resource_group_name` |
| `AZURE_CREDENTIALS` | JSON de `az ad sp create-for-rbac --sdk-auth` |

**6. Verificar**
```bash
terraform output container_app_url
# Abrir https://<url>/api/swagger-ui.html
```

A partir de aquí, cada push a `main` que toque `vaulten-backend/` despliega automáticamente.

**Destruir infraestructura**
```bash
terraform destroy
```
