package com.example.appmusculacao

class ExerciseInteractor {

    var output: ExerciseInteractorOutput? = null

    private val exerciseList = mutableListOf<Exercise>()

    fun addExercise(exercise: Exercise) {
        if (exercise.name.isBlank()) {
            output?.onFailure("Nome do exercício não pode ser vazio")
            return
        }

        val newExercise = Exercise(
            name = exercise.name,
            repetitions = exercise.repetitions,
            series = exercise.series,
            intervalSeconds = exercise.intervalSeconds,
            muscleGroup = exercise.muscleGroup
        )

        //exercise.add(newExercise)
        output?.onAddSuccess(newExercise)
    }

    fun removeExercise(name: String) {
        val removed = exerciseList.removeIf { it.name.equals(name, ignoreCase = true) }

        if (removed) {
            output?.onRemoveSuccess(name)
        } else {
            output?.onFailure("Exercício não encontrado")
        }
    }

    fun listExercises() {
        output?.onListSuccess(exerciseList.toList())
    }
}