package com.example.appmusculacao

import android.content.Context

class RegisterInteractor(private val context: Context) {

    var output: RegisterInteractorOutput? = null

    fun register(username: String, password: String, email: String) {
        if (username.isNotBlank() && password.isNotBlank() && email.isNotBlank()) {
            val user = User(
                id = "1",
                username = username,
                password = password,
                email = email
            )
            
            // Save user data
            UserStorage.saveUser(context, username, email, password)
            
            output?.onRegisterSuccess(user)
        } else {
            output?.onRegisterFailure("Invalid data: all fields are required")
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
    fun calendar(user: User) {
        // Simula o registro e retorna sucesso
        if (true) {
            var calendar = Calendar(
                user = user
            )
            output?.onRegisterSuccess(user)
        } else {
            output?.onRegisterFailure("Dados inválidos")
        }
    }
}