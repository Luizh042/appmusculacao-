package com.example.appmusculacao

class ExerciseListInteractorOutputMock : ExerciseListInteractorOutput {
    var didCreateExercise = false
    var didLoadExercises = false
    var didUpdateExercise = false
    var didDeleteExercise = false

    var createdExercise: ExerciseCRUD? = null
    var exercisesList: List<ExerciseCRUD> = emptyList()
    var updatedExercise: ExerciseCRUD? = null
    var deletedExerciseId: String? = null

    override fun onExerciseCreated(exercise: ExerciseCRUD) {
        didCreateExercise = true
        createdExercise = exercise
    }

    override fun onExercisesLoaded(exercises: List<ExerciseCRUD>) {
        didLoadExercises = true
        exercisesList = exercises
    }

    override fun onExerciseUpdated(exercise: ExerciseCRUD) {
        didUpdateExercise = true
        updatedExercise = exercise
    }

    override fun onExerciseDeleted(exerciseId: String) {
        didDeleteExercise = true
        deletedExerciseId = exerciseId
    }

    override fun onError(message: String) { }
}
