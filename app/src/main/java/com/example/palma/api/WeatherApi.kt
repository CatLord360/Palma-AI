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
    @GET("weather")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<WeatherResponse>//END of FUNCTION: getCurrentWeather

    //START of FUNCTION: getPastWeather
    @GET("onecall/timemachine")
    fun getPastWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("dt") timestamp: Long,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<PastWeatherResponse>//END of FUNCTION: getPastWeather

    //START of FUNCTION: getFutureWeather
    @GET("forecast")
    fun getFutureWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("dt") timestamp: Long,
        @Query("appid") apiKey: String,
        @Query("units") units: String
    ): Call<FutureWeatherResponse>//END of FUNCTION: getFutureForecast
}//END of INTERFACE: WeatherApi