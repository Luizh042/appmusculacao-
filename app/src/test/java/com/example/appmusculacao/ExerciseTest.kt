package com.example.appmusculacao

class ExerciseTest : ExerciseInteractorOutput {

    private val interactor = ExerciseInteractor()

    init {
        interactor.output = this

        // Testes
        interactor.addExercise("Flexão")
        interactor.addExercise("Agachamento")
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