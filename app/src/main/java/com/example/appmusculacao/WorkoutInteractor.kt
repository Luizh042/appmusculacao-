package com.example.appmusculacao

import java.time.LocalDate

data class WorkoutExercise(
    val name: String,
    val reps: Int,
    val sets: Int,
    val restTime: Int,
    val paid: Boolean = false
)

interface WorkoutInteractorOutput {
    fun onWorkoutCompleted(date: LocalDate)
    fun onWorkoutUpdated(exercises: List<Exercise>)
    fun onNavigateToCalendar()
}

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