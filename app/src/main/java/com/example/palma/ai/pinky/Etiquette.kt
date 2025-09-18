package com.example.palma.ai.pinky

import com.example.palma.models.Message
import com.example.palma.models.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//START of CLASS: Etiquette
class Etiquette{
    private val database = Firebase.database
    private val aiKey = "AI - 6"

    //START of FUNCTION: writeEtiquette
    fun writeEtiquette(userKey: String, messageKey: String, message: String){
        val listMessage = message.lowercase().replace(Regex("[^a-z0-9\\s@]"), "").trim()
        val words = listMessage.split(Regex("\\s+"))

        val greetingWords = setOf("hello", "hi", "hey", "greetings")
        val goodWords = setOf("good", "morning", "afternoon", "evening", "night")
        val gratitudeWords = setOf("thank", "thanks")
        val farewellWords = setOf("bye", "goodbye", "later", "see", "take", "farewell")

        val isGreeting = words.any { it in greetingWords }
        val isGood = words.any { it in goodWords }
        val isGratitude = words.any { it in gratitudeWords }
        val isFarewell = words.any{ it in farewellWords }

        //START of IF-STATEMENT:
        if(isGreeting){
            writeGreeting(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(isGood){
            writeGood(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(isGratitude){
            writeWelcome(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(isFarewell){
            writeFarewell(userKey, messageKey, message)
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeEtiquette

    //START of FUNCTION: writeGreeting
    private fun writeGreeting(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val cleaned = message.lowercase().trim()
        val words = cleaned.split(Regex("\\s+"))

        val greetingWords = setOf("hello", "hi", "hey", "greetings")
        val groupWords = setOf("everyone", "all", "guys", "friends", "team")

        //START of IF-STATEMENT:
        if (words.isNotEmpty() && words[0] in greetingWords){
            val directedToPinky = cleaned.contains("pinky")
            val directedToGroup = words.any { it in groupWords }
            val singleWordGreeting = words.size == 1

            //START of IF-STATEMENT:
            if(directedToPinky || directedToGroup || singleWordGreeting){
                userReference.get().addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(User::class.java)

                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                        //START of FUNCTION: onDataChange
                        override fun onDataChange(snapshot: DataSnapshot){
                            var index = 1
                            var key = "message$index"

                            //START of WHILE-LOOP:
                            while (snapshot.hasChild(key)){
                                index++
                                key = "message$index"
                            }//END of WHILE-LOOP

                            val response = "Hewo ${user?.username} ^w^"

                            messageReference.child(key).setValue(
                                Message(aiKey, date, time, response)
                            )
                        }//END of FUNCTION: onDataChange

                        //START of FUNCTION: onCancelled
                        override fun onCancelled(error: DatabaseError) {}
                        //END of FUNCTION: onCancelled
                    })
                }
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeGreeting

    //START of FUNCTION: writeGood
    private fun writeGood(userKey: String, messageKey: String, message: String) {
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val cleaned = message.lowercase().trim()
        val words = cleaned.split(Regex("\\s+"))

        val goodResponses = mapOf(
            "morning" to "Good morning",
            "afternoon" to "Good afternoon",
            "evening" to "Good evening",
            "night" to "Good night"
        )

        val groupWords = setOf("everyone", "all", "guys", "friends", "team")

        //START of IF-STATEMENT:
        if(words.isNotEmpty() && (
                    (words[0] == "good" && words.size >= 2 && words[1] in goodResponses.keys) ||
                            (words[0] in goodResponses.keys)
                    )
        ){
            val keyWord = if (words[0] == "good") words[1] else words[0]
            val baseResponse = goodResponses[keyWord] ?: "Hello"

            val directedToPinky = cleaned.contains("pinky")
            val directedToGroup = words.any { it in groupWords }
            val singleGreeting = words.size <= 2 // e.g., "good morning", "morning"

            //START of IF-STATEMENT:
            if(directedToPinky || directedToGroup || singleGreeting){
                userReference.get().addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(User::class.java)

                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                        //START of FUNCTION: onDataChange
                        override fun onDataChange(snapshot: DataSnapshot){
                            var index = 1
                            var key = "message$index"

                            //START of WHILE-LOOP:
                            while (snapshot.hasChild(key)){
                                index++
                                key = "message$index"
                            }//END of WHILE-LOOP

                            val response = when {
                                directedToPinky -> when (keyWord) {
                                    "morning" -> "$baseResponse ${user?.username} -_-"
                                    "afternoon" -> "$baseResponse ${user?.username} *u*"
                                    "evening" -> "$baseResponse ${user?.username} :D"
                                    "night" -> "$baseResponse ${user?.username}, sweet dreams *<3-"
                                    else -> "$baseResponse ${user?.username}"
                                }
                                directedToGroup -> "$baseResponse ${user?.username}, hi everyone too! ^_^"
                                keyWord == "night" -> "$baseResponse ${user?.username}, sweet dreams *<3-"
                                else -> "$baseResponse ${user?.username}"
                            }

                            messageReference.child(key).setValue(
                                Message(aiKey, date, time, response)
                            )
                        }//END of FUNCTION: onDataChange

                        //START of FUNCTION: onCancelled
                        override fun onCancelled(error: DatabaseError) {}
                        //END of FUNCTION: onCancelled
                    })
                }
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeGood

    //START of FUNCTION: writeWelcome
    private fun writeWelcome(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val cleaned = message.lowercase().trim()
        val words = cleaned.split(Regex("\\s+"))

        val thankWords = setOf("thank", "thanks")
        val groupWords = setOf("everyone", "all", "guys", "friends", "team")

        //START of IF-STATEMENT:
        if(words.isNotEmpty() && thankWords.any { words[0].startsWith(it) }){
            val directedToPinky = cleaned.contains("pinky")
            val directedToGroup = words.any { it in groupWords }
            val singleWordThanks = words.size == 1 || (words.size == 2 && words[0] in thankWords)

            //START of IF-STATEMENT:
            if(directedToPinky || directedToGroup || singleWordThanks){
                userReference.get().addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(User::class.java)

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

                            val response = "You're welcome ${user?.username} *u-"

                            messageReference.child(key).setValue(Message(aiKey, date, time, response))
                        }//END of FUNCTION: onDataChange

                        //START of FUNCTION: onCancelled
                        override fun onCancelled(error: DatabaseError) {}
                        //END of FUNCTION: onCancelled
                    })
                }
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeWelcome

    //START of FUNCTION: writeFarewell
    private fun writeFarewell(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val cleaned = message.lowercase().trim()
        val words = cleaned.split(Regex("\\s+"))

        val farewellWords = setOf("bye", "goodbye", "later", "see", "take", "farewell")
        val groupWords = setOf("everyone", "all", "guys", "friends", "team")

        //START of IF-STATEMENT:
        if(words.isNotEmpty() && words.any { it in farewellWords }){
            val directedToPinky = cleaned.contains("pinky")
            val directedToGroup = words.any { it in groupWords }
            val singleFarewell = words.size <= 3

            //START of IF-STATEMENT:
            if(directedToPinky || directedToGroup || singleFarewell){
                userReference.get().addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(User::class.java)

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

                            val response = "Bye bye ${user?.username}..."

                            messageReference.child(key).setValue(Message(aiKey, date, time, response))
                        }//END of FUNCTION: onDataChange

                        //START of FUNCTION: onCancelled
                        override fun onCancelled(error: DatabaseError){
                        }//END of FUNCTION: onCancelled
                    })
                }
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeFarewell
}//END of CLASS: Etiquette