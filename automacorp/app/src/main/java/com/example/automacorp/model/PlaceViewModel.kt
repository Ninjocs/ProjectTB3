package com.example.automacorp.model

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.automacorp.service.ApiServices
import com.example.automacorp.service.ApiServices.placesApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlaceViewModel: ViewModel() {
    var place by mutableStateOf <PlaceDto?>(null)
    val placesState = MutableStateFlow(PlaceList())

    fun findAll() {
        viewModelScope.launch(context = Dispatchers.IO) { // (1)
            runCatching { ApiServices.placesApiService.findAll().execute() }
                .onSuccess {
                    val places = it.body() ?: emptyList()
                    placesState.value = PlaceList(places) // (2)
                }
                .onFailure {
                    it.printStackTrace()
                    placesState.value = PlaceList(emptyList(), it.stackTraceToString() ) // (3)
                }
        }
    }



    fun findPlaceByIdOrName(id: Long?, name: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                Log.d("PlaceActivity", "Searching for place with ID: $id or Name: $name")

                when {
                    id != null -> ApiServices.placesApiService.findById(id).execute() // Single place by ID
                    name != null -> ApiServices.placesApiService.findByName(name).execute() // List of places by name
                    else -> null
                }
            }
                .onSuccess { response ->
                    if (response != null) {
                        Log.d("PlaceActivity", "Raw API response: $response")

                        val place: PlaceDto? = when {
                            id != null -> response.body() as? PlaceDto // Directly cast to PlaceDto for ID search
                            name != null -> {
                                val placesList = response.body() as? List<PlaceDto> // Cast response to List<PlaceDto>
                                placesList?.firstOrNull() // Get the first match if the list is not empty
                            }
                            else -> null
                        }

                        if (place != null) {
                            Log.d("PlaceActivity", "Found place: $place")
                            this@PlaceViewModel.place = place // Update state with the found place
                        } else {
                            Log.d("PlaceActivity", "No place found")
                        }
                    } else {
                        Log.d("PlaceActivity", "API response was null")
                    }
                }
                .onFailure {
                    Log.e("PlaceActivity", "Error finding place", it)
                }
        }
    }


    fun updatePlace(id: Long, placeDto: PlaceDto) {
        if (id <= 0) {
            Log.e("PlaceActivity", "Invalid place ID: $id")
            return
        }
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching {
                ApiServices.placesApiService.deletePlaces(id).execute()
            }
                .onSuccess {

                    val newPlace = PlaceDto(
                        id = 0,  // New ID will be generated by the backend (or set manually if needed)
                        name = placeDto.name,
                        currentHumidity = placeDto.currentHumidity,
                        currentTemperature = placeDto.currentTemperature,
                        windows = placeDto.windows
                    )

                    runCatching {
                        ApiServices.placesApiService.createPlaces(newPlace).execute()
                    }
                        .onSuccess {
                            val createdPlace = it.body()
                            if (createdPlace != null) {
                            } else {
                            }
                        }
                        .onFailure { error ->
                            error.printStackTrace()
                        }
                }
                .onFailure { error ->
                    error.printStackTrace()
                }
        }
    }



    fun createPlace(placeDto: PlaceDto) {
        val command = PlaceDto(
            id = placeDto.id,
            name = placeDto.name,
            currentHumidity = placeDto.currentHumidity?.let { Math.round(it * 10) / 10.0 },
            currentTemperature = placeDto.currentTemperature,
            windows = placeDto.windows?.map { meal ->
                MealDto(
                    id = meal.id,
                    name = meal.name,
                    room = meal.room,
                )
            }
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.placesApiService.createPlaces(command).execute() }
                .onSuccess {
                    place = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    place = null
                }
        }
    }

    fun deletePlace(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching {
                placesApiService.deletePlaces(id).execute()
            }.onSuccess { response ->
                if (response.isSuccessful) {
                    place = null
                }
            }
        }
    }
}
