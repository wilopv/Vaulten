package com.wilove.vaulten.ui.navigation

/**
 * Navigation destinations for the Vaulten app.
 * Defines all screen routes used in the navigation graph.
 */
object VaultenDestinations {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val DASHBOARD = "dashboard"
    const val CREDENTIALS_LIST = "credentials_list"
    const val CREDENTIAL_DETAIL = "credential_detail/{credentialId}"
    const val ADD_CREDENTIAL = "add_credential"
    const val EDIT_CREDENTIAL = "edit_credential/{credentialId}"
    const val PASSWORD_GENERATOR = "password_generator"
    const val PASSWORD_GENERATOR_FOR_CREDENTIAL = "password_generator_for_credential"
    const val SECURITY_SETTINGS = "security_settings"

    /**
     * Creates a route for credential detail with the given ID.
     */
    fun credentialDetail(credentialId: String) = "credential_detail/$credentialId"

    /**
     * Creates a route for edit credential with the given ID.
     */
    fun editCredential(credentialId: String) = "edit_credential/$credentialId"
}
