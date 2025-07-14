package com.example.appmusculacao



interface LoginInteractorOutput {
    fun onLoginSuccess(user: User)
    fun onLoginFailure(error: String)
}

// Classe principal do interactor
class LoginInteractor {
    var output: LoginInteractorOutput? = null

    // email?
    fun login(email: String, password: String) {
        if (email.isNotBlank() && password.isNotBlank()) {
            val user = User(
                id = "1",
                username = "",
                password = password,
                email = email // Assumindo que username é o email
            )
            output?.onLoginSuccess(user)
        } else {
            output?.onLoginFailure("Dados inválidos")
        }
    }
}