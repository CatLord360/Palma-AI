package com.example.palma.ai.tom

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
    private val aiKey = "AI - 2"

    //START of FUNCTION: writeEtiquette
    fun writeEtiquette(userKey: String, messageKey: String, message: String){
        //START of IF-STATEMENT:
        if(message.lowercase().trim().startsWith("hello")){
            writeHello(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.lowercase().trim().startsWith("good")){
            writeGood(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.lowercase().trim().startsWith("thank")){
            writeWelcome(userKey, messageKey, message)
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeEtiquette

    //START of FUNCTION: writeHello
    private fun writeHello(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        //START of IF-STATEMENT:
        if(message.lowercase().trim() == "hello" || message.lowercase().trim().startsWith("hello tom") || message.lowercase().trim().startsWith("hello everyone")){
            userReference.get().addOnSuccessListener{ snapshot ->
                val user = snapshot.getValue(User::class.java)

                messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    //START of FUNCTION: onDataChange
                    override fun onDataChange(snapshot: DataSnapshot){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        messageReference.child(key).setValue(Message(aiKey, date, time, "Greetings ${user?.username}"))
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeHello

    //START of FUNCTION: writeGood
    private fun writeGood(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        //START of IF-STATEMENT:
        if(message.lowercase().trim() == "good morning" || message.lowercase().trim().startsWith("good morning tom")){
            userReference.get().addOnSuccessListener{ snapshot ->
                val user = snapshot.getValue(User::class.java)

                messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    //START of FUNCTION: onDataChange
                    override fun onDataChange(snapshot: DataSnapshot){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        messageReference.child(key).setValue(Message(aiKey, date, time, "Good morning ${user?.username}"))
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.lowercase().trim() == "good afternoon" || message.lowercase().trim().startsWith("good afternoon tom")){
            userReference.get().addOnSuccessListener{ snapshot ->
                val user = snapshot.getValue(User::class.java)

                messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    //START of FUNCTION: onDataChange
                    override fun onDataChange(snapshot: DataSnapshot){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        messageReference.child(key).setValue(Message(aiKey, date, time, "Good afternoon ${user?.username}"))
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.lowercase().trim() == "good evening" || message.lowercase().trim().startsWith("good evening tom")){
            userReference.get().addOnSuccessListener{ snapshot ->
                val user = snapshot.getValue(User::class.java)

                messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    //START of FUNCTION: onDataChange
                    override fun onDataChange(snapshot: DataSnapshot){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        messageReference.child(key).setValue(Message(aiKey, date, time, "Good evening ${user?.username}"))
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.lowercase().trim() == "good night" || message.lowercase().trim().startsWith("good night tom")){
            userReference.get().addOnSuccessListener{ snapshot ->
                val user = snapshot.getValue(User::class.java)

                messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    //START of FUNCTION: onDataChange
                    override fun onDataChange(snapshot: DataSnapshot){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        messageReference.child(key).setValue(Message(aiKey, date, time, "Good night ${user?.username}, sweet dreams"))
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeGood

    //START of FUNCTION: writeWelcome
    private fun writeWelcome(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        //START of IF-STATEMENT:
        if(message.lowercase().trim() == "thank you" || message.lowercase().trim() == "thanks" || message.lowercase().trim().startsWith("thanks tom") || message.lowercase().trim().startsWith("thank you tom")){
            userReference.get().addOnSuccessListener{ snapshot ->
                val user = snapshot.getValue(User::class.java)

                messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                    //START of FUNCTION: onDataChange
                    override fun onDataChange(snapshot: DataSnapshot){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        messageReference.child(key).setValue(Message(aiKey, date, time, "You're welcome ${user?.username}"))
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeWelcome
}//END of CLASS: Etiquette