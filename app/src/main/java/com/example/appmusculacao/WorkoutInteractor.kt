package com.example.appmusculacao

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.util.UUID

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
        Exercise(UUID.randomUUID().toString(), "Supino Reto", 12, 4, 60),
    )

    private var currentExercises = defaultExercises.toMutableList()
    private var workoutAlreadyCompleted = false // <-- adicionado

    fun getExercises(): List<Exercise> = currentExercises

    @RequiresApi(Build.VERSION_CODES.O)
    fun markExerciseAsPaid(exerciseIndex: Int, isPaid: Boolean) {
        if (exerciseIndex in currentExercises.indices) {
            currentExercises[exerciseIndex] = currentExercises[exerciseIndex].copy(paid = isPaid)
            output?.onWorkoutUpdated(currentExercises)

            if (!workoutAlreadyCompleted && areAllExercisesPaid()) {
                workoutAlreadyCompleted = true
                output?.onWorkoutCompleted(LocalDate.now())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun markAllExercisesAsPaid(isPaid: Boolean) {
        currentExercises = currentExercises.map { it.copy(paid = isPaid) }.toMutableList()
        output?.onWorkoutUpdated(currentExercises)

        if (isPaid && !workoutAlreadyCompleted && areAllExercisesPaid()) {
            workoutAlreadyCompleted = true
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