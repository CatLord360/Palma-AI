package com.example.palma.ai.rin

import com.example.palma.models.Message
import com.example.palma.models.Reminder
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//START of CLASS: Reminder
class Reminder{
    private val database = Firebase.database
    private val aiKey = "AI - 5"

    //START of FUNCTION: writeReminder
    fun writeReminder(userKey: String, messageKey: String, message: String){
        val list = message.lowercase().trim().split(" ")

        //START of IF-STATEMENT:
        if(list[1] == "set"){
            setReminder(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "remove"){
            removeReminder(userKey, messageKey, message)
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeReminder

    //START of FUNCTION: setReminder
    private fun setReminder(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val reminderReference = messageReference.child("Reminder")
        val list = message.lowercase().trim().split(" ")

        contactReference.get().addOnSuccessListener{ snapshot ->
            //START of FOR-LOOP:
            for(child in snapshot.children){
                val foundMessage = child.child("messageKey").getValue(String::class.java)

                //START of IF-STATEMENT:
                if(messageKey == foundMessage){
                    val contactKey = child.key

                    database.getReference("Palma/User/$userKey/Contact/$contactKey/Member").get()
                        .addOnSuccessListener { memberSnapshot ->
                            var cancel = "false"

                            //START of FOR-LOOP:
                            for(member in memberSnapshot.children){
                                val username = member.child("username").getValue(String::class.java)

                                //START of IF-STATEMENT:
                                if(username == "Palma" || username == "Tom"){
                                    cancel = "true"
                                    break
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP

                            //START of IF-STATEMENT:
                            if(cancel == "false"){
                                userReference.get().addOnSuccessListener{ snapshot ->
                                    if (list.size < 5) return@addOnSuccessListener
                                    val type = list[2]
                                    val reminder = list.subList(5, list.size).joinToString(" ")

                                    reminderReference.get().addOnSuccessListener{ reminderSnapshot ->
                                        val date = list[3]
                                        val time = list[4]
                                        var index = 1
                                        var reminderKey = "Reminder - $index"

                                        //START of WHILE-LOOP:
                                        while(reminderSnapshot.hasChild(reminderKey)){
                                            index++
                                            reminderKey = "Reminder - $index"
                                        }//END of WHILE-LOOP

                                        reminderReference.child(reminderKey).setValue(Reminder(userKey, type, date, time, reminder))
                                    }

                                    val current = LocalDateTime.now()
                                    val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                        //START of FUNCTION: onDataChange
                                        override fun onDataChange(snapshot: DataSnapshot){
                                            var index = 1
                                            var key = "message$index"

                                            //START of WHILE-LOOP:
                                            while(snapshot.hasChild(key)){
                                                index++
                                                key = "message$index"
                                            }//END of WHILE-LOOP

                                            val message = Message(aiKey, date, time, "I have added your $type reminder to $reminder darling...")
                                            messageReference.child(key).setValue(message)
                                        }//END of FUNCTION: onDataChange

                                        //START of FUNCTION: onCancelled
                                        override fun onCancelled(error: DatabaseError){
                                        }//END of FUNCTION: onCancelled
                                    })
                                }
                            }//END of IF-STATEMENT
                        }
                }//END of IF-STATEMENT
            }//END of FOR-LOOP
        }

    }//END of FUNCTION: setReminder

    //START of FUNCTION: removeReminder
    private fun removeReminder(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val reminderReference = messageReference.child("Reminder")
        val list = message.lowercase().trim().split(" ")

        contactReference.get().addOnSuccessListener{ snapshot ->
            //START of FOR-LOOP:
            for(child in snapshot.children){
                val foundMessage = child.child("messageKey").getValue(String::class.java)

                //START of IF-STATEMENT:
                if(messageKey == foundMessage){
                    val contactKey = child.key

                    database.getReference("Palma/User/$userKey/Contact/$contactKey/Member").get()
                        .addOnSuccessListener { memberSnapshot ->
                            var cancel = "false"

                            //START of FOR-LOOP:
                            for(member in memberSnapshot.children){
                                val username = member.child("username").getValue(String::class.java)

                                //START of IF-STATEMENT:
                                if(username == "Palma" || username == "Tom"){
                                    cancel = "true"
                                    break
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP

                            //START of IF-STATEMENT:
                            if(cancel == "false"){
                                userReference.get().addOnSuccessListener{ snapshot ->
                                    if (list.size < 5) return@addOnSuccessListener
                                    val type = list[2]
                                    val reminder = list.subList(5, list.size).joinToString(" ")

                                    reminderReference.get().addOnSuccessListener{ reminderSnapshot ->
                                        val date = list[3]
                                        val time = list[4]
                                        var reminderKey: String? = null

                                        //START of FOR-LOOP:
                                        for(child in reminderSnapshot.children) {
                                            val foundType = child.child("type").getValue(String::class.java)
                                            val foundDate = child.child("date").getValue(String::class.java)
                                            val foundTime = child.child("time").getValue(String::class.java)
                                            val foundReminder = child.child("reminder").getValue(String::class.java)

                                            //START of IF-STATEMENT
                                            if(type == foundType && date == foundDate && time == foundTime && reminder == foundReminder){
                                                reminderKey = child.key
                                                break
                                            }//END of IF-STATEMENT
                                        }//END of FOR-LOOP

                                        //START of IF-STATEMENT:
                                        if(reminderKey != null){
                                            reminderReference.child(reminderKey).removeValue()
                                        }//END of IF-STATEMENT

                                        messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                            //START of FUNCTION: onDataChange
                                            override fun onDataChange(snapshot: DataSnapshot){
                                                var index = 1
                                                var key = "message$index"

                                                //START of WHILE-LOOP:
                                                while(snapshot.hasChild(key)){
                                                    index++
                                                    key = "message$index"
                                                }//END of WHILE-LOOP

                                                val message = Message(aiKey, date, time, "I have removed your $type reminder to $reminder darling...")
                                                messageReference.child(key).setValue(message)
                                            }//END of FUNCTION: onDataChange

                                            //START of FUNCTION: onCancelled
                                            override fun onCancelled(error: DatabaseError){
                                            }//END of FUNCTION: onCancelled
                                        })
                                    }
                                }
                            }//END of IF-STATEMENT
                        }
                }//END of IF-STATEMENT
            }//END of FOR-LOOP
        }

    }//END of FUNCTION: removeReminder
}//END of CLASS: Reminder