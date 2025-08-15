package com.example.appmusculacao

import android.content.Context

class ExerciseListInteractor(private val context: Context) {

    var output: ExerciseListInteractorOutput? = null
    private val exercises = mutableListOf<ExerciseCRUD>()

    fun createExercise(exercise: ExerciseCRUD) {
        exercises.add(exercise)
        output?.onExerciseCreated(exercise)
    }

    fun getAllExercises() {
        output?.onExercisesLoaded(exercises)
    }

    fun updateExercise(updated: ExerciseCRUD) {
        val index = exercises.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            exercises[index] = updated
            output?.onExerciseUpdated(updated)
        }
    }

    fun deleteExercise(id: String) {
        val removed = exercises.removeIf { it.id == id }
        if (removed) {
            output?.onExerciseDeleted(id)
        }
    }
}
