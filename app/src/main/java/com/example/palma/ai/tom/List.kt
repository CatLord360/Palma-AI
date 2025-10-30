package com.example.palma.ai.tom

import com.example.palma.models.Item
import com.example.palma.models.List
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
    private val aiKey = "AI - 2"

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

        //START of IF-STATEMENT:
        if(list[1] == "load"){
            loadList(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "new"){
            newList(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "delete"){
            deleteList(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "add"){
            addItem(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "remove"){
            removeItem(userKey, messageKey, message)
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeList

    //START of FUNCTION: writeCommand
    private fun writeCommand(userKey: String, messageKey: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")

        userReference.get().addOnSuccessListener{ userSnapshot ->
            val list = arrayOf(
                "List of Command/s:",
                "#list command",
                "#list contact",
                "#list reminder",
                "#list new [name] [type]",
                "#list load [name]",
                "#list delete [name]",
                "#list add [name] [item]",
                "#list remove [name] [item]",
                "#reminder set daily [HH:mm] [reminder]",
                "#reminder set weekly [day] [HH:mm] [reminder]",
                "#reminder set monthly [dd] [HH:mm] [reminder]",
                "#reminder set annually [MM-dd] [HH:mm] [reminder]",
                "#reminder delete daily [HH:mm] [reminder]",
                "#reminder delete weekly [day] [HH:mm] [reminder]",
                "#reminder delete monthly [dd] [HH:mm] [reminder]",
                "#reminder delete annually [MM-dd] [HH:mm] [reminder]",
                "#contact write ai [ai]",
                "#contact write user [email]",
                "#contact write group [group]",
                "#contact delete ai [ai]",
                "#contact delete user [email]",
                "#contact delete group [group]",
                "#contact add ai [ai]",
                "#contact add user [email]",
                "#contact remove ai [ai]",
                "#contact remove user [user]"
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

                    success(userKey, messageKey, "command", "command", "")
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

                        success(userKey, messageKey, "contact", "contact", "")
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

                            success(userKey, messageKey, "reminder", "reminder", "")
                        }
                    }//END of IF-STATEMENT

                    //START of ELSE-STATEMENT:
                    else{
                        error(userKey, messageKey, "reminder")
                    }//END of ELSE-STATEMENT
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: writeReminder

    //START of FUNCTION: loadList
    private fun loadList(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val listReference = messageReference.child("List")
        val list = message.trim().split(" ")

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
                                if(username == "Palma"){
                                    cancel = "true"
                                    break
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP

                            //START of IF-STATEMENT:
                            if(cancel == "false"){
                                userReference.get().addOnSuccessListener{ userSnapshot ->
                                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                                        //START of FUNCTION: onDataChange
                                        override fun onDataChange(snapshot: DataSnapshot){
                                            //START of IF-STATEMENT:
                                            if(snapshot.hasChild("List")){
                                                listReference.get().addOnSuccessListener{ listSnapshot ->
                                                    val name = list[2]
                                                    var index = 1

                                                    //START of FOR-LOOP:
                                                    for(child in listSnapshot.children){
                                                        //START of IF-STATEMENT
                                                        if(name == child.child("name").getValue(String::class.java)){
                                                            var index = 1
                                                            var key = "message$index"

                                                            //START of WHILE-LOOP:
                                                            while(snapshot.hasChild(key)){
                                                                index = index + 1
                                                                key = "message$index"
                                                            }//END of WHILE-LOOP

                                                            //START of IF-STATEMENT:
                                                            if((child.child("type").getValue(String::class.java) == "public") || ((child.child("type").getValue(String::class.java) == "private") && (child.child("userKey").getValue(String::class.java) == userKey))){
                                                                //START of FOR-LOOP:
                                                                for(item in child.child("Item").children){
                                                                    val current = LocalDateTime.now()
                                                                    val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                                    val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

                                                                    messageReference.child("message$index").setValue(Message(aiKey, date, time, item.child("item").getValue(String::class.java)))
                                                                    index = index + 1
                                                                }//END of FOR-LOOP

                                                                success(userKey, messageKey, "load", name, "")
                                                            }//END of IF-STATEMENT

                                                            //START of ELSE-STATEMENT:
                                                            else{
                                                                error(userKey, messageKey, "userKey")
                                                            }//END of ELSE-STATEMENT

                                                            break
                                                        }//END of IF-STATEMENT

                                                        //START of IF-STATEMENT:
                                                        if((name != child.child("name").getValue(String::class.java)) && (index == listSnapshot.childrenCount.toInt())){
                                                            error(userKey, messageKey, "list")
                                                        }//END of IF-STATEMENT

                                                        index = index + 1
                                                    }//END of FOR-LOOP
                                                }
                                            }//END of IF-STATEMENT

                                            //START of ELSE-STATEMENT:
                                            else{
                                                error(userKey, messageKey, "list")
                                            }//END of ELSE-STATEMENT
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

    }//END of FUNCTION: loadList

    //START of FUNCTION: newList
    private fun newList(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val listReference = messageReference.child("List")
        val list = message.trim().split(" ")

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
                                if(username == "Palma"){
                                    cancel = "true"
                                    break
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP

                            //START of IF-STATEMENT:
                            if(cancel == "false"){
                                userReference.get().addOnSuccessListener{ userSnapshot ->
                                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                                        //START of FUNCTION: onDataChange
                                        override fun onDataChange(snapshot: DataSnapshot){
                                            val name = list[2]
                                            val type = list[3]

                                            //START of IF-STATEMENT:
                                            if(snapshot.hasChild("List")){
                                                listReference.get().addOnSuccessListener{ listSnapshot ->
                                                    var index = 1

                                                    //START of FOR-LOOP:
                                                    for(child in listSnapshot.children){
                                                        //START of IF-STATEMENT:
                                                        if(name == child.child("name").getValue(String::class.java)){
                                                            error(userKey, messageKey, "exist")
                                                            break
                                                        }//END of IF-STATEMENT

                                                        //START of IF-STATEMENT:
                                                        if((name != child.child("name").getValue(String::class.java)) && (index == listSnapshot.childrenCount.toInt())){
                                                            //START of IF-STATEMENT:
                                                            if(((type == "public") || (type == "private")) && (type.isNotBlank())){
                                                                index = 1
                                                                var listKey = "List - $index"

                                                                //START of WHILE-LOOP:
                                                                while(listSnapshot.hasChild(listKey)){
                                                                    index = index + 1
                                                                    listKey = "List - $index"
                                                                }//END of WHILE-LOOP

                                                                listReference.child(listKey).setValue(List(name, type, userKey))
                                                                success(userKey, messageKey, "new", name, "")
                                                            }//END of IF-STATEMENT

                                                            //START of ELSE-STATEMENT:
                                                            else{
                                                                error(userKey, messageKey, "type")
                                                            }//END of ELSE-STATEMENT
                                                        }//END of IF-STATEMENT

                                                        index = index + 1
                                                    }
                                                }
                                            }//END of IF-STATEMENT

                                            //START of ELSE-STATEMENT:
                                            else{
                                                //START of IF-STATEMENT:
                                                if(((type == "public") || (type == "private")) && (type.isNotBlank())){
                                                    listReference.child("List - 1").setValue(List(name, type, userKey))
                                                    success(userKey, messageKey, "new", name, "")
                                                }//END of IF-STATEMENT

                                                //START of ELSE-STATEMENT:
                                                else{
                                                    error(userKey, messageKey, "type")
                                                }//END of ELSE-STATEMENT
                                            }//END of ELSE-STATEMENT
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

    }//END of FUNCTION: newList

    //START of FUNCTION: deleteList
    private fun deleteList(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val listReference = messageReference.child("List")
        val list = message.trim().split(" ")

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
                                if(username == "Palma"){
                                    cancel = "true"
                                    break
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP

                            //START of IF-STATEMENT:
                            if(cancel == "false"){
                                userReference.get().addOnSuccessListener{ userSnapshot ->
                                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                                        //START of FUNCTION: onDataChange
                                        override fun onDataChange(snapshot: DataSnapshot){
                                            val name = list[2]

                                            //START of IF-STATEMENT:
                                            if(snapshot.hasChild("List")){
                                                listReference.get().addOnSuccessListener{ listSnapshot ->
                                                    var index = 1

                                                    //START of FOR-LOOP:
                                                    for(child in listSnapshot.children){
                                                        //START of IF-STATEMENT:
                                                        if(name == child.child("name").getValue(String::class.java)){
                                                            //START of IF-STATEMENT:
                                                            if((child.child("type").getValue(String::class.java) == "public") || ((child.child("type").getValue(String::class.java) == "private") && (child.child("userKey").getValue(String::class.java) == userKey))){
                                                                listReference.child(child.key.toString()).removeValue()
                                                                success(userKey, messageKey, "delete", name, "")
                                                            }//END of IF-STATEMENT

                                                            //START of ELSE-STATEMENT:
                                                            else{
                                                                error(userKey, messageKey, "userKey")
                                                            }//END of ELSE-STATEMENT
                                                        }//END of IF-STATEMENT

                                                        //START of IF-STATEMENT:
                                                        if((name != child.child("name").getValue(String::class.java)) && (index == listSnapshot.childrenCount.toInt())){
                                                            error(userKey, messageKey, "list")
                                                        }//END of IF-STATEMENT

                                                        index = index + 1
                                                    }//END of FOR-LOOP
                                                }
                                            }//END of IF-STATEMENT

                                            //START of ELSE-STATEMENT:
                                            else{
                                                error(userKey, messageKey, "list")
                                            }//END of ELSE-STATEMENT
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

    }//END of FUNCTION: deleteList

    //START of FUNCTION: addList
    private fun addItem(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val listReference = messageReference.child("List")
        val list = message.trim().split(" ")

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
                                if(username == "Palma"){
                                    cancel = "true"
                                    break
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP

                            //START of IF-STATEMENT:
                            if(cancel == "false"){
                                userReference.get().addOnSuccessListener{ userSnapshot ->
                                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                                        //START of FUNCTION: onDataChange
                                        override fun onDataChange(snapshot: DataSnapshot){
                                            val name = list[2]

                                            //START of IF-STATEMENT:
                                            if(snapshot.hasChild("List")){
                                                listReference.get().addOnSuccessListener{ listSnapshot ->
                                                    var index = 1

                                                    //START of FOR-LOOP:
                                                    for(child in listSnapshot.children){
                                                        //START of IF-STATEMENT:
                                                        if(name == child.child("name").getValue(String::class.java)){
                                                            val type = child.child("type").getValue(String::class.java)
                                                            val item = list.subList(3, list.size).joinToString(" ")

                                                            //START of IF-STATEMENT:
                                                            if((type == "public") || (type == "private") && (child.child("userKey").getValue(String::class.java) == userKey)){
                                                                index = 1
                                                                var itemKey = "Item - $index"

                                                                //START of WHILE-LOOP:
                                                                while(child.child("Item").hasChild(itemKey)){
                                                                    index = index + 1
                                                                    itemKey = "Item - $index"
                                                                }//END of WHILE-LOOP

                                                                listReference.child("${child.key}/Item/$itemKey").setValue(Item(item, userKey))
                                                                success(userKey, messageKey, "add", name, item)
                                                            }//END of IF-STATEMENT

                                                            //START of ELSE-STATEMENT:
                                                            else{
                                                                error(userKey, messageKey, "userKey")
                                                            }//END of ELSE-STATEMENT

                                                            break
                                                        }//END of IF-STATEMENT

                                                        //START of IF-STATEMENT:
                                                        if((name != child.child("name").getValue(String::class.java)) && (index == listSnapshot.childrenCount.toInt())){
                                                            error(userKey, messageKey, "list")
                                                        }//END of IF-STATEMENT

                                                        index = index + 1
                                                    }//END lof FOR-LOOP
                                                }
                                            }//END of IF-STATEMENT

                                            //START of ELSE-STATEMENT:
                                            else{
                                                error(userKey, messageKey, "list")
                                            }//END of ELSE-STATEMENT
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

    }//END of FUNCTION: addItem

    //START of FUNCTION: removeItem
    private fun removeItem(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val listReference = messageReference.child("List")
        val list = message.trim().split(" ")

        contactReference.get().addOnSuccessListener { snapshot ->
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
                                if(username == "Palma"){
                                    cancel = "true"
                                    break
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP

                            //START of IF-STATEMENT:
                            if(cancel == "false"){
                                userReference.get().addOnSuccessListener{ userSnapshot ->
                                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                                        //START of FUNCTION: onDataChange
                                        override fun onDataChange(snapshot: DataSnapshot){
                                            val name = list[2]

                                            //START of IF-STATEMENT:
                                            if(snapshot.hasChild("List")){
                                                listReference.get().addOnSuccessListener{ listSnapshot ->
                                                    var index = 1

                                                    //START of FOR-LOOP:
                                                    for(child in listSnapshot.children){
                                                        //START of IF-STATEMENT:
                                                        if(name == child.child("name").getValue(String::class.java)){
                                                            val type = child.child("type").getValue(String::class.java)
                                                            val item = list.subList(3, list.size).joinToString(" ")

                                                            //START of IF-STATEMENT:
                                                            if((type == "public") || (type == "private") && (child.child("userKey").getValue(String::class.java) == userKey)){
                                                                index = 1

                                                                //START of FOR-LOOP:
                                                                for(foundItem in child.child("Item").children){
                                                                    //START of IF-STATEMENT:
                                                                    if(item == foundItem.child("item").getValue(String::class.java)){
                                                                        listReference.child("${child.key}/Item/${foundItem.key}").removeValue()
                                                                        success(userKey, messageKey, "remove", name, item)

                                                                        break
                                                                    }//END of IF-STATEMENT

                                                                    //START of IF-STATEMENT:
                                                                    if(((item != foundItem.child("item").getValue(String::class.java))) && (index == child.child("Item").childrenCount.toInt())){
                                                                        error(userKey, messageKey, "item")
                                                                    }//END of IF-STATEMENT

                                                                    index = index + 1
                                                                }//END of FOR-LOOP
                                                            }//END of IF-STATEMENT

                                                            //START of ELSE-STATEMENT:
                                                            else{
                                                                error(userKey, messageKey, "userKey")
                                                            }//END of ELSE-STATEMENT

                                                            break
                                                        }//END of IF-STATEMENT

                                                        //START of IF-STATEMENT:
                                                        if((name != child.child("name").getValue(String::class.java)) && (index == listSnapshot.childrenCount.toInt())){
                                                            error(userKey, messageKey, "list")
                                                        }//END of IF-STATEMENT

                                                        index = index + 1
                                                    }//END of FOR-LOOP
                                                }
                                            }//END of IF-STATEMENT

                                            //START of ELSE-STATEMENT:
                                            else{
                                                error(userKey, messageKey, "list")
                                            }//END of ELSE-STATEMENT
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

    }//END of FUNCTION: removeItem

    //START of FUNCTION: success
    private fun success(userKey: String, messageKey: String, type: String, list: String, item: String){
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
                        response = "I have successfully finished loading the $list list..."
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "new"){
                        response = "I have successfully created the new list $list..."
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "delete"){
                        response = "I have successfully deleted the list $list..."
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "add"){
                        response = "I have successfully added the item $item in $list..."
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "remove"){
                        response = "I have successfully removed the item $item from $list..."
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
    private fun error(userKey: String, messageKey: String, type: String){
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
                    if(type == "reminder"){
                        response = "Unfortunately the are currently no reminders..."
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "list"){
                        response = "Unfortunately the list you have entered does not exist..."
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "exist"){
                        response = "Unfortunately the list already exists..."
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "userKey"){
                        response = "Unfortunately you are not allowed to have access to this list..."
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "type"){
                        response = "Unfortunately the type you have entered is invalid..."
                    }//END of IF-STATEMENT

                    //START of IF-STATEMENT:
                    if(type == "item"){
                        response = "Unfortunately the item you have entered does not exist..."
                    }//END of IF-STATEMENT

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