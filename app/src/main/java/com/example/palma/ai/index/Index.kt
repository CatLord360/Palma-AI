package com.example.palma.ai.index

import com.example.palma.models.Contact
import com.example.palma.models.Message
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//START of CLASS: Index
class Index{
    private val database = Firebase.database

    //START of FUNCTION: writeIndex
    fun writeIndex(userKey: String, username: String): Task<Void> {
        val contactReference = database.getReference("Palma/User/$userKey/Contact")
        val messageReference = database.getReference("Palma/Message")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        return messageReference.get().continueWithTask { messageTask ->
            //START of IF-STATEMENT:
            if (!messageTask.isSuccessful) {
                throw messageTask.exception ?: Exception("Failed to fetch messages")
            }//END of IF-STATEMENT

            val snapshot = messageTask.result
            var index = 1
            var messageKey = "Message - $index"

            //START of WHILE-LOOP:
            while(snapshot.hasChild(messageKey)) {
                index++
                messageKey = "Message - $index"
            }//END of WHILE-LOOP

            contactReference.get().continueWithTask { contactTask ->
                //START of IF-STATEMENT:
                if (!contactTask.isSuccessful) {
                    throw contactTask.exception ?: Exception("Failed to fetch contacts")
                }//END of IF-STATEMENT

                val contactSnapshot = contactTask.result
                var index = 1
                var contactKey = "Contact - $index"

                //START of WHILE-LOOP:
                while (contactSnapshot.hasChild(contactKey)) {
                    index++
                    contactKey = "Contact - $index"
                }//END of WHILE-LOOP

                val contact = Contact(messageKey, "Index", "33333", "index@ai.com", "ai")
                val message = Message("AI - 3", date, time, "Hello $username, I am Index")

                val contactTaskRef = contactReference.child(contactKey).setValue(contact)
                val messageTaskRef = messageReference.child("$messageKey/message1").setValue(message)

                return@continueWithTask Tasks.whenAll(contactTaskRef, messageTaskRef)
            }
        }
    }//END of FUNCTION: writeIndex

    //START of FUNCTION: writeMessage
    fun writeMessage(userKey: String, messageKey: String, message: String){
        val listMessage = message.lowercase().replace(Regex("[^a-z0-9\\s@]"), "").trim()
        val words = listMessage.split(Regex("\\s+"))

        val stopWords = setOf("is", "am", "are", "was", "were", "do", "did", "does", "my", "the", "a", "an", "of", "in", "on", "for", "to", "what", "what's", "whats", "who", "whose", "when", "how", "can", "have", "has", "had", "i", "you", "me")
        val questionWords = setOf("who", "whose", "what", "what's", "whats", "where", "when", "why", "how", "do", "does", "did", "can", "could", "is", "are", "will", "would", "should", "shall", "give")
        val etiquetteWords = setOf("hello", "hi", "hey", "greetings", "good", "morning", "afternoon", "evening", "night", "thank", "thanks", "bye", "goodbye", "goodnight", "later", "see", "take", "farewell")

        val keywords = words.filter { it.isNotBlank() && it !in stopWords }

        val isQuery = message.trim().endsWith("?") ||
                (words.isNotEmpty() && words[0] in questionWords) ||
                keywords.any {
                    it in setOf("email", "birthdate", "birthday", "mobile", "username", "gender", "remember", "know", "contact")
                }

        val isEtiquette = words.any { it in etiquetteWords }

        //START of IF-STATEMENT:
        if(isEtiquette){
            Etiquette().writeEtiquette(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(isQuery){
            Query().writeQuery(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(message.trim().startsWith("#")) {
            Command().writeCommand(userKey, messageKey, message)
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeMessage
}//END of CLASS: Index