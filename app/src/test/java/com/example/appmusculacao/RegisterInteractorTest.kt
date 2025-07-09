package com.example.appmusculacao

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.junit.Assert.*

class RegisterInteractorTest {
    private lateinit var interactor: RegisterInteractor
    private lateinit var mockOutput: RegisterInteractorOutputMock

    @Mock
    private lateinit var mockContext: Context
    
    @Mock
    private lateinit var mockPrefs: SharedPreferences
    
    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        setupSharedPreferencesMocks()
        setupInteractor()
    }

    private fun setupSharedPreferencesMocks() {
        // Configure SharedPreferences mock behavior
        Mockito.`when`(mockContext.getSharedPreferences(UserStorage.PREFS_NAME, Context.MODE_PRIVATE)).thenReturn(mockPrefs)
        Mockito.`when`(mockPrefs.edit()).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(mockEditor)
        Mockito.`when`(mockEditor.apply()).then { }
    }

    private fun setupInteractor() {
        mockOutput = RegisterInteractorOutputMock()
        interactor = RegisterInteractor(mockContext)
        interactor.output = mockOutput
    }

    @Test
    fun `should register user with valid data`() {
        // Given
        val username = "test_user"
        val password = "test123!@#"
        val email = "test@example.com"

        // When
        interactor.register(username, password, email)

        // Then
        verifyUserStorageInteractions(username, email, password)
        assertTrue("Output should indicate successful registration", mockOutput.didRegisterUser)
    }

    @Test
    fun `should not register user with empty data`() {
        // Given
        val username = ""
        val password = "test123"
        val email = "test@example.com"

        // When
        interactor.register(username, password, email)

        // Then
        verifyNoSharedPreferencesInteractions()
        assertFalse("Output should not indicate successful registration", mockOutput.didRegisterUser)
    }

    private fun verifyUserStorageInteractions(username: String, email: String, password: String) {
        Mockito.verify(mockContext).getSharedPreferences(UserStorage.PREFS_NAME, Context.MODE_PRIVATE)
        Mockito.verify(mockPrefs).edit()
        Mockito.verify(mockEditor).putString(UserStorage.KEY_NAME, username)
        Mockito.verify(mockEditor).putString(UserStorage.KEY_EMAIL, email)
        Mockito.verify(mockEditor).putString(UserStorage.KEY_PASSWORD, password)
        Mockito.verify(mockEditor).apply()
    }

    private fun verifyNoSharedPreferencesInteractions() {
        Mockito.verifyNoInteractions(mockPrefs, mockEditor)
    }
}

// Mock implementation of the output interface
class RegisterInteractorOutputMock : RegisterInteractorOutput {
    var didRegisterUser = false

    override fun onRegisterSuccess(user: User) {
        didRegisterUser = true
    }

    override fun onRegisterFailure(error: String) {
        didRegisterUser = false
    }
}

class LoginInteractorTest {
    private lateinit var interactor: LoginInteractor
    private lateinit var mockOutput: LoginInteractorOutputMock

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        setupInteractor()
    }

    private fun setupInteractor() {
        mockOutput = LoginInteractorOutputMock()
        interactor = LoginInteractor()
        interactor.output = mockOutput
    }

    @Test
    fun `should login user with valid data`() {

        val username = "test_user"
        val password = "test123!@#"


        interactor.login(username, password)


        assertTrue("Output should indicate successful login", mockOutput.didLoginUser)
        assertEquals("Username should match", username, mockOutput.user?.username)
        assertEquals("Password should match", password, mockOutput.user?.password)
    }

    @Test
    fun `should not login user with empty data`() {

        val username = ""
        val password = "test123!@#"


        interactor.login(username, password)


        assertFalse("Output should not indicate successful login", mockOutput.didLoginUser)
        assertEquals("Error message should be set", "Dados inválidos", mockOutput.errorMessage)
    }

    @Test
    fun `should not login user with blank username`() {

        val username = "   "
        val password = "test123!@#"


        interactor.login(username, password)


        assertFalse("Output should not indicate successful login", mockOutput.didLoginUser)
        assertEquals("Error message should be set", "Dados inválidos", mockOutput.errorMessage)
    }
}

// Mock implementation of the output interface
class LoginInteractorOutputMock : LoginInteractorOutput {
    var didLoginUser = false
    var user: User? = null
    var errorMessage: String? = null

    override fun onRegisterSuccess(user: User) {
        this.didLoginUser = true
        this.user = user
    }

    override fun onRegisterFailure(error: String) {
        this.didLoginUser = false
        this.errorMessage = error
    }
}

// Interfaces e classes necessárias
interface LoginInteractorOutput {
    fun onRegisterSuccess(user: User)
    fun onRegisterFailure(error: String)
}

data class User(
    val id: String,
    val username: String,
    val password: String,
    val email: String
)

class LoginInteractor {
    var output: LoginInteractorOutput? = null

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
}
