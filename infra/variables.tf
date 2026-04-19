variable "location" {
  description = "Azure region donde se despliegan los recursos"
  default     = "westeurope"
}

variable "app_name" {
  description = "Nombre base para todos los recursos Azure"
  default     = "vaulten"
}

variable "sql_admin_login" {
  description = "Usuario administrador del SQL Server"
  sensitive   = true
}

variable "sql_admin_password" {
  description = "Contraseña del SQL Server (mínimo 8 chars, mayúscula, minúscula, número y símbolo)"
  sensitive   = true
}

variable "jwt_secret" {
  description = "Clave JWT en base64 (mínimo 256 bits)"
  sensitive   = true
}

variable "vault_encryption_key" {
  description = "Clave AES-256 en base64 para cifrar las credenciales"
  sensitive   = true
}
