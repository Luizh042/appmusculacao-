package com.example.appmusculacao

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class RegisterInteractorTest {

    private lateinit var interactor: RegisterInteractor
    private lateinit var mockOutput: RegisterInteractorOutputMock
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        mockOutput = RegisterInteractorOutputMock()
        interactor = RegisterInteractor(context)
        interactor.output = mockOutput
    }

    @Test
    fun `deve registrar usuario com dados válidos`() {
        val username = "Luiz"
        val password = "luiz@#$"
        val email = "luiz@email.com"

        interactor.register(username, password, email)

        val resultadoEmail = UserStorage.getUserEmail(context)
        assertEquals(email, resultadoEmail)
    }

    @Test
    fun `nao deve registrar usuario com dados inválidos`() {
        var didRegisterUser = false
        var didFailRegister = false
        var errorMessage: String? = null

        val mockOutput = object : RegisterInteractorOutput {
            override fun onRegisterSuccess(user: User) {
                didRegisterUser = true
            }

            override fun onRegisterFailure(error: String) {
                didFailRegister = true
                errorMessage = error
            }
        }

        val interactor = RegisterInteractor(context)
        interactor.output = mockOutput

        interactor.register("", "senha123", "email@email.com")

        assertFalse(didRegisterUser)
        assertTrue(didFailRegister)
        assertEquals("Dados inválidos", errorMessage)
    }

    @Test
    fun `deve logar usuario com dados válidos`() {
        val username = "Luiz"
        val password = "luiz@#$"

        interactor.login(username, password)

        assertTrue(mockOutput.didRegisterUser)
    }
    @Test
    fun `deve cadastrar usuario com dados válidos`() {
        val paid = true
        val user = User(id = "1", username = "Luiz", password = "luiz@#$", email = "luiz@email.com")

        interactor.paid(paid, user)

        assertTrue(mockOutput.didRegisterUser)
    }
    @Test
    fun `deve cadastrar agenda usuario com dados válidos`() {

        val user = User(id = "1", username = "Luiz", password = "luiz@#$", email = "luiz@email.com")

        interactor.calendar(user)

        assertTrue(mockOutput.didRegisterUser)
    }
}

// MOCK para validar se o interactor chamou corretamente o output
class RegisterInteractorOutputMock : RegisterInteractorOutput {
    var didRegisterUser = false

    override fun onRegisterSuccess(user: User) {
        didRegisterUser = true
    }

    override fun onRegisterFailure(error: String) {
        // não precisa testar aqui
    }
}
