package com.example.palma.ai.tom

import com.example.palma.models.Message
import com.example.palma.models.WeatherResponse
import com.example.palma.api.WeatherApiClient
import com.example.palma.models.FutureWeatherResponse
import com.example.palma.models.PastWeatherResponse
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//START of CLASS: Forecast
class Forecast{
    private val database = Firebase.database
    private val aiKey = "AI - 2"

    //START of FUNCTION: writeForecast
    fun writeForecast(userKey: String, messageKey: String, message: String){
        val list = message.lowercase().replace(Regex("[^a-z0-9\\s]"), "").trim().split(Regex("\\s+"))
        val currentKey = setOf("current", "now", "today")
        val pastKey = setOf("past", "yesterday", "before")
        val futureKey = setOf("future", "tomorrow", "later")

        //START of IF-STATEMENT:
        if(list.any {it in currentKey}){
            currentForecast(userKey, messageKey)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        else if(list.any {it in pastKey}){
            pastForecast(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        else if(list.any {it in futureKey}){
            futureForecast(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of ELSE-STATEMENT:
        else{
            Query().writeQuery(userKey, messageKey, message)
        }//END of ELSE-STATEMENT
    }//END of FUNCTION: writeForecast

    //START of FUNCTION: currentForecast
    private fun currentForecast(userKey: String, messageKey: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val now = LocalDateTime.now()
        val date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val lat = 14.5995
        val lon = 120.9842

        //START of IF-STATEMENT:
        if(lat !in -90.0..90.0 || lon !in -180.0..180.0){
            userReference.get().addOnSuccessListener{ userSnapshot ->
                messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                    //START of FUNCTION: onDataChange
                    override fun onDataChange(snapshot: DataSnapshot){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        val message = Message(aiKey, date, time, "Invalid coordinates.")
                        messageReference.child(key).setValue(message)
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
            return
        }//END of IF-STATEMENT

        userReference.get().addOnSuccessListener{
            messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot) {
                    var index = 1
                    var key = "message$index"

                    //START of WHILE-LOOP:
                    while (snapshot.hasChild(key)) {
                        index++
                        key = "message$index"
                    }//END of WHILE-LOOP

                    WeatherApiClient.retrofitService.getCurrentWeather(lat, lon, true)
                        .enqueue(object : Callback<WeatherResponse> {
                            //START of FUNCTION: onResponse
                            override fun onResponse(
                                call: Call<WeatherResponse>,
                                response: Response<WeatherResponse>
                            ) {
                                val forecast =
                                    if (response.isSuccessful && response.body() != null) {
                                        val body = response.body()!!
                                        val temp = body.current_weather.temperature
                                        val code = body.current_weather.weathercode
                                        "Current weather: ${weatherCode(code)}, ${temp}°C"
                                    }
                                    //START of ELSE-STATEMENT:
                                    else {
                                        "Unable to fetch current weather."
                                    }//END of ELSE-STATEMENT

                                val message = Message(aiKey, date, time, forecast)
                                messageReference.child(key).setValue(message)
                            }//END of FUNCTION: onResponse

                            //START of FUNCTION: onFailure
                            override fun onFailure(call: Call<WeatherResponse>, t: Throwable){
                                val message = Message(aiKey, date, time, "Failed to contact weather server.")
                                messageReference.child(key).setValue(message)
                            }//END of FUNCTION: onFailure
                        })
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError) {
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: currentForecast

    //START of FUNCTION: pastForecast
    private fun pastForecast(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val now = LocalDateTime.now()
        val date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val words = message.lowercase().trim().split(" ")
        var days = words.firstOrNull { it.toIntOrNull() != null }?.toInt() ?: 1
        for (word in words) {
            days = when (word) {
                "yesterday" -> 1
                "past" -> 2
                "before" -> 3
                "ago" -> days
                else -> days
            }
        }

        //START of IF-STATEMENT:
        if(days !in 1..5){
            userReference.get().addOnSuccessListener{ userSnapshot ->
                messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                    //START of FUNCTION: onDataChange
                    override fun onDataChange(snapshot: DataSnapshot){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        val message = Message(aiKey, date, time, "Sorry, I can only check up to 5 days in the past.")
                        messageReference.child(key).setValue(message)
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
            return
        }//END of IF-STATEMENT

        val targetDate = now.minusDays(days.toLong())
        val lat = 14.5995
        val lon = 120.9842

        userReference.get().addOnSuccessListener{ userSnapshot ->
            messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    var index = 1
                    var key = "message$index"

                    //START of WHILE-LOOP:
                    while(snapshot.hasChild(key)){
                        index++
                        key = "message$index"
                    }//END of WHILE-LOOP

                    WeatherApiClient.retrofitService.getPastWeather(
                        lat, lon,
                        targetDate.format(DateTimeFormatter.ISO_DATE),
                        targetDate.format(DateTimeFormatter.ISO_DATE)
                    ).enqueue(object : Callback<PastWeatherResponse> {
                        //START of FUNCTION: onResponse
                        override fun onResponse(call: Call<PastWeatherResponse>, response: Response<PastWeatherResponse>){
                            val forecast = if (response.isSuccessful && response.body() != null){
                                val daily = response.body()!!.daily
                                val temp = daily.temperature_2m_max.firstOrNull() ?: 0.0
                                val code = daily.weathercode.firstOrNull() ?: 0
                                "Weather $days day(s) ago: ${weatherCode(code)}, ${temp}°C"
                            }

                            //START of ELSE-STATEMENT:
                            else{
                                "Unable to fetch past weather."
                            }//END of ELSE-STATEMENT:

                            val message = Message(aiKey, date, time, forecast)
                            messageReference.child(key).setValue(message)
                        }//END of FUNCTION: onResponse

                        //START of FUNCTION: onFailure
                        override fun onFailure(call: Call<PastWeatherResponse>, t: Throwable){
                            val message = Message(aiKey, date, time, "Failed to contact weather server.")
                            messageReference.child(key).setValue(message)
                        }//END of FUNCTION: onFailure
                    })
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: pastForecast

    //START of FUNCTION: futureForecast
    private fun futureForecast(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val now = LocalDateTime.now()
        val date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val words = message.lowercase().trim().split(" ")
        var days = words.firstOrNull { it.toIntOrNull() != null }?.toInt() ?: 1
        for (word in words) {
            days = when (word){
                "tomorrow" -> 1
                "future" -> 2
                "later" -> 3
                else -> days
            }
        }

        //START of IF-STATEMENT:
        if(days !in 1..5){
            userReference.get().addOnSuccessListener{ userSnapshot ->
                messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                    //START of FUNCTION: onDataChange
                    override fun onDataChange(snapshot: DataSnapshot){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        val message = Message(aiKey, date, time, "Sorry, I can only check up to 5 days in the future.")
                        messageReference.child(key).setValue(message)
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
            return
        }//END of IF-STATEMENT

        val targetDate = now.plusDays(days.toLong())
        val lat = 14.5995
        val lon = 120.9842

        userReference.get().addOnSuccessListener{ userSnapshot ->
            messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    var index = 1
                    var key = "message$index"

                    //START of WHILE-LOOP:
                    while(snapshot.hasChild(key)){
                        index++
                        key = "message$index"
                    }//END of WHILE-LOOP

                    WeatherApiClient.retrofitService.getFutureWeather(
                        lat, lon,
                        targetDate.format(DateTimeFormatter.ISO_DATE),
                        targetDate.format(DateTimeFormatter.ISO_DATE)
                    ).enqueue(object : Callback<FutureWeatherResponse>{
                        //START of FUNCTION: onResponse
                        override fun onResponse(call: Call<FutureWeatherResponse>, response: Response<FutureWeatherResponse>){
                            val forecast = if (response.isSuccessful && response.body() != null){
                                val daily = response.body()!!.daily
                                val temp = daily.temperature_2m_max.firstOrNull() ?: 0.0
                                val code = daily.weathercode.firstOrNull() ?: 0
                                "Weather in $days day(s): ${weatherCode(code)}, ${temp}°C"
                            }

                            //START of ELSE-STATEMENT:
                            else{
                                "Unable to fetch future weather."
                            }//END of ELSE-STATEMENT

                            val message = Message(aiKey, date, time, forecast)
                            messageReference.child(key).setValue(message)
                        }//END of FUNCTION: onResponse

                        //START of FUNCTION: onFailure
                        override fun onFailure(call: Call<FutureWeatherResponse>, t: Throwable){
                            val message = Message(aiKey, date, time, "Failed to contact weather server.")
                            messageReference.child(key).setValue(message)
                        }//END of FUNCTION: onFailure
                    })
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: futureForecast

    //START of FUNCTION: weatherCode
    private fun weatherCode(code: Int): String {
        return when(code){
            0 -> "Clear sky"
            1, 2, 3 -> "Partly cloudy"
            45, 48 -> "Fog"
            51, 53, 55 -> "Drizzle"
            61, 63, 65 -> "Rain"
            71, 73, 75 -> "Snow"
            80, 81, 82 -> "Rain showers"
            95, 96, 99 -> "Thunderstorm"
            else -> "Unknown"
        }
    }//END of FUNCTION: weatherCode
}//END of CLASS: Forecast