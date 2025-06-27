package com.example.appmusculacao

class RegisterInteractor {

    var output: RegisterInteractorOutput? = null

    fun register(username: String, password: String, email: String) {
        // Simula o registro e retorna sucesso
        if (username.isNotBlank() && password.isNotBlank() && email.isNotBlank()) {
            val user = User(
                id = "1",
                username = username,
                password = password,
                email = email
            )
            // bancos de dados
            output?.onRegisterSuccess(user)
        } else {
            output?.onRegisterFailure("Dados inválidos")
        }
    }

    fun login(username: String, password: String) {
        // Simula o registro e retorna sucesso
        if (username.isNotBlank() && password.isNotBlank()) {
            val user = User(
                id = "1",
                username = username,
                password = password,
                email = ""
            )
            output?.onRegisterSuccess(user)
        } else {
            output?.onRegisterFailure("Dados inválidos")
        }
    }

    fun paid(paid: Boolean, user: User) {
        // Simula o registro e retorna sucesso
        if (paid) {
            var workout = Workout(
                paid = paid,
                user = user
            )
            output?.onRegisterSuccess(user)
        } else {
            output?.onRegisterFailure("Dados inválidos")
        }
    }
}