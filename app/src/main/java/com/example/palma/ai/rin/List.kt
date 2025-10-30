package com.example.palma.ai.rin

import com.example.palma.models.Message
import com.example.palma.models.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//START of CLASS: List
class List{
    private val database = Firebase.database
    private val aiKey = "AI - 5"

    //START of FUNCTION: writeList
    fun writeList(userKey: String, messageKey: String, message: String){
        val list = message.lowercase().trim().split(" ")

        //START of IF-STATEMENT:
        if(list[1] == "command"){
            writeCommand(userKey, messageKey)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "contact"){
            writeContact(userKey, messageKey)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "reminder"){
            writeReminder(userKey, messageKey)
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeList

    //START of FUNCTION: writeCommand
    private fun writeCommand(userKey: String, messageKey: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")

        userReference.get().addOnSuccessListener{ snapshot ->
            val list = arrayOf(
                "List of Command/s:",
                "#list command",
                "#list contact",
                "#list reminder",
                "#reminder set daily [HH:mm] [reminder]",
                "#reminder set weekly [day] [HH:mm] [reminder]",
                "#reminder set monthly [dd] [HH:mm] [reminder]",
                "#reminder set annually [MM-dd] [HH:mm] [reminder]",
                "#reminder delete daily [HH:mm] [reminder]",
                "#reminder delete weekly [day] [HH:mm] [reminder]",
                "#reminder delete monthly [dd] [HH:mm] [reminder]",
                "#reminder delete annually [MM-dd] [HH:mm] [reminder]"
            )

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

                    //START of FOR-LOOP:
                    for(i in 0 until list.size){
                        val current = LocalDateTime.now()
                        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                        val key = "message$index"

                        messageReference.child(key).setValue(Message(aiKey, date, time, list[i]))

                        index++
                    }//END of FOR-LOOP

                    success(userKey, messageKey, "command", "command")
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION:

    //START of FUNCTION: writeContact
    private fun writeContact(userKey: String, messageKey: String) {
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")

        userReference.get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)

            messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(messageSnapshot: DataSnapshot){
                    contactReference.get().addOnSuccessListener { contactSnapshot ->
                        val contactList = mutableListOf<String>()
                        contactList.add("List of ${user?.username}\'s Contact/s:")

                        //START of FOR-LOOP:
                        for(contact in contactSnapshot.children){
                            val username = contact.child("username").getValue(String::class.java) ?: ""
                            val type = contact.child("type").getValue(String::class.java) ?: ""
                            val mobile = contact.child("mobile").getValue(String::class.java) ?: ""
                            val email = contact.child("email").getValue(String::class.java) ?: ""

                            val contactString = "$username $type $mobile $email"
                            contactList.add(contactString)
                        }//END of FOR-LOOP

                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index++
                            key = "message$index"
                        }//END of WHILE-LOOP

                        //START of FOR-LOOP:
                        for(contactInfo in contactList){
                            val current = LocalDateTime.now()
                            val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                            val message = Message(aiKey, date, time, contactInfo)
                            messageReference.child(key).setValue(message)
                            index++
                        }//END of FOR-LOOP

                        success(userKey, messageKey, "contact", "contact")
                    }
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError) {
                }
                //END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: writeContact

    //START of FUNCTION: writeReminder
    private fun writeReminder(userKey: String, messageKey: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val reminderReference = messageReference.child("Reminder")

        userReference.get().addOnSuccessListener{ userSnapshot ->
            messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    //START of IF-STATEMENT:
                    if(snapshot.hasChild("Reminder")){
                        var index = 1
                        var key = "message$index"

                        //START of WHILE-LOOP:
                        while(snapshot.hasChild(key)){
                            index = index + 1
                            key = "message$index"
                        }//END of WHILE-LOOP

                        reminderReference.get().addOnSuccessListener{ reminderSnapshot ->
                            //START of FOR-LOOP:
                            for(reminder in reminderSnapshot.children){
                                val current = LocalDateTime.now()
                                val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                                messageReference.child("message$index").setValue(Message(aiKey, date, time, "${reminder.child("type").getValue(String::class.java)} reminder for ${reminder.child("reminder").getValue(String::class.java)}"))
                                index = index + 1

                                //START of IF-STATEMENT:
                                if(reminder.child("type").getValue(String::class.java) == "daily"){
                                    messageReference.child("message$index").setValue(Message(aiKey, date, time, "date: everyday"))
                                }//END of IF-STATEMENT

                                //START of IF-STATEMENT:
                                if(reminder.child("type").getValue(String::class.java) == "weekly"){
                                    messageReference.child("message$index").setValue(Message(aiKey, date, time, "date: every ${reminder.child("day").getValue(String::class.java)}"))
                                }//END of IF-STATEMENT

                                //START of IF-STATEMENT:
                                if(reminder.child("type").getValue(String::class.java) == "monthly"){
                                    messageReference.child("message$index").setValue(Message(aiKey, date, time, "date: ${reminder.child("date").getValue(String::class.java)} of the month"))
                                }//END of IF-STATEMENT

                                //START of IF-STATEMENT:
                                if(reminder.child("type").getValue(String::class.java) == "annually"){
                                    messageReference.child("message$index").setValue(Message(aiKey, date, time, "date: ${reminder.child("date").getValue(String::class.java)} of the year"))
                                }//END of IF-STATEMENT
                                index = index + 1

                                messageReference.child("message$index").setValue(Message(aiKey, date, time, "time: ${reminder.child("time").getValue(String::class.java)}"))
                                index = index + 1
                            }//END of FOR-LOOP

                            success(userKey, messageKey, "reminder", "reminder")
                        }
                    }//END of IF-STATEMENT

                    //START of ELSE-STATEMENT:
                    else{
                        error(userKey, messageKey)
                    }//END of ELSE-STATEMENT
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: writeReminder

    //START of FUNCTION: success
    private fun success(userKey: String, messageKey: String, type: String, list: String){
        val userReference = database.getReference("Palma/User/$userKey")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ userSnapshot ->
            messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    var index = 1
                    var key = "message$index"
                    var response = ""

                    //START of WHILE-LOOP:
                    while(snapshot.hasChild(key)){
                        index++
                        key = "message$index"
                    }//END of WHILE-LOOP

                    //START of IF-STATEMENT:
                    if((type == "command") || (type == "contact") || (type == "reminder") || (type == "load")){
                        response = "I have finished loading $list list darling..."
                    }//END of IF-STATEMENT

                    val message = Message(aiKey, date, time, response)
                    messageReference.child(key).setValue(message)
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: success

    //START of FUNCTION: error
    private fun error(userKey: String, messageKey: String){
        val userReference = database.getReference("Palma/User/$userKey")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ userSnapshot ->
            messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    var index = 1
                    var key = "message$index"
                    val response = "Sadly there are no reminders darling..."

                    //START of WHILE-LOOP:
                    while(snapshot.hasChild(key)){
                        index++
                        key = "message$index"
                    }//END of WHILE-LOOP

                    val message = Message(aiKey, date, time, response)
                    messageReference.child(key).setValue(message)
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: error
}//END of CLASS: List