package com.example.appmusculacao

class ExerciseInteractor {

    var output: ExerciseInteractorOutput? = null

    private val exercises = mutableListOf<Exercise>()

    fun addExercise(name: String) {
        if (name.isBlank()) {
            output?.onFailure("Nome do exercício não pode ser vazio")
            return
        }

        val newExercise = Exercises(
            id = (exercises.size + 1).toString(),
            name = name
        )

        exercises.add(newExercise)
        output?.onAddSuccess(newExercise)
    }

    fun removeExercise(name: String) {
        val removed = exercises.removeIf { it.name.equals(name, ignoreCase = true) }

        if (removed) {
            output?.onRemoveSuccess(name)
        } else {
            output?.onFailure("Exercício não encontrado")
        }
    }

    fun listExercises() {
        output?.onListSuccess(exercises.toList())
    }
}