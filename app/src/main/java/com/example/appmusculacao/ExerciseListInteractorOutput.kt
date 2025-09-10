package com.example.appmusculacao

interface ExerciseListInteractorOutput {
    fun onExerciseCreated(exercise: ExerciseCRUD)
    fun onExercisesLoaded(exercises: List<ExerciseCRUD>)
    fun onExerciseUpdated(exercise: ExerciseCRUD)
    fun onExerciseDeleted(exerciseId: String)
    fun onError(message: String)
}