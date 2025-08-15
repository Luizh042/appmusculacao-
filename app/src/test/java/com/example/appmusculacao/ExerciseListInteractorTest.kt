package com.example.appmusculacao

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExerciseListInteractorRealTest {

    private lateinit var context: Context
    private lateinit var interactor: ExerciseListInteractor
    private lateinit var output: ExerciseListInteractorOutputMock

    @Before
    fun setUp() {
        context = mock(Context::class.java) // contexto falso
        output = ExerciseListInteractorOutputMock()
        interactor = ExerciseListInteractor(context)
        interactor.output = output
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

        assertTrue(output.didCreateExercise)
        assertEquals(exercise.name, output.createdExercise?.name)
    }

    @Test
    fun `deve listar todos os exercícios`() {
        val exercise1 = ExerciseCRUD(
            name = "Supino",
            repetitions = 12,
            series = 3,
            intervalSeconds = 60,
            muscleGroup = "Peito"
        )
        val exercise2 = ExerciseCRUD(
            name = "Agachamento",
            repetitions = 15,
            series = 4,
            intervalSeconds = 90,
            muscleGroup = "Pernas"
        )

        interactor.createExercise(exercise1)
        interactor.createExercise(exercise2)

        interactor.getAllExercises()

        assertTrue(output.didLoadExercises)
        assertEquals(2, output.exercisesList.size)
    }

    @Test
    fun `deve atualizar exercício existente`() {
        val originalExercise = ExerciseCRUD(
            name = "Supino",
            repetitions = 10,
            series = 3,
            intervalSeconds = 60,
            muscleGroup = "Peito"
        )
        interactor.createExercise(originalExercise)

        val updatedExercise = originalExercise.copy(repetitions = 12, series = 4)
        interactor.updateExercise(updatedExercise)

        assertTrue(output.didUpdateExercise)
        assertEquals(12, output.updatedExercise?.repetitions)
        assertEquals(4, output.updatedExercise?.series)
    }

    @Test
    fun `deve deletar exercício por ID`() {
        val exercise = ExerciseCRUD(
            name = "Rosca",
            repetitions = 12,
            series = 3,
            intervalSeconds = 45,
            muscleGroup = "Braços"
        )
        interactor.createExercise(exercise)

        interactor.deleteExercise(exercise.id)

        assertTrue(output.didDeleteExercise)
        assertEquals(exercise.id, output.deletedExerciseId)
    }
} 

