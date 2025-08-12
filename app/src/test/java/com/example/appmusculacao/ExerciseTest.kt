package com.example.appmusculacao

import java.util.UUID

class ExerciseTest : ExerciseInteractorOutput {

    private val interactor = ExerciseInteractor()

    init {
        interactor.output = this

        // Testes
        interactor.addExercise(Exercise(UUID.randomUUID().toString(), "Supino Reto", 12, 4, 60))
        interactor.addExercise(Exercise(UUID.randomUUID().toString(), "Flexão", 12, 4, 60))
        interactor.listExercises()
        interactor.removeExercise("Flexão")
        interactor.listExercises()
        interactor.removeExercise("Corrida") // Não existe
    }

    override fun onAddSuccess(exercise: Exercise) {
        println("Exercício adicionado: ${exercise.name}")
    }

    override fun onRemoveSuccess(name: String) {
        println("Exercício removido: $name")
    }

    override fun onListSuccess(list: List<Exercise>) {
        println("Lista de exercícios:")
        list.forEach {
            println("- ${it.name}")
        }
    }

    override fun onFailure(error: String) {
        println("Erro: $error")
    }
}