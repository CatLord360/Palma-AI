package com.example.palma.api

import com.example.palma.models.WeatherResponse
import com.example.palma.models.PastWeatherResponse
import com.example.palma.models.FutureWeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//START of INTERFACE: WeatherApi
interface WeatherApi{
    //START of FUNCTION: getCurrentWeather
    @GET("v1/forecast")
    fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") current: Boolean = true
    ): Call<WeatherResponse>//END of FUNCTION: getCurrentWeather

    //START of FUNCTION: getPastWeather
    @GET("v1/forecast")
    fun getPastWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weathercode"
    ): Call<PastWeatherResponse>//END of FUNCTION: getPastWeather

    //START of FUNCTION: getFutureWeather
    @GET("v1/forecast")
    fun getFutureWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,weathercode"
    ): Call<FutureWeatherResponse>//END of FUNCTION: getFutureWeather
}//END of INTERFACE: WeatherApi