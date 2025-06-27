package com.example.appmusculacao

interface RegisterInteractorOutput {
    fun onRegisterSuccess(user: User)
    fun onRegisterFailure(error: String)
}