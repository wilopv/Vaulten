output "acr_login_server" {
  description = "URL del Container Registry — usar como ACR_LOGIN_SERVER en GitHub Secrets"
  value       = azurerm_container_registry.main.login_server
}

output "acr_admin_username" {
  description = "Usuario admin del ACR — usar como ACR_USERNAME en GitHub Secrets"
  value       = azurerm_container_registry.main.admin_username
  sensitive   = true
}

output "acr_admin_password" {
  description = "Password admin del ACR — usar como ACR_PASSWORD en GitHub Secrets"
  value       = azurerm_container_registry.main.admin_password
  sensitive   = true
}

output "container_app_name" {
  description = "Nombre del Container App — usar como AZURE_CONTAINER_APP_NAME en GitHub Secrets"
  value       = azurerm_container_app.main.name
}

output "resource_group_name" {
  description = "Nombre del Resource Group — usar como AZURE_RESOURCE_GROUP en GitHub Secrets"
  value       = azurerm_resource_group.main.name
}

output "container_app_url" {
  description = "URL pública de la API"
  value       = "https://${azurerm_container_app.main.ingress.fqdn}"
}

output "sql_server_fqdn" {
  description = "FQDN del SQL Server (para conexiones directas)"
  value       = azurerm_mssql_server.main.fully_qualified_domain_name
}
