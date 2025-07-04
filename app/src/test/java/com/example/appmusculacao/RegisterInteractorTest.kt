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
