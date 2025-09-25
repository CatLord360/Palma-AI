package com.example.palma.ai.index

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
    private val aiKey = "AI - 3"

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

        userReference.get().addOnSuccessListener{ snapshot ->
            val user = snapshot.getValue(User::class.java)
            val list = arrayOf(
                "List of Command/s:",
                "#list command",
                "#list contact",
                "#contact write ai [ai]",
                "#contact write user [email]",
                "#contact write group [group]",
                "#contact delete ai [ai]",
                "#contact delete user [email]",
                "#contact delete group [group]",
                "#contact add ai [ai]",
                "#contact add user [email]",
                "#contact remove ai [ai]",
                "#contact remove user [user]",
                "#compute sum of [number] [number]...",
                "#compute difference of [number] [number]...",
                "#compute product of [number] [number]...",
                "#compute quotient of [number] [divisor]",
                "#compute modulus of [number] [divisor]",
                "#compute average of [number] [number]...",
                "#compute square of [number]",
                "#compute cube of [number]",
                "#compute factorial of [number]",
                "#compute gcd of [number] [number]",
                "#compute lcm of [number] [number]",
                "#compute percentage of [whole] [part]",
                "#compute absolute of [number]",
                "#compute round of [number]",
                "How may I be of service, ${user?.username}"
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
                    }
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError) {
                }
                //END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: writeContact

    //START of FUNCTION: loadList
    private fun loadList(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val listReference = messageReference.child("List")
        val list = message.trim().split(" ")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

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
                                    listReference.get().addOnSuccessListener{ listSnapshot ->
                                        if (list.size < 3) return@addOnSuccessListener
                                        val name = list[2]
                                        var listKey: String? = null

                                        //START of FOR-LOOP:
                                        for(child in listSnapshot.children){
                                            val listName = child.child("name").getValue(String::class.java)

                                            //START of IF-STATEMENT
                                            if(listName != null && listName == name){
                                                listKey = child.key
                                                break
                                            }//END of IF-STATEMENT
                                        }//END of FOR-LOOP

                                        //START of IF-STATEMENT:
                                        if(listKey != null){
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

                                                    listReference.child("$listKey/Item").get().addOnSuccessListener{ itemSnapshot ->
                                                        //START of FOR-LOOP
                                                        for(child in itemSnapshot.children){
                                                            val item = child.child("item").getValue(String::class.java)
                                                            val key = "message$index"
                                                            val message = Message(aiKey, date, time, item)
                                                            messageReference.child(key).setValue(message)
                                                            index++
                                                        }//END of FOR-LOOP
                                                    }
                                                }//END of FUNCTION: onDataChange

                                                //START of FUNCTION: onCancelled
                                                override fun onCancelled(error: DatabaseError){
                                                }//END of FUNCTION: onCancelled
                                            })
                                        }//END of IF-STATEMENT
                                    }
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
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

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
                                    listReference.get().addOnSuccessListener{ listSnapshot ->
                                        val list = message.trim().split(" ")
                                        val name = list[2]
                                        val type = list[3]

                                        var index = 1
                                        var listKey = "List - $index"

                                        //START of WHILE-LOOP:
                                        while(listSnapshot.hasChild(listKey)){
                                            index++
                                            listKey = "List - $index"
                                        }//END of WHILE-LOOP

                                        listReference.child(listKey).setValue(List(name, type, userKey))

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

                                                val message = Message(aiKey, date, time, "I have successfully made the list $name...")
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

    }//END of FUNCTION: newList

    //START of FUNCTION: deleteList
    private fun deleteList(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val listReference = messageReference.child("List")
        val list = message.trim().split(" ")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

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
                                    listReference.get().addOnSuccessListener{ listSnapshot ->
                                        if (list.size < 3) return@addOnSuccessListener
                                        val name = list[2]
                                        var listKey: String? = null

                                        //START of FOR-LOOP:
                                        for(child in listSnapshot.children){
                                            val listName = child.child("name").getValue(String::class.java)

                                            //START of IF-STATEMENT
                                            if(listName != null && listName == name){
                                                listKey = child.key
                                                break
                                            }//END of IF-STATEMENT
                                        }//END of FOR-LOOP

                                        //START of IF-STATEMENT:
                                        if(listKey != null){
                                            listReference.child(listKey).removeValue()
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

                                                val message = Message(aiKey, date, time, "I have successfully deleted the list $name...")
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

    }//END of FUNCTION: deleteList

    //START of FUNCTION: addList
    private fun addItem(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val listReference = messageReference.child("List")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

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
                                    listReference.get().addOnSuccessListener{ listSnapshot ->
                                        val list = message.trim().split(" ")
                                        if (list.size < 4) return@addOnSuccessListener

                                        val name = list[2]
                                        val item = list.subList(3, list.size).joinToString(" ")

                                        var index = 1
                                        var listKey: String? = null

                                        //START of FOR-LOOP:
                                        for(child in listSnapshot.children) {
                                            val listName = child.child("name").getValue(String::class.java)

                                            //START of IF-STATEMENT
                                            if(listName != null && listName == name){
                                                listKey = child.key
                                                break
                                            }//END of IF-STATEMENT
                                        }//END of FOR-LOOP

                                        listReference.child("$listKey/Item").get().addOnSuccessListener{ itemSnapshot ->
                                            index = 1
                                            var itemKey = "Item - $index"

                                            //START of WHILE-LOOP
                                            while(itemSnapshot.hasChild(itemKey)){
                                                index++
                                                itemKey = "Item - $index"
                                            }//END of WHILE-LOOP

                                            listReference.child("$listKey/Item/$itemKey").setValue(Item(item, userKey))
                                        }

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

                                                val message = Message(aiKey, date, time, "I have successfully added $item to the list $name...")
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

    }//END of FUNCTION: addItem

    //START of FUNCTION: removeItem
    private fun removeItem(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val listReference = messageReference.child("List")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

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
                                if(username == "Palma" || username == "Tom"){
                                    cancel = "true"
                                    break
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP

                            //START of IF-STATEMENT:
                            if(cancel == "false"){
                                userReference.get().addOnSuccessListener{ snapshot ->
                                    listReference.get().addOnSuccessListener{ listSnapshot ->
                                        val list = message.trim().split(" ")
                                        if (list.size < 4) return@addOnSuccessListener

                                        val name = list[2]
                                        val item = list.subList(3, list.size).joinToString(" ")

                                        var index = 1
                                        var listKey: String? = null

                                        //START of FOR-LOOP:
                                        for(child in listSnapshot.children) {
                                            val listName = child.child("name").getValue(String::class.java)

                                            //START of IF-STATEMENT
                                            if(listName != null && listName == name){
                                                listKey = child.key
                                                break
                                            }//END of IF-STATEMENT
                                        }//END of FOR-LOOP

                                        listReference.child("$listKey/Item").get().addOnSuccessListener{ itemSnapshot ->
                                            index = 1
                                            var itemKey: String? = null

                                            //START of FOR-LOOP:
                                            for(child in itemSnapshot.children) {
                                                val itemName = child.child("item").getValue(String::class.java)

                                                //START of IF-STATEMENT:
                                                if(itemName != null && itemName == item){
                                                    itemKey = child.key
                                                    break
                                                }//END of IF-STATEMENT
                                            }//END of FOR-LOOP

                                            listReference.child("$listKey/Item/$itemKey").removeValue()
                                        }

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

                                                val message = Message(aiKey, date, time, "I have successfully removed $item from the list $name...")
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

    }//END of FUNCTION: removeItem
}//END of CLASS: List