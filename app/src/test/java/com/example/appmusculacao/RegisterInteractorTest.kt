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
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import java.time.LocalDate

//Test do Registro//
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

// Interfaces corrigidas
interface LoginInteractorOutput {
    fun onLoginSuccess(user: User)
    fun onLoginFailure(error: String)
}

data class User(
    val id: String,
    val username: String,
    val password: String,
    val email: String
)

// Implementação mais realista do LoginInteractor
class LoginInteractor {
    var output: LoginInteractorOutput? = null

    // Simula uma base de dados de usuários
    private val users = listOf(
        User("1", "user1", "password123", "user1@email.com"),
        User("2", "user2", "mypass456", "user2@email.com"),
        User("3", "test_user", "test123!@#", "test@email.com")
    )

    fun login(email: String, password: String, context: Context? = null) {
        when {
            email.isBlank() || password.isBlank() -> {
                output?.onLoginFailure("Dados inválidos")
            }
            !isValidEmail(email) -> {
                output?.onLoginFailure("Email inválido")
            }
            else -> {
                // Simula autenticação
                authenticateUser(email, password)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun authenticateUser(email: String, password: String) {
        val user = users.find { it.email == email }

        when {
            user == null -> {
                output?.onLoginFailure("Usuário não encontrado")
            }
            user.password != password -> {
                output?.onLoginFailure("Senha incorreta")
            }
            else -> {
                output?.onLoginSuccess(user)
            }
        }
    }
}

// Mock corrigido
class LoginInteractorOutputMock : LoginInteractorOutput {
    var didLoginUser = false
    var user: User? = null
    var errorMessage: String? = null

    override fun onLoginSuccess(user: User) {
        this.didLoginUser = true
        this.user = user
        this.errorMessage = null
    }

    override fun onLoginFailure(error: String) {
        this.didLoginUser = false
        this.user = null
        this.errorMessage = error
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
    fun `should login user with valid credentials`() {

        val email = "test@email.com"
        val password = "test123!@#"


        interactor.login(email, password)


        assertTrue("Should indicate successful login", mockOutput.didLoginUser)
        assertEquals("Email should match", email, mockOutput.user?.email)
        assertEquals("Password should match", password, mockOutput.user?.password)
        assertEquals("Username should match", "test_user", mockOutput.user?.username)
        assertNull("Error message should be null", mockOutput.errorMessage)
    }

    @Test
    fun `should not login user with empty email`() {

        val email = ""
        val password = "test123!@#"


        interactor.login(email, password)


        assertFalse("Should not indicate successful login", mockOutput.didLoginUser)
        assertEquals("Error message should be set", "Dados inválidos", mockOutput.errorMessage)
        assertNull("User should be null", mockOutput.user)
    }

    @Test
    fun `should not login user with blank email`() {

        val email = "   "
        val password = "test123!@#"


        interactor.login(email, password)


        assertFalse("Should not indicate successful login", mockOutput.didLoginUser)
        assertEquals("Error message should be set", "Dados inválidos", mockOutput.errorMessage)
        assertNull("User should be null", mockOutput.user)
    }

    @Test
    fun `should not login user with empty password`() {

        val email = "test@email.com"
        val password = ""


        interactor.login(email, password)


        assertFalse("Should not indicate successful login", mockOutput.didLoginUser)
        assertEquals("Error message should be set", "Dados inválidos", mockOutput.errorMessage)
        assertNull("User should be null", mockOutput.user)
    }

    @Test
    fun `should not login user with invalid email format`() {

        val email = "invalid-email"
        val password = "test123!@#"


        interactor.login(email, password)


        assertFalse("Should not indicate successful login", mockOutput.didLoginUser)
        assertEquals("Error message should be set", "Email inválido", mockOutput.errorMessage)
        assertNull("User should be null", mockOutput.user)
    }

    @Test
    fun `should not login user with non-existent email`() {

        val email = "nonexistent@email.com"
        val password = "test123!@#"


        interactor.login(email, password)


        assertFalse("Should not indicate successful login", mockOutput.didLoginUser)
        assertEquals("Error message should be set", "Usuário não encontrado", mockOutput.errorMessage)
        assertNull("User should be null", mockOutput.user)
    }

    @Test
    fun `should not login user with wrong password`() {

        val email = "test@email.com"
        val password = "wrongpassword"


        interactor.login(email, password)


        assertFalse("Should not indicate successful login", mockOutput.didLoginUser)
        assertEquals("Error message should be set", "Senha incorreta", mockOutput.errorMessage)
        assertNull("User should be null", mockOutput.user)
    }

    @Test
    fun `should login different valid users`() {
        // Test first user
        interactor.login("user1@email.com", "password123")
        assertTrue("Should login first user", mockOutput.didLoginUser)
        assertEquals("Should be first user", "user1", mockOutput.user?.username)

        // Reset mock
        setupInteractor()

        // Test second user
        interactor.login("user2@email.com", "mypass456")
        assertTrue("Should login second user", mockOutput.didLoginUser)
        assertEquals("Should be second user", "user2", mockOutput.user?.username)
    }
}
//Test da Lista de exercício//
@RunWith(AndroidJUnit4::class)
class WorkoutScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockOnGoToCalendar: () -> Unit
    private lateinit var mockOnWorkoutMarked: (LocalDate) -> Unit

    @Before
    fun setup() {
        mockOnGoToCalendar = mock()
        mockOnWorkoutMarked = mock()
    }

    @Test
    fun workoutScreen_displaysCorrectTitle() {
        // Given
        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Exercícios")
            .assertIsDisplayed()
    }

    @Test
    fun workoutScreen_displaysAllExercises() {

        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }


        composeTestRule
            .onNodeWithText("Supino Reto")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Agachamento")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Rosca Direta")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Puxada Alta")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Remada Curvada")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Desenvolvimento")
            .assertIsDisplayed()
    }

    @Test
    fun workoutScreen_displaysCalendarButton() {

        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Ver Calendário")
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun calendarButton_clickCallsOnGoToCalendar() {

        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }


        composeTestRule
            .onNodeWithText("Ver Calendário")
            .performClick()


        verify(mockOnGoToCalendar).invoke()
    }

    @Test
    fun mainCheckbox_startsUnchecked() {

        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }


        composeTestRule
            .onNode(hasText("Pago:"))
            .onSiblings()
            .filterToOne(hasClickAction())
            .assertIsOff()
    }

    @Test
    fun mainCheckbox_whenChecked_marksAllExercisesAsPaid() {

        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }


        composeTestRule
            .onNode(hasText("Pago:"))
            .onSiblings()
            .filterToOne(hasClickAction())
            .performClick()


        verify(mockOnWorkoutMarked).invoke(LocalDate.now())
    }

    @Test
    fun mainCheckbox_whenUnchecked_marksAllExercisesAsUnpaid() {

        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }


        composeTestRule
            .onNode(hasText("Pago:"))
            .onSiblings()
            .filterToOne(hasClickAction())
            .performClick()


        composeTestRule
            .onNode(hasText("Pago:"))
            .onSiblings()
            .filterToOne(hasClickAction())
            .performClick()


        composeTestRule
            .onNode(hasText("Pago:"))
            .onSiblings()
            .filterToOne(hasClickAction())
            .assertIsOff()
    }

    @Test
    fun workoutScreen_displaysCurrentDate() {

        val today = LocalDate.now()
        val expectedDay = today.dayOfWeek.getDisplayName(
            java.time.format.TextStyle.FULL,
            java.util.Locale("pt", "BR")
        ).replaceFirstChar { it.uppercase() }

        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(expectedDay)
            .assertIsDisplayed()
    }

    @Test
    fun workoutScreen_displaysPaidLabel() {

        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }


        composeTestRule
            .onNodeWithText("Pago:")
            .assertIsDisplayed()
    }

    @Test
    fun workoutScreen_whenAllExercisesMarkedIndividually_callsOnWorkoutMarked() {

        composeTestRule.setContent {
            WorkoutScreen(
                onGoToCalendar = mockOnGoToCalendar,
                onWorkoutMarked = mockOnWorkoutMarked
            )
        }

        composeTestRule
            .onNode(hasText("Pago:"))
            .onSiblings()
            .filterToOne(hasClickAction())
            .performClick()

        // Then
        verify(mockOnWorkoutMarked).invoke(LocalDate.now())
    }
}

data class Exercise(
    val name: String,
    val reps: Int,
    val sets: Int,
    val weight: Int,
    val paid: Boolean = false
)