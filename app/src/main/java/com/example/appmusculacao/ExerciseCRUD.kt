package com.example.appmusculacao

import java.util.UUID

data class ExerciseCRUD (
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val repetitions: Int,
    val series: Int,
    val intervalSeconds: Int,
    val muscleGroup: String,
    val createdAt: Long = System.currentTimeMillis()
)
