package com.example.palma.ai.tom

import com.example.palma.ai.index.Index
import com.example.palma.ai.mid.Mid
import com.example.palma.ai.palma.Palma
import com.example.palma.ai.pinky.Pinky
import com.example.palma.ai.rin.Rin
import com.example.palma.models.Contact
import com.example.palma.models.Member
import com.example.palma.models.Message
import com.example.palma.models.User
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//START of CLASS: Contact
class Contact{
    private val database = Firebase.database
    private val aiKey = "AI - 2"

    //START of FUNCTION: writeContact
    fun writeContact(userKey: String, messageKey: String, message: String){
        val list = message.lowercase().trim().split(" ")

        //START of IF-STATEMENT:
        if(list[1] == "write"){
            //START of IF-STATEMENT:
            if(list[2] == "ai"){
                writeAI(userKey, messageKey, message)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(list[2] == "user"){
                writeUser(userKey, messageKey, message)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(list[2] == "group"){
                writeGroup(userKey, messageKey, message)
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "delete"){
            //START of IF-STATEMENT:
            if(list[2] == "ai"){
                deleteAI(userKey, messageKey, message)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(list[2] == "user"){
                deleteUser(userKey, messageKey, message)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(list[2] == "group"){
                deleteGroup(userKey, messageKey, message)
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "add"){
            //START of IF-STATEMENT:
            if(list[2] == "ai"){
                addAI(userKey, messageKey, message)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(list[2] == "user"){
                addUser(userKey, messageKey, message)
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "remove"){
            //START of IF-STATEMENT:
            if(list[2] == "ai"){
                removeAI(userKey, messageKey, message)
            }//END of IF-STATEMENT

            //START of IF-STATEMENT:
            if(list[2] == "user"){
                removeUser(userKey, messageKey, message)
            }//END of IF-STATEMENT
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeContact

    //START of FUNCTION: writeAI
    private fun writeAI(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")

        //START of IF-STATEMENT:
        if(list[3] == "Palma" || list[3] == "Tom" || list[3] == "Index" || list[3] == "Mid" || list[3] == "Rin" || list[3] == "Pinky"){
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

                        //START of IF-STATEMENT:
                        if(list[3] == "Palma"){
                            Palma().writePalma(userKey, user?.username.toString())
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(list[3] == "Tom"){
                            Tom().writeTom(userKey, user?.username.toString())
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(list[3] == "Index"){
                            Index().writeIndex(userKey, user?.username.toString())
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(list[3] == "Mid"){
                            Mid().writeMid(userKey, user?.username.toString())
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(list[3] == "Rin"){
                            Rin().writeRin(userKey, user?.username.toString())
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(list[3] == "Pinky"){
                            Pinky().writePinky(userKey, user?.username.toString())
                        }//END of IF-STATEMENT

                        messageReference.child(key).setValue(Message(aiKey, date, time, "${list[3]} has been added to ${user?.username}\' contact/s successfully..."))
                    }//END of FUNCTION: onDataChange

                    //START of FUNCTION: onCancelled
                    override fun onCancelled(error: DatabaseError){
                    }//END of FUNCTION: onCancelled
                })
            }
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeAI

    //START of FUNCTION: writeUser
    private fun writeUser(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")
        val email = list[3].trim()

        userReference.get().addOnSuccessListener { userSnapshot ->
            val user = userSnapshot.getValue(User::class.java)

            database.getReference("Palma/User").get().addOnSuccessListener{ snapshot ->
                var foundUserKey: String? = null

                //START of FOR-LOOP:
                for(child in snapshot.children){
                    val personal = child.child("Personal Information")
                    val foundEmail = personal.child("email").getValue(String::class.java)?.trim()

                    //START of IF-STATEMENT
                    if(email == foundEmail){
                        foundUserKey = child.key

                        database.getReference("Palma/User/$foundUserKey/Contact").get().addOnSuccessListener { contactSnapshot ->
                            var index = 1
                            var contactKey = "Contact - $index"

                            //START of WHILE-LOOP:
                            while(contactSnapshot.hasChild(contactKey)){
                                index++
                                contactKey = "Contact - $index"
                            }//END of WHILE-LOOP

                            database.getReference("Palma/Message").get().addOnSuccessListener { messageSnapshot ->
                                index = 1
                                var newMessage = "Message - $index"

                                //START of WHILE-LOOP:
                                while(messageSnapshot.hasChild(newMessage)){
                                    index++
                                    newMessage = "Message - $index"
                                }//END of WHILE-LOOP

                                database.getReference("Palma/User/$foundUserKey/Contact/$contactKey").setValue(Contact(newMessage, user?.username.toString(), user?.mobile.toString(), user?.email.toString(), "user"))
                                database.getReference("Palma/Message/$newMessage/message1").setValue(Message(aiKey, date, time, "${user?.username.toString()} would like to contact you..."))

                                contactReference.get().addOnSuccessListener { snapshot ->
                                    index = 1
                                    contactKey = "Contact - $index"

                                    //START of WHILE-LOOP:
                                    while(snapshot.hasChild(contactKey)){
                                        index++
                                        contactKey = "Contact - $index"
                                    }//END of WHILE-LOOP

                                    database.getReference("Palma/User/$foundUserKey/Personal Information").get().addOnSuccessListener { snapshot ->
                                        val user = snapshot.getValue(User::class.java)

                                        contactReference.child(contactKey).setValue(Contact(newMessage, user?.username.toString(), user?.mobile.toString(), user?.email.toString(), "user"))

                                        messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                            //START of FUNCTION: onDataChange
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                var index = 1
                                                var key = "message$index"

                                                //START of WHILE-LOOP:
                                                while(snapshot.hasChild(key)){
                                                    index++
                                                    key = "message$index"
                                                }//END of WHILE-LOOP

                                                val message = Message(aiKey, date, time, "I have successfully added ${user?.username.toString()} to your contacts...")
                                                messageReference.child(key).setValue(message)
                                            }//END of FUNCTION: onDataChange

                                            //START of FUNCTION: onCancelled
                                            override fun onCancelled(error: DatabaseError){
                                            }//END of FUNCTION: onCancelled
                                        })
                                    }
                                }
                            }
                        }
                        break
                    }//END of IF-STATEMENT
                }//END of FOR-LOOP
            }
        }
    }//END of FUNCTION: writeUser

    //START of FUNCTION: writeGroup
    private fun writeGroup(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")
        val group = list[3].trim()

        database.getReference("Palma/Message").get().addOnSuccessListener { snapshot ->
            var index = 1
            var newMessage = "Message - $index"

            //START of WHILE-LOOP:
            while(snapshot.hasChild(newMessage)){
                index++
                newMessage = "Message - $index"
            }//END of WHILE-LOOP

            contactReference.get().addOnSuccessListener { contactSnapshot ->
                index = 1
                var newContact = "Contact - $index"

                //START of WHILE-LOOP:
                while(contactSnapshot.hasChild(newContact)){
                    index++
                    newContact = "Contact - $index"
                }//END of WHILE-LOOP

                val contact = Contact(newMessage, group, "", "", "group")
                val message = Message(aiKey, date, time, "Welcome to $group")

                userReference.get().addOnSuccessListener{ userSnapshot ->
                    val user = userSnapshot.getValue(User::class.java)

                    contactReference.child(newContact).setValue(contact)
                    contactReference.child(newContact).child("Member/Member - 1").setValue(Member(user?.username.toString(), user?.mobile.toString(), user?.email.toString(), "user"))
                    contactReference.child(newContact).child("Member/Member - 2").setValue(Member("Tom", "11111", "tom@ai.com", "ai"))
                    database.getReference("Palma/Message/$newMessage/message1").setValue(message)
                }
            }

            messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot) {
                    var index = 1
                    var key = "message$index"

                    //START of WHILE-LOOP:
                    while(snapshot.hasChild(key)){
                        index++
                        key = "message$index"
                    }//END of WHILE-LOOP

                    val message = Message(aiKey, date, time, "I have successfully added $group from your contacts...")
                    messageReference.child(key).setValue(message)
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: writeGroup

    //START of FUNCTION: deleteAI
    private fun deleteAI(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")
        val delete = list[3].trim()

        //START of IF-STATEMENT:
        if(delete != "Tom"){
            contactReference.get().addOnSuccessListener { snapshot ->
                //START of FOR-LOOP
                for(child in snapshot.children){
                    val type = child.child("type").getValue(String::class.java)?.trim()
                    val username = child.child("username").getValue(String::class.java)?.trim()

                    //START of IF-STATEMENT
                    if(type == "ai" && username == delete){
                        val contactKey = child.key ?: continue
                        val deleteMessage = child.child("messageKey").getValue(String::class.java)

                        database.getReference("Palma/Message/$deleteMessage").removeValue()
                        contactReference.child(contactKey).removeValue()

                        messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                            //START of FUNCTION: onDataChange
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var index = 1
                                var key = "message$index"

                                //START of WHILE-LOOP:
                                while(snapshot.hasChild(key)){
                                    index++
                                    key = "message$index"
                                }//END of WHILE-LOOP

                                val message = Message(aiKey, date, time, "I have successfully removed $delete from your contacts...")
                                messageReference.child(key).setValue(message)
                            }//END of FUNCTION: onDataChange

                            //START of FUNCTION: onCancelled
                            override fun onCancelled(error: DatabaseError){
                            }//END of FUNCTION: onCancelled
                        })

                        break
                    }//END of IF-STATEMENT
                }//END of FOR-LOOP
            }
        }//END of IF-STATEMENT
    }//END of FUNCTION: deleteAI

    //START of FUNCTION: deleteUser
    private fun deleteUser(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")
        val delete = list[3].trim()

        contactReference.get().addOnSuccessListener { snapshot ->
            //START of FOR-LOOP:
            for(child in snapshot.children){
                val type = child.child("type").getValue(String::class.java)?.trim()
                val email = child.child("email").getValue(String::class.java)?.trim()

                //START of IF-STATEMENT:
                if(type == "user" && email == delete){
                    val contactKey = child.key ?: continue
                    val deleteMessage = child.child("messageKey").getValue(String::class.java)

                    database.getReference("Palma/Message/$deleteMessage").removeValue()
                    contactReference.child(contactKey).removeValue()

                    database.getReference("Palma/User").get().addOnSuccessListener { userSnapshot ->
                        var foundUserKey: String? = null

                        //START of FOR-LOOP:
                        for(user in userSnapshot.children){
                            val personal = user.child("Personal Information")
                            val foundEmail = personal.child("email").getValue(String::class.java)?.trim()

                            //START of IF-STATEMENT:
                            if(delete == foundEmail){
                                foundUserKey = user.key

                                database.getReference("Palma/User/$foundUserKey/Contact").get().addOnSuccessListener { contactSnapshot ->
                                    var contactKey: String? = null

                                    //START of FOR-LOOP:
                                    for(contact in contactSnapshot.children){
                                        val foundMessage = contact.child("messageKey").getValue(String::class.java)

                                        //START of IF-STATEMENT:
                                        if(deleteMessage == foundMessage){
                                            contactKey = contact.key

                                            database.getReference("Palma/User/$foundUserKey/Contact/$contactKey").removeValue()

                                            database.getReference("Palma/User/$foundUserKey/Personal Information").get().addOnSuccessListener { deleteSnapshot ->
                                                val user = deleteSnapshot.getValue(User::class.java)

                                                messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                                    //START of FUNCTION: onDataChange
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        var index = 1
                                                        var key = "message$index"

                                                        //START of WHILE-LOOP:
                                                        while(snapshot.hasChild(key)){
                                                            index++
                                                            key = "message$index"
                                                        }//END of WHILE-LOOP

                                                        val message = Message(aiKey, date, time, "I have successfully removed ${user?.username} from your contacts...")
                                                        messageReference.child(key).setValue(message)
                                                    }//END of FUNCTION: onDataChange

                                                    //START of FUNCTION: onCancelled
                                                    override fun onCancelled(error: DatabaseError){
                                                    }//END of FUNCTION: onCancelled
                                                })
                                            }

                                            break
                                        }//END of IF-STATEMENT
                                    }//END of FOR-LOOP
                                }
                            }//END of IF-STATEMENT
                        }//END of FOR-LOOP
                    }
                }//END of IF-STATEMENT
            }//END of FOR-LOOP
        }
    }//END of FUNCTION: deleteUser

    //START of FUNCTION: deleteGroup
    private fun deleteGroup(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")
        val delete = list[3].trim()

        contactReference.get().addOnSuccessListener{ snapshot ->
            //START of FOR-LOOP:
            for(child in snapshot.children){
                //START of IF-STATEMENT:
                if(delete == child.child("username").getValue(String::class.java) && child.child("type").getValue(String::class.java) == "group"){
                    val deleteKey = child.child("messageKey").getValue(String::class.java)

                    database.getReference("Palma/User").get().addOnSuccessListener{ userSnapshot ->
                        //START of FOR-LOOP:
                        for(user in userSnapshot.children){
                            database.getReference("Palma/User/${user.key}/Contact").get().addOnSuccessListener{ contactSnapshot ->
                                //START of FOR-LOOP:
                                for(contact in contactSnapshot.children){
                                    //START of IF-STATEMENT:
                                    if(deleteKey == contact.child("messageKey").getValue(String::class.java)){
                                        database.getReference("Palma/User/${user.key}/Contact/${contact.key}").removeValue()
                                        break
                                    }//END of IF-STATEMENT
                                }//END of FOR-LOOP
                            }
                        }//END of FOR-LOOP
                    }

                    database.getReference("Palma/Message/$deleteKey").removeValue()

                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        //START of FUNCTION: onDataChange
                        override fun onDataChange(snapshot: DataSnapshot) {
                            var index = 1
                            var key = "message$index"

                            //START of WHILE-LOOP:
                            while(snapshot.hasChild(key)){
                                index++
                                key = "message$index"
                            }//END of WHILE-LOOP

                            val message = Message(aiKey, date, time, "I have successfully deleted $delete from your contacts...")
                            messageReference.child(key).setValue(message)
                        }//END of FUNCTION: onDataChange

                        //START of FUNCTION: onCancelled
                        override fun onCancelled(error: DatabaseError){
                        }//END of FUNCTION: onCancelled
                    })
                }//END of IF-STATEMENT
            }//END of FOR-LOOP
        }
    }//END of FUNCTION: deleteGroup

    //START of FUNCTION: addAI
    private fun addAI(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")
        val add = list[3].trim()

        contactReference.get().addOnSuccessListener{ snapshot ->
            //START of FOR-LOOP:
            for(child in snapshot.children){
                val foundMessage = child.child("messageKey").getValue(String::class.java)

                //START of IF-STATEMENT:
                if(messageKey == foundMessage){
                    val contactKey = child.key

                    database.getReference("Palma/User/$userKey/Contact/$contactKey/Member").get().addOnSuccessListener{ memberSnapshot ->
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
                            //START of FOR-LOOP:
                            for(member in memberSnapshot.children){
                                val type = member.child("type").getValue(String::class.java)
                                val email = member.child("email").getValue(String::class.java)

                                //START of IF-STATEMENT:
                                if(type == "user"){
                                    database.getReference("Palma/User").get().addOnSuccessListener{ userSnapshot ->
                                        var foundUserKey: String? = null

                                        //START of FOR-LOOP:
                                        for(user in userSnapshot.children){
                                            val personal = user.child("Personal Information")
                                            val foundEmail = personal.child("email").getValue(String::class.java)?.trim()

                                            //START of IF-STATEMENT:
                                            if(email == foundEmail){
                                                foundUserKey = user.key

                                                database.getReference("Palma/User/$foundUserKey/Contact").get().addOnSuccessListener { contactSnapshot ->
                                                    //START of FOR-LOOP:
                                                    for(contact in contactSnapshot.children){
                                                        val foundMessageKey = contact.child("messageKey").getValue(String::class.java)

                                                        //START of IF-STATEMENT:
                                                        if(messageKey == foundMessageKey){
                                                            var index = 1
                                                            var newMemberKey = "Member - $index"
                                                            val foundContactKey = contact.key.toString()
                                                            val group = contact.child("username").getValue(String::class.java)

                                                            //START of WHILE-LOOP:
                                                            while(memberSnapshot.hasChild(newMemberKey)){
                                                                index++
                                                                newMemberKey = "Member - $index"
                                                            }//END of WHILE-LOOP

                                                            //START of IF-STATEMENT:
                                                            if(add == "Palma"){
                                                                database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member/$newMemberKey").setValue(Member("Palma", "00000", "palma@ai.com", "ai"))
                                                            }//END of IF-STATEMENT

                                                            //START of IF-STATEMENT:
                                                            if(add == "Tom"){
                                                                database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member/$newMemberKey").setValue(Member("Tom", "11111", "tom@ai.com", "ai"))
                                                            }//END of IF-STATEMENT

                                                            //START of IF-STATEMENT:
                                                            if(add == "Index"){
                                                                database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member/$newMemberKey").setValue(Member("Index", "33333", "index@ai.com", "ai"))
                                                            }//END of IF-STATEMENT

                                                            //START of IF-STATEMENT:
                                                            if(add == "Mid"){
                                                                database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member/$newMemberKey").setValue(Member("Mid", "44444", "mid@ai.com", "ai"))
                                                            }//END of IF-STATEMENT

                                                            //START of IF-STATEMENT:
                                                            if(add == "Rin"){
                                                                database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member/$newMemberKey").setValue(Member("Rin", "55555", "rin@ai.com", "ai"))
                                                            }//END of IF-STATEMENT

                                                            //START of IF-STATEMENT:
                                                            if(add == "Pinky"){
                                                                database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member/$newMemberKey").setValue(Member("Pinky", "66666", "pinky@ai.com", "ai"))
                                                            }//END of IF-STATEMENT

                                                            messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                                                //START of FUNCTION: onDataChange
                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    var index = 1
                                                                    var key = "message$index"

                                                                    //START of WHILE-LOOP:
                                                                    while(snapshot.hasChild(key)){
                                                                        index++
                                                                        key = "message$index"
                                                                    }//END of WHILE-LOOP

                                                                    val message = Message(aiKey, date, time, "I have successfully added $add to $group...")
                                                                    messageReference.child(key).setValue(message)
                                                                }//END of FUNCTION: onDataChange

                                                                //START of FUNCTION: onCancelled
                                                                override fun onCancelled(error: DatabaseError){
                                                                }//END of FUNCTION: onCancelled
                                                            })
                                                        }//END of IF-STATEMENT
                                                    }//END of FOR-LOOP
                                                }
                                            }//END of IF-STATEMENT
                                        }//END of FOR-LOOP
                                    }
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP
                        }//END of IF-STATEMENT
                    }
                }//END of IF-STATEMENT
            }//END of FOR-LOOP
        }
    }//END of FUNCTION: addAI

    //START of FUNCTION: addUser
    private fun addUser(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")
        val add = list[3].trim()

        database.getReference("Palma/User").get().addOnSuccessListener{ snapshot ->
            //START of FOR-LOOP:
            for(child in snapshot.children){
                val personal = child.child("Personal Information")
                val foundEmail = personal.child("email").getValue(String::class.java)

                //START of IF-STATEMENT:
                if(add == foundEmail){
                    contactReference.get().addOnSuccessListener{ contactSnapshot ->
                        //START of FOR-LOOP:
                        for(contact in contactSnapshot.children){
                            //START of IF-STATEMENT:
                            if(contact.child("messageKey").getValue(String::class.java) == messageKey){
                                contactReference.child("${contact.key}/Member").get().addOnSuccessListener{ memberSnapshot ->
                                    var index = 1
                                    var newMemberKey = "Member - $index"

                                    //START of WHILE-LOOP:
                                    while(memberSnapshot.hasChild(newMemberKey)){
                                        index++
                                        newMemberKey = "Member - $index"
                                    }//END of WHILE-LOOP

                                    contactReference.child("${contact.key}/Member/$newMemberKey").setValue(Member(personal.child("username").getValue(String::class.java).toString(), personal.child("mobile").getValue(String::class.java).toString(), personal.child("email").getValue(String::class.java).toString(), "user"))

                                    database.getReference("Palma/User/${child.key}/Contact").get().addOnSuccessListener{ childSnapshot ->
                                        index = 1
                                        var foundContactKey = "Contact - $index"
                                        //START of WHILE-LOOP:
                                        while(childSnapshot.hasChild(foundContactKey)){
                                            index++
                                            foundContactKey = "Contact - $index"
                                        }//END of WHILE-LOOP

                                        val contactData = mapOf(
                                            "username" to contact.child("username").getValue(String::class.java),
                                            "messageKey" to messageKey,
                                            "mobile" to contact.child("mobile").getValue(String::class.java),
                                            "email" to contact.child("email").getValue(String::class.java),
                                            "type" to contact.child("type").getValue(String::class.java)
                                        )

                                        val newContactRef = database.getReference("Palma/User/${child.key}/Contact/$foundContactKey")
                                        newContactRef.setValue(contactData).addOnSuccessListener{
                                            contact.child("Member").children.forEachIndexed { i, member ->
                                                val memberKey = "Member - ${i + 1}"
                                                val memberData = member.getValue(Member::class.java)
                                                newContactRef.child("Member/$memberKey").setValue(memberData)
                                            }
                                        }

                                        newContactRef.child("Member/$newMemberKey").setValue(Member(personal.child("username").getValue(String::class.java).toString(), personal.child("mobile").getValue(String::class.java).toString(), personal.child("email").getValue(String::class.java).toString(), "user"))
                                    }

                                    //START of FOR-LOOP:
                                    for(member in memberSnapshot.children){
                                        //START of FOR-LOOP:
                                        for(user in snapshot.children){
                                            val memberPersonal = user.child("Personal Information")
                                            val email = memberPersonal.child("email").getValue(String::class.java)

                                            //START of IF-STATEMENT:
                                            if(member.child("email").getValue(String::class.java) == email){
                                                val memberKey = user.key

                                                database.getReference("Palma/User/$memberKey/Contact").get().addOnSuccessListener{ newContactSnapshot ->
                                                    //START of FOR-LOOP:
                                                    for(newContact in newContactSnapshot.children){
                                                        //START of IF-STATEMENT:
                                                        if(newContact.child("messageKey").getValue(String::class.java) == messageKey){
                                                            val newContactKey = newContact.key

                                                            database.getReference("Palma/User/$memberKey/Contact/$newContactKey/Member/$newMemberKey").setValue(Member(personal.child("username").getValue(String::class.java).toString(), personal.child("mobile").getValue(String::class.java).toString(), personal.child("email").getValue(String::class.java).toString(), "user"))

                                                            messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                                                //START of FUNCTION: onDataChange
                                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                                    var index = 1
                                                                    var key = "message$index"

                                                                    //START of WHILE-LOOP:
                                                                    while(snapshot.hasChild(key)){
                                                                        index++
                                                                        key = "message$index"
                                                                    }//END of WHILE-LOOP

                                                                    val message = Message(aiKey, date, time, "I have successfully added ${personal.child("username").getValue(String::class.java).toString()} to ${contact.child("username").getValue(String::class.java).toString()}...")
                                                                    messageReference.child(key).setValue(message)
                                                                }//END of FUNCTION: onDataChange

                                                                //START of FUNCTION: onCancelled
                                                                override fun onCancelled(error: DatabaseError){
                                                                }//END of FUNCTION: onCancelled
                                                            })
                                                        }//END of IF-STATEMENT
                                                    }//END of FOR-LOOP
                                                }

                                                break
                                            }//END of IF-STATEMENT
                                        }//END of FOR-LOOP
                                    }//END of FOR-LOOP
                                }

                                break
                            }//END of IF-STATEMENT
                        }//END of FOR-LOOP
                    }

                    break
                }//END of IF-STATEMENT
            }//END of FOR-LOOP
        }
    }//END of FUNCTION: addUser

    //START of FUNCTION: removeAI
    private fun removeAI(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")
        val remove = list[3].trim()

        //START of IF-STATEMENT:
        if(remove != "Tom"){
            contactReference.get().addOnSuccessListener{ snapshot ->
                //START of FOR-LOOP:
                for(child in snapshot.children){
                    val foundMessageKey = child.child("messageKey").getValue(String::class.java)

                    //START of IF-STATEMENT:
                    if(messageKey == foundMessageKey){
                        val contactKey = child.key

                        database.getReference("Palma/User/$userKey/Contact/$contactKey/Member").get().addOnSuccessListener{ memberSnapshot ->
                            //START of FOR-LOOP:
                            for(member in memberSnapshot.children){
                                val type = member.child("type").getValue(String::class.java)
                                val email = member.child("email").getValue(String::class.java)

                                //START of IF-STATEMENT:
                                if(type == "user"){
                                    database.getReference("Palma/User").get().addOnSuccessListener{ userSnapshot ->
                                        //START of FOR-LOOP:
                                        for(user in userSnapshot.children){
                                            val personal = user.child("Personal Information")
                                            val foundEmail = personal.child("email").getValue(String::class.java)?.trim()

                                            //START of IF-STATEMENT:
                                            if(email == foundEmail){
                                                val foundUserKey = user.key

                                                database.getReference("Palma/User/$foundUserKey/Contact").get().addOnSuccessListener{ contactSnapshot ->
                                                    //START of FOR-LOOP:
                                                    for(contact in contactSnapshot.children){
                                                        val foundMessage = contact.child("messageKey").getValue(String::class.java)

                                                        //START of IF-STATEMENT:
                                                        if(messageKey == foundMessage){
                                                            val foundContactKey = contact.key.toString()
                                                            val group = contact.child("username").getValue(String::class.java)

                                                            database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member").get().addOnSuccessListener{ snapshot ->
                                                                //START of FOR-LOOP:
                                                                for(child in snapshot.children){
                                                                    //START of IF-STATEMENT:
                                                                    if(child.child("username").getValue(String::class.java) == remove && child.child("type").getValue(String::class.java) == "ai"){
                                                                        database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member/${child.key}").removeValue()

                                                                        messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                                                            //START of FUNCTION: onDataChange
                                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                                var index = 1
                                                                                var key = "message$index"

                                                                                //START of WHILE-LOOP:
                                                                                while(snapshot.hasChild(key)){
                                                                                    index++
                                                                                    key = "message$index"
                                                                                }//END of WHILE-LOOP

                                                                                val message = Message(aiKey, date, time, "I have successfully removed $remove from $group...")
                                                                                messageReference.child(key).setValue(message)
                                                                            }//END of FUNCTION: onDataChange

                                                                            //START of FUNCTION: onCancelled
                                                                            override fun onCancelled(error: DatabaseError){
                                                                            }//END of FUNCTION: onCancelled
                                                                        })

                                                                        break
                                                                    }//END of IF-STATEMENT
                                                                }//END of FOR-LOOP
                                                            }
                                                        }//END of IF-STATEMENT
                                                    }//END of FOR-LOOP
                                                }
                                            }//END of IF-STATEMENT
                                        }//END of FOR-LOOP
                                    }
                                }//END of IF-STATEMENT
                            }//END of FOR-LOOP
                        }
                    }//END of IF-STATEMENT
                }//END of FOR-LOOP
            }
        }//END of IF-STATEMENT
    }//END of FUNCTION: removeAI

    //START of FUNCTION: removeUser
    private fun removeUser(userKey: String, messageKey: String, message: String){
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val list = message.trim().split(" ")
        val remove = list[3].trim()

        contactReference.get().addOnSuccessListener{ snapshot ->
            //START of FOR-LOOP:
            for(child in snapshot.children){
                val foundMessageKey = child.child("messageKey").getValue(String::class.java)

                //START of IF-STATEMENT:
                if(messageKey == foundMessageKey){
                    database.getReference("Palma/User/$userKey/Contact/${child.key}/Member").get().addOnSuccessListener{ memberSnapshot ->
                        //START of FOR-LOOP:
                        for(member in memberSnapshot.children){
                            val type = member.child("type").getValue(String::class.java)
                            val email = member.child("email").getValue(String::class.java)

                            //START of IF-STATEMENT:
                            if(type == "user"){
                                database.getReference("Palma/User").get().addOnSuccessListener{ userSnapshot ->
                                    //START of FOR-LOOP:
                                    for(user in userSnapshot.children){
                                        val personal = user.child("Personal Information")
                                        val foundEmail = personal.child("email").getValue(String::class.java)?.trim()

                                        //START of IF-STATEMENT:
                                        if(email == foundEmail){
                                            val foundUserKey = user.key

                                            database.getReference("Palma/User/$foundUserKey/Contact").get().addOnSuccessListener{ contactSnapshot ->
                                                //START of FOR-LOOP:
                                                for(contact in contactSnapshot.children){
                                                    val foundMessage = contact.child("messageKey").getValue(String::class.java)

                                                    //START of IF-STATEMENT:
                                                    if(messageKey == foundMessage) {
                                                        val foundContactKey = contact.key.toString()
                                                        val group = contact.child("username").getValue(String::class.java)

                                                        database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member").get().addOnSuccessListener{ snapshot ->
                                                            //START of FOR-LOOP:
                                                            for(child in snapshot.children){
                                                                //START of IF-STATEMENT
                                                                if(remove == child.child("email").getValue(String::class.java) && child.child("type").getValue(String::class.java) == "user"){
                                                                    database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey/Member/${child.key}").removeValue()

                                                                    //START of IF-STATEMENT:
                                                                    if(remove == foundEmail){
                                                                        database.getReference("Palma/User/$foundUserKey/Contact/$foundContactKey").removeValue()

                                                                        messageReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                                                            //START of FUNCTION: onDataChange
                                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                                var index = 1
                                                                                var key = "message$index"

                                                                                //START of WHILE-LOOP:
                                                                                while(snapshot.hasChild(key)){
                                                                                    index++
                                                                                    key = "message$index"
                                                                                }//END of WHILE-LOOP

                                                                                val message = Message(aiKey, date, time, "I have successfully removed ${personal.child("username").getValue(String::class.java)} from $group...")
                                                                                messageReference.child(key).setValue(message)
                                                                            }//END of FUNCTION: onDataChange

                                                                            //START of FUNCTION: onCancelled
                                                                            override fun onCancelled(error: DatabaseError){
                                                                            }//END of FUNCTION: onCancelled
                                                                        })

                                                                        break
                                                                    }//END of IF-STATEMENT
                                                                }//END of IF-STATEMENT
                                                            }//END of FOR-LOOP
                                                        }
                                                    }
                                                }//END of FOR-LOOP
                                            }
                                        }//END of IF-STATEMENT
                                    }//END of FOR-LOOP
                                }
                            }//END of IF-STATEMENT
                        }//END of FOR-LOOP
                    }
                }//END of IF-STATEMENT
            }//END of FOR-LOOP
        }
    }//END of FUNCTION: removeUser
}//END of CLASS: Contact