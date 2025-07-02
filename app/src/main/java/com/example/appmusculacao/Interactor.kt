package com.example.appmusculacao.domain

import android.content.Context
import com.example.appmusculacao.UserStorage

class Interactor(private val context: Context) {
    fun register(name: String, email: String, password: String) {
        UserStorage.saveUser(context, name, email, password)
    }
}