package com.example.appmusculacao

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ExerciseListInteractorTest {

    private lateinit var context: Context
    private lateinit var interactor: ExerciseListInteractor
    private lateinit var mockOutput: ExerciseListInteractorOutputMock

    @Before
    fun setUp() {
        // Contexto fake de teste
        context = ApplicationProvider.getApplicationContext()
        mockOutput = ExerciseListInteractorOutputMock()

        // Cria interactor com contexto fake
        interactor = ExerciseListInteractor(context)
        interactor.output = mockOutput
    }

    @Test
    fun `deve criar exercício com dados válidos`() {
        val exercise = ExerciseCRUD(
            name = "Supino Reto",
            repetitions = 12,
            series = 3,
            intervalSeconds = 60,
            muscleGroup = "Peito"
        )

        interactor.createExercise(exercise)

        assertTrue(mockOutput.didCreateExercise)
        assertEquals(exercise.name, mockOutput.createdExercise?.name)
    }

    @Test
    fun `deve listar todos os exercícios`() {
        val exercise1 = ExerciseCRUD(name = "Supino", repetitions = 12, series = 3, intervalSeconds = 60, muscleGroup = "Peito")
        val exercise2 = ExerciseCRUD(name = "Agachamento", repetitions = 15, series = 4, intervalSeconds = 90, muscleGroup = "Pernas")

        interactor.createExercise(exercise1)
        interactor.createExercise(exercise2)

        interactor.getAllExercises()

        assertTrue(mockOutput.didLoadExercises)
        assertEquals(2, mockOutput.exercisesList.size)
    }

    @Test
    fun `deve atualizar exercício existente`() {
        val originalExercise = ExerciseCRUD(name = "Supino", repetitions = 10, series = 3, intervalSeconds = 60, muscleGroup = "Peito")
        interactor.createExercise(originalExercise)

        val updatedExercise = originalExercise.copy(repetitions = 12, series = 4)
        interactor.updateExercise(updatedExercise)

        assertTrue(mockOutput.didUpdateExercise)
        assertEquals(12, mockOutput.updatedExercise?.repetitions)
        assertEquals(4, mockOutput.updatedExercise?.series)
    }

    @Test
    fun `deve deletar exercício por ID`() {
        val exercise = ExerciseCRUD(name = "Rosca", repetitions = 12, series = 3, intervalSeconds = 45, muscleGroup = "Braços")
        interactor.createExercise(exercise)

        interactor.deleteExercise(exercise.id)

        assertTrue(mockOutput.didDeleteExercise)
        assertEquals(exercise.id, mockOutput.deletedExerciseId)
    }
}
