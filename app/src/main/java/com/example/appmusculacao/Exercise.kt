package com.example.appmusculacao

import java.util.UUID

data class Exercise(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val repetitions: Int = 0,
    val series: Int = 0,
    val intervalSeconds: Int = 0,
    val muscleGroup: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    var paid: Boolean = false
)