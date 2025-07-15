package com.example.appmusculacao

import android.content.Context


interface LoginInteractorOutput {
    fun onLoginSuccess(user: User)
    fun onLoginFailure(error: String)
}

class LoginInteractor {
    lateinit var output: LoginInteractorOutput

    fun login(email: String, password: String, context: Context) {
        val isValid = UserStorage.loginUser(context, email, password)

        if (isValid) {
            val name = UserStorage.getUserName(context) ?: "Usuário"
            output.onLoginSuccess(User(name, email, "1", password))
        } else {
            output.onLoginFailure("Email ou senha inválidos")
        }
    }
}