resource "azurerm_log_analytics_workspace" "main" {
  name                = "${var.app_name}-logs"
  resource_group_name = azurerm_resource_group.main.name
  location            = azurerm_resource_group.main.location
  sku                 = "PerGB2018"
  retention_in_days   = 30
}

resource "azurerm_container_app_environment" "main" {
  name                       = "${var.app_name}-env"
  resource_group_name        = azurerm_resource_group.main.name
  location                   = azurerm_resource_group.main.location
  log_analytics_workspace_id = azurerm_log_analytics_workspace.main.id
}

resource "azurerm_container_app" "main" {
  name                         = "${var.app_name}-api"
  container_app_environment_id = azurerm_container_app_environment.main.id
  resource_group_name          = azurerm_resource_group.main.name
  revision_mode                = "Single"

  registry {
    server               = azurerm_container_registry.main.login_server
    username             = azurerm_container_registry.main.admin_username
    password_secret_name = "acr-password"
  }

  secret {
    name  = "acr-password"
    value = azurerm_container_registry.main.admin_password
  }
  secret {
    name  = "jwt-secret"
    value = var.jwt_secret
  }
  secret {
    name  = "vault-encryption-key"
    value = var.vault_encryption_key
  }
  secret {
    name  = "sql-password"
    value = var.sql_admin_password
  }
  secret {
    name  = "sql-username"
    value = var.sql_admin_login
  }

  template {
    min_replicas = 0
    max_replicas = 1

    container {
      name   = "${var.app_name}-api"
      image  = "${azurerm_container_registry.main.login_server}/${var.app_name}:latest"
      cpu    = 0.25
      memory = "0.5Gi"

      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod"
      }
      env {
        name  = "SPRING_DATASOURCE_URL"
        value = "jdbc:sqlserver://${azurerm_mssql_server.main.fully_qualified_domain_name}:1433;database=vaulten;encrypt=true;trustServerCertificate=false"
      }
      env {
        name        = "SPRING_DATASOURCE_USERNAME"
        secret_name = "sql-username"
      }
      env {
        name        = "SPRING_DATASOURCE_PASSWORD"
        secret_name = "sql-password"
      }
      env {
        name        = "JWT_SECRET"
        secret_name = "jwt-secret"
      }
      env {
        name        = "VAULT_ENCRYPTION_KEY"
        secret_name = "vault-encryption-key"
      }
    }
  }

  lifecycle {
    ignore_changes = [template]
  }

  ingress {
    external_enabled = true
    target_port      = 8080
    traffic_weight {
      percentage      = 100
      latest_revision = true
    }
  }
}
