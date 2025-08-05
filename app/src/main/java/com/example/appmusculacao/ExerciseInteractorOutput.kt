package com.example.appmusculacao

interface ExerciseInteractorOutput {
    fun onAddSuccess(exercise: Exercise)
    fun onRemoveSuccess(name: String)
    fun onListSuccess(list: List<Exercise>)
    fun onFailure(error: String)
}