package com.example.automacorp.model

data class PlaceDto(
    val id: Long,
    val name: String,
    val currentHumidity: Double?,
    val currentTemperature: Double?,
    val windows: List<MealDto>?
) {

}

data class PlaceCommandDto(
    val id: Long,
    val name: String,
    val currentTemperature: Double?,
    val currentHumidity: Double?,
    val windows: List<MealCommandDto>?
)