package com.example.appmusculacao

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExerciseListInteractor(private val context: Context) {

    var output: ExerciseListInteractorOutput? = null

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "exercise_prefs"
        private const val KEY_EXERCISES = "exercises"
    }

    private fun saveExercises(exercises: List<ExerciseCRUD>) {
        val json = gson.toJson(exercises)
        prefs.edit().putString(KEY_EXERCISES, json).apply()
    }

    private fun loadExercises(): MutableList<ExerciseCRUD> {
        val json = prefs.getString(KEY_EXERCISES, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<ExerciseCRUD>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun createExercise(exercise: ExerciseCRUD) {
        val exercises = loadExercises()
        exercises.add(exercise)
        saveExercises(exercises)
        output?.onExerciseCreated(exercise)
    }

    fun getAllExercises() {
        val exercises = loadExercises()
        output?.onExercisesLoaded(exercises)
    }

    fun updateExercise(updated: ExerciseCRUD) {
        val exercises = loadExercises()
        val index = exercises.indexOfFirst { it.id == updated.id }
        if (index != -1) {
            exercises[index] = updated
            saveExercises(exercises)
            output?.onExerciseUpdated(updated)
        }
    }

    fun deleteExercise(id: String) {
        val exercises = loadExercises()
        val removed = exercises.removeIf { it.id == id }
        if (removed) {
            saveExercises(exercises)
            output?.onExerciseDeleted(id)
        }
    }
}

