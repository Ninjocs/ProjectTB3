package com.example.automacorp.model

data class MealDto(
    val id: Long,
    val name: String,
    val room: String,
)

data class MealCommandDto(
    val id: Long,
    val name: String,
    val room: String,
)
