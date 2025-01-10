package com.example.automacorp.service

import com.example.automacorp.model.PlaceDto
import retrofit2.Call
import retrofit2.http.*

interface PlacesApiService {

    @GET("places")
    fun findAll(): Call<List<PlaceDto>>

    @GET("places/{id}")
    fun findById(@Path("id") id: Long): Call<PlaceDto>

    @GET("places")
    fun findByName(@Query("name") name: String): Call<List<PlaceDto>>

    @PUT("places/{id}")
    fun updatePlaces(@Path("id") id: Long, @Body place: PlaceDto): Call<PlaceDto>

    @POST("places")
    fun createPlaces(@Body place: PlaceDto): Call<PlaceDto>

    @DELETE("places/{id}")
    fun deletePlaces(@Path("id") id: Long): Call<Void>
}