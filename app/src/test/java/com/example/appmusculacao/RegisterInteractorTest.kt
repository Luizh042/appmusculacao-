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
import java.time.LocalDate

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

        val username = "test_user"
        val password = "test123!@#"
        val email = "test@example.com"


        interactor.register(username, password, email)


        verifyUserStorageInteractions(username, email, password)
        assertTrue("Output should indicate successful registration", mockOutput.didRegisterUser)
    }

    @Test
    fun `should not register user with empty data`() {

        val username = ""
        val password = "test123"
        val email = "test@example.com"


        interactor.register(username, password, email)


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


class RegisterInteractorOutputMock : RegisterInteractorOutput {
    var didRegisterUser = false

    override fun onRegisterSuccess(user: User) {
        didRegisterUser = true
    }

    override fun onRegisterFailure(error: String) {
        didRegisterUser = false
    }
}

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

data class Exercise(
    val name: String,
    val reps: Int,
    val sets: Int,
    val restTime: Int,
    val paid: Boolean = false
)

// Interface para o output do WorkoutInteractor
interface WorkoutInteractorOutput {
    fun onWorkoutCompleted(date: LocalDate)
    fun onWorkoutUpdated(exercises: List<Exercise>)
    fun onNavigateToCalendar()
}

// Interactor que contém a lógica de negócio do WorkoutScreen
class WorkoutInteractor {
    var output: WorkoutInteractorOutput? = null

    private val defaultExercises = listOf(
        Exercise("Supino Reto", 12, 4, 60),
        Exercise("Agachamento", 12, 3, 90),
        Exercise("Rosca Direta", 15, 4, 45),
        Exercise("Puxada Alta", 12, 4, 60),
        Exercise("Remada Curvada", 10, 4, 60),
        Exercise("Desenvolvimento", 12, 4, 60)
    )

    private var currentExercises = defaultExercises.toMutableList()

    fun getExercises(): List<Exercise> = currentExercises

    fun markExerciseAsPaid(exerciseIndex: Int, isPaid: Boolean) {
        if (exerciseIndex in currentExercises.indices) {
            currentExercises[exerciseIndex] = currentExercises[exerciseIndex].copy(paid = isPaid)
            output?.onWorkoutUpdated(currentExercises)

            if (areAllExercisesPaid()) {
                output?.onWorkoutCompleted(LocalDate.now())
            }
        }
    }

    fun markAllExercisesAsPaid(isPaid: Boolean) {
        currentExercises = currentExercises.map { it.copy(paid = isPaid) }.toMutableList()
        output?.onWorkoutUpdated(currentExercises)

        if (isPaid && areAllExercisesPaid()) {
            output?.onWorkoutCompleted(LocalDate.now())
        }
    }

    fun areAllExercisesPaid(): Boolean {
        return currentExercises.all { it.paid }
    }

    fun navigateToCalendar() {
        output?.onNavigateToCalendar()
    }
}

// Mock implementation do output interface
class WorkoutInteractorOutputMock : WorkoutInteractorOutput {
    var didCompleteWorkout = false
    var completedDate: LocalDate? = null
    var updatedExercises: List<Exercise>? = null
    var didNavigateToCalendar = false

    override fun onWorkoutCompleted(date: LocalDate) {
        didCompleteWorkout = true
        completedDate = date
    }

    override fun onWorkoutUpdated(exercises: List<Exercise>) {
        updatedExercises = exercises
    }

    override fun onNavigateToCalendar() {
        didNavigateToCalendar = true
    }
}

class WorkoutInteractorTest {
    private lateinit var interactor: WorkoutInteractor
    private lateinit var mockOutput: WorkoutInteractorOutputMock

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        setupInteractor()
    }

    private fun setupInteractor() {
        mockOutput = WorkoutInteractorOutputMock()
        interactor = WorkoutInteractor()
        interactor.output = mockOutput
    }

    @Test
    fun `should return default exercises on initialization`() {
        // Given & When
        val exercises = interactor.getExercises()

        // Then
        assertEquals("Should have 6 exercises", 6, exercises.size)
        assertEquals("First exercise should be Supino Reto", "Supino Reto", exercises[0].name)
        assertEquals("Second exercise should be Agachamento", "Agachamento", exercises[1].name)
        assertTrue("All exercises should start unpaid", exercises.all { !it.paid })
    }

    @Test
    fun `should mark individual exercise as paid`() {
        // Given
        val exerciseIndex = 0

        // When
        interactor.markExerciseAsPaid(exerciseIndex, true)

        // Then
        val exercises = interactor.getExercises()
        assertTrue("First exercise should be marked as paid", exercises[0].paid)
        assertFalse("Other exercises should remain unpaid", exercises[1].paid)
        assertNotNull("Should update exercises", mockOutput.updatedExercises)
        assertFalse("Should not complete workout yet", mockOutput.didCompleteWorkout)
    }

    @Test
    fun `should not mark exercise with invalid index`() {
        // Given
        val invalidIndex = 10

        // When
        interactor.markExerciseAsPaid(invalidIndex, true)

        // Then
        val exercises = interactor.getExercises()
        assertTrue("All exercises should remain unpaid", exercises.all { !it.paid })
        assertNull("Should not update exercises", mockOutput.updatedExercises)
    }

    @Test
    fun `should complete workout when all exercises are marked individually`() {
        // Given & When - Mark all exercises as paid one by one
        for (i in 0 until 6) {
            interactor.markExerciseAsPaid(i, true)
        }

        // Then
        assertTrue("Should complete workout", mockOutput.didCompleteWorkout)
        assertNotNull("Should set completion date", mockOutput.completedDate)
        assertTrue("All exercises should be paid", interactor.areAllExercisesPaid())
    }

    @Test
    fun `should mark all exercises as paid at once`() {
        // Given & When
        interactor.markAllExercisesAsPaid(true)

        // Then
        val exercises = interactor.getExercises()
        assertTrue("All exercises should be marked as paid", exercises.all { it.paid })
        assertTrue("Should complete workout", mockOutput.didCompleteWorkout)
        assertNotNull("Should update exercises", mockOutput.updatedExercises)
        assertNotNull("Should set completion date", mockOutput.completedDate)
    }

    @Test
    fun `should unmark all exercises when marking all as unpaid`() {
        // Given - First mark all as paid
        interactor.markAllExercisesAsPaid(true)

        // Reset mock to test unmarking
        setupInteractor()

        // When
        interactor.markAllExercisesAsPaid(false)

        // Then
        val exercises = interactor.getExercises()
        assertTrue("All exercises should be unmarked", exercises.all { !it.paid })
        assertFalse("Should not complete workout", mockOutput.didCompleteWorkout)
        assertNotNull("Should update exercises", mockOutput.updatedExercises)
    }

    @Test
    fun `should not complete workout when not all exercises are paid`() {
        // Given & When - Mark only some exercises
        interactor.markExerciseAsPaid(0, true)
        interactor.markExerciseAsPaid(1, true)
        interactor.markExerciseAsPaid(2, true)

        // Then
        assertFalse("Should not complete workout", mockOutput.didCompleteWorkout)
        assertFalse("Not all exercises should be paid", interactor.areAllExercisesPaid())
    }

    @Test
    fun `should navigate to calendar when requested`() {
        // Given & When
        interactor.navigateToCalendar()

        // Then
        assertTrue("Should navigate to calendar", mockOutput.didNavigateToCalendar)
    }

    @Test
    fun `should unmark individual exercise`() {
        // Given - First mark an exercise as paid
        interactor.markExerciseAsPaid(0, true)

        // Reset mock to test unmarking
        setupInteractor()

        // When
        interactor.markExerciseAsPaid(0, false)

        // Then
        val exercises = interactor.getExercises()
        assertFalse("Exercise should be unmarked", exercises[0].paid)
        assertNotNull("Should update exercises", mockOutput.updatedExercises)
        assertFalse("Should not complete workout", mockOutput.didCompleteWorkout)
    }

    @Test
    fun `should handle mixed exercise states correctly`() {
        // Given - Mark some exercises as paid
        interactor.markExerciseAsPaid(0, true)
        interactor.markExerciseAsPaid(2, true)
        interactor.markExerciseAsPaid(4, true)

        // When
        val exercises = interactor.getExercises()

        // Then
        assertTrue("Exercise 0 should be paid", exercises[0].paid)
        assertFalse("Exercise 1 should not be paid", exercises[1].paid)
        assertTrue("Exercise 2 should be paid", exercises[2].paid)
        assertFalse("Exercise 3 should not be paid", exercises[3].paid)
        assertTrue("Exercise 4 should be paid", exercises[4].paid)
        assertFalse("Exercise 5 should not be paid", exercises[5].paid)
        assertFalse("Not all exercises are paid", interactor.areAllExercisesPaid())
    }

    @Test
    fun `should complete workout only once when all exercises are marked`() {
        // Given
        var completionCount = 0
        val countingOutput = object : WorkoutInteractorOutput {
            override fun onWorkoutCompleted(date: LocalDate) {
                completionCount++
            }
            override fun onWorkoutUpdated(exercises: List<Exercise>) {}
            override fun onNavigateToCalendar() {}
        }
        interactor.output = countingOutput

        // When - Mark all exercises multiple times
        interactor.markAllExercisesAsPaid(true)
        interactor.markAllExercisesAsPaid(true)
        interactor.markExerciseAsPaid(0, true) // Already paid

        // Then
        assertEquals("Should complete workout only once", 1, completionCount)
    }
}