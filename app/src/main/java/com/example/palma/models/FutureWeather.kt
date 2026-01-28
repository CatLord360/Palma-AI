package com.example.palma.models

import kotlin.collections.List

//START of DATA-CLASS: FutureWeather
data class FutureWeather(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val weathercode: List<Int>
)//END of DATA-CLASS: FutureWeather
