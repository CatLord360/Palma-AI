package com.example.palma.ai.pinky

import com.example.palma.models.FutureWeatherResponse
import com.example.palma.models.Message
import com.example.palma.models.PastWeatherResponse
import com.example.palma.models.WeatherResponse
import com.example.palma.api.WeatherApiClient as WeatherApi
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//START of CLASS: Forecast
class Forecast{
    private val database = Firebase.database
    private val aiKey = "AI - 6"

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
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        WeatherApi.retrofitService.getCurrentWeather("Manila", "53e782f981e55ead0e8c1323ebbebf8a", "metric").enqueue(object : Callback<WeatherResponse> {
            //START of FUNCTION: onResponse
            override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>){
                //START of IF-STATEMENT:
                if(response.isSuccessful && response.body() != null){
                    val weather = response.body()!!
                    val condition = weather.weather.firstOrNull()?.description ?: "Unknown"
                    val temp = weather.main.temp
                    val forecast = "Current weather: $condition, $temp°C"

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

                                val message = Message(aiKey, date, time, forecast)
                                messageReference.child(key).setValue(message)
                            }//END of FUNCTION: onDataChange

                            //START of FUNCTION: onCancelled
                            override fun onCancelled(error: DatabaseError){
                            }//END of FUNCTION: onCancelled
                        })
                    }
                }//END of IF-STATEMENT
            }//END of FUNCTION: onResponse

            //START of FUNCTION: onFailure
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable){
            }//END of FUNCTION: onFailure
        })
    }//END of FUNCTION: currentForecast

    //START of FUNCTION: pastForecast
    private fun pastForecast(userKey: String, messageKey: String, message: String) {
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val now = LocalDateTime.now()
        val date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.lowercase().trim().split(" ")
        val pastKey = setOf("past", "yesterday", "before", "ago")
        var foundPastKey = ""
        var days = 0

        //START of FOR-LOOP:
        for(word in list){
            //START of IF-STATEMENT:
            if(word in pastKey){
                foundPastKey = word
                break
            }//END of IF-STATEMENT
        }//END of FOR-LOOP

        //START of IF-STATEMENT:
        if(foundPastKey == "yesterday"){
            days = 1
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(foundPastKey == "past"){
            days = 2
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(foundPastKey == "before"){
            days = 3
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(foundPastKey == "ago"){
            val number = list.firstOrNull { it.toIntOrNull() != null }?.toIntOrNull()
            if (number != null) days = number
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if (days !in 1..5){
            val forecast = "Sorry, I can only check up to 5 days in the past."

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

                        val message = Message(aiKey, date, time, forecast)
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
        val timestamp = targetDate.atZone(ZoneId.of("UTC")).toEpochSecond()
        val lat = 14.5995
        val lon = 120.9842

        WeatherApi.retrofitService.getPastWeather(lat, lon, timestamp, "53e782f981e55ead0e8c1323ebbebf8a", "metric").enqueue(object : Callback<PastWeatherResponse>{
            //START of FUNCTION: onResponse
            override fun onResponse(call: Call<PastWeatherResponse>, response: Response<PastWeatherResponse>){
                //START of IF-STATEMENT:
                if(response.isSuccessful && response.body() != null){
                    val past = response.body()!!
                    val condition = past.current.weather.firstOrNull()?.description ?: "Unknown"
                    val temp = past.current.temp
                    val forecast = "Weather $days day(s) ago: $condition, $temp°C"

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

                                val message = Message(aiKey, date, time, forecast)
                                messageReference.child(key).setValue(message)
                            }//END of FUNCTION: onDataChange

                            //START of FUNCTION: onCancelled
                            override fun onCancelled(error: DatabaseError){
                            }//END of FUNCTION: onCancelled
                        })
                    }
                }//END of IF-STATEMENT

                //START of ELSE-STATEMENT:
                else{
                    val forecast = "Unable to retrieve past weather (${response.code()})."

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

                                val message = Message(aiKey, date, time, forecast)
                                messageReference.child(key).setValue(message)
                            }//END of FUNCTION: onDataChange

                            //START of FUNCTION: onCancelled
                            override fun onCancelled(error: DatabaseError){
                            }//END of FUNCTION: onCancelled
                        })
                    }
                }//END of ELSE-STATEMENT
            }//END of FUNCTION: onResponse

            //START of FUNCTION: onFailure
            override fun onFailure(call: Call<PastWeatherResponse>, t: Throwable){
                val forecast = "Failed to contact weather server."

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

                            val message = Message(aiKey, date, time, forecast)
                            messageReference.child(key).setValue(message)
                        }//END of FUNCTION: onDataChange

                        //START of FUNCTION: onCancelled
                        override fun onCancelled(error: DatabaseError){
                        }//END of FUNCTION: onCancelled
                    })
                }
            }//END of FUNCTION: onFailure
        })
    }//END of FUNCTION: pastForecast

    //START of FUNCTION: futureForecast
    private fun futureForecast(userKey: String, messageKey: String, message: String) {
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val now = LocalDateTime.now()
        val date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.lowercase().trim().split(" ")
        val futureKey = setOf("future", "tomorrow", "later", "next")
        var foundKey = ""
        var days = 0

        //START of FOR-LOOP:
        for(word in list){
            //START of IF-STATEMENT:
            if(word in futureKey){
                foundKey = word
                break
            }//END of IF-STATEMENT
        }//END of FOR-LOOP

        //START of IF-STATEMENT:
        if(foundKey == "tomorrow"){
            days = 1
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(foundKey == "future"){
            days = 2
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(foundKey == "later"){
            days = 3
        }//END of IF-STATEMENT

        val number = list.firstOrNull { it.toIntOrNull() != null }?.toIntOrNull()
        if (number != null) days = number

        //START of IF-STATEMENT:
        if(days !in 1..5){
            val forecast = "Sorry, I can only check up to 5 days in the future."

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

                        val message = Message(aiKey, date, time, forecast)
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
        val timestamp = targetDate.atZone(ZoneId.of("UTC")).toEpochSecond()
        val lat = 14.5995
        val lon = 120.9842

        WeatherApi.retrofitService.getFutureWeather(lat, lon, timestamp, "53e782f981e55ead0e8c1323ebbebf8a", "metric").enqueue(object : Callback<FutureWeatherResponse>{
            //START of FUNCTION: onResponse
            override fun onResponse(call: Call<FutureWeatherResponse>, response: Response<FutureWeatherResponse>){
                //START of IF-STATEMENT:
                if(response.isSuccessful && response.body() != null){
                    val future = response.body()!!
                    val condition = future.current.weather.firstOrNull()?.description ?: "Unknown"
                    val temp = future.current.temp
                    val forecast = "Weather in $days day(s): $condition, $temp°C"

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

                                val message = Message(aiKey, date, time, forecast)
                                messageReference.child(key).setValue(message)
                            }//END of FUNCTION: onDataChange

                            //START of FUNCTION: onCancelled
                            override fun onCancelled(error: DatabaseError){
                            }//END of FUNCTION: onCancelled
                        })
                    }
                }//END of IF-STATEMENT

                //START of ELSE-STATEMENT:
                else{
                    val forecast = "Unable to retrieve past weather (${response.code()})."

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

                                val message = Message(aiKey, date, time, forecast)
                                messageReference.child(key).setValue(message)
                            }//END of FUNCTION: onDataChange

                            //START of FUNCTION: onCancelled
                            override fun onCancelled(error: DatabaseError){
                            }//END of FUNCTION: onCancelled
                        })
                    }
                }//END of ELSE-STATEMENT
            }//END of FUNCTION: onResponse

            //START of FUNCTION: onFailure
            override fun onFailure(call: Call<FutureWeatherResponse>, t: Throwable){
                val forecast = "Failed to contact weather server."

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

                            val message = Message(aiKey, date, time, forecast)
                            messageReference.child(key).setValue(message)
                        }//END of FUNCTION: onDataChange

                        //START of FUNCTION: onCancelled
                        override fun onCancelled(error: DatabaseError){
                        }//END of FUNCTION: onCancelled
                    })
                }
            }//END of FUNCTION: onFailure
        })
    }//END of FUNCTION: futureForecast
}//END of CLASS: Forecast