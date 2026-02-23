package com.example.palma.ai.rin

import android.util.Log
import com.example.palma.models.Message
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//START of CLASS: Query
class Query{
    private val database = Firebase.database
    private val aiKey = "AI - 5"
    private val cancel = setOf(
        "what", "whats", "what's", "when", "where", "which", "who", "whom", "whose", "why", "how", "could", "would",
        "is", "are", "am", "was", "were", "do", "does", "did",
        "the", "a", "an", "and", "or", "of", "to", "for", "in", "on", "at",
        "my", "your", "his", "her", "their", "our", "someone", "something",
        "other", "they", "them", "that", "this", "these", "those", "give", "know"
    )

    //START of FUNCTION: writeQuery
    fun writeQuery(userKey: String, messageKey: String, message: String){
        val stopWords = setOf("is","am","are","was","were","did","does","my","the","a","an","of","in","on","for","to","give","whats","what's","i","me","could","would")
        val interrogative = setOf("what", "whats", "what's", "when", "where", "which", "who", "whom", "whose", "why", "how", "could", "do")
        val ai = setOf("you", "your", "you're")
        val userDataFields = setOf("username","gender","birthdate","birthday", "mobile","email","contact","name","number")

        val cleanedMessage = message.lowercase().replace(Regex("[^a-z0-9\\s@]"), "").trim()
        val list = cleanedMessage.split(Regex("\\s+"))
        val lastInterrogativeIndex = list.indexOfLast{it in interrogative}

        val query = if(lastInterrogativeIndex != -1){
            list.subList(lastInterrogativeIndex, list.size).joinToString(" ")
        }//END of IF-STATEMENT

        //START of ELSE-STATEMENT:
        else{cleanedMessage}//END of ELSE-STATEMENT

        val keywords = list.filter{it.isNotBlank() && it !in stopWords}
        val isAiQuery = keywords.any{it in ai} && keywords.any{it in userDataFields}
        val isUserQuery = keywords.any{it in userDataFields}
        var classification = ""

        //START of IF-STATEMENT:
        if(isAiQuery){
            classification = "ai"
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(isUserQuery){
            classification = "user"
        }//END of IF-STATEMENT

        //START of ELSE-STATEMENT:
        if(!isAiQuery && !isUserQuery){
            classification = "log"
        }//END of IF-STATEMENT

        queryLog(userKey, messageKey, classification, query)
    }//END of FUNCTION: writeQuery

    //START of FUNCTION: queryAI
    private fun queryAI(messageKey: String, message: String){
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val username = "Rin"
        val gender = "female"
        val mobile = "55555"
        val email = "rin@ai.com"
        val type = "ai"

        val fieldMap = mapOf(
            "username" to Pair(setOf("username", "user", "name"), username),
            "gender" to Pair(setOf("gender", "sex"), gender),
            "email" to Pair(setOf("email", "mail", "address"), email),
            "mobile" to Pair(setOf("mobile", "phone", "number", "contact"), mobile),
            "type" to Pair(setOf("type", "are", "you"), type)
        )

        val stopWords = setOf(
            "what", "whats", "what's", "when", "where", "which", "who", "whom", "whose", "why", "how",
            "is", "are", "am", "was", "were", "do", "does", "did",
            "the", "a", "an", "and", "or", "of", "to", "for", "in", "on", "at",
            "my", "your", "his", "her", "their", "our", "someone", "something",
            "other", "they", "them", "that", "this", "these", "those", "give", "could", "would"
        )

        val words = message.lowercase().split(Regex("[^\\w']+")).filter { it.isNotBlank() }
        val keywords = words.filter { it !in stopWords }.toSet()

        var matchedData: String? = null
        var matchedLabel: String? = null

        //START of FOR-LOOP:
        for((label, pair) in fieldMap){
            //START of IF-STATEMENT:
            if(keywords.any { it in pair.first }){
                matchedLabel = label
                matchedData = pair.second
                break
            }//END of IF-STATEMENT
        }//END of FOR-LOOP

        val cleanedPhrase = words
            .filter { it !in stopWords }
            .joinToString(" ")
            .replace(Regex("\\byour\\b", RegexOption.IGNORE_CASE), "my")
            .replace(Regex("\\byou\\b", RegexOption.IGNORE_CASE), "I")
            .replace(Regex("\\bme\\b", RegexOption.IGNORE_CASE), "you")
            .replace(Regex("\\bmy\\b", RegexOption.IGNORE_CASE), "your")
            .replace(Regex("\\bi\\b", RegexOption.IGNORE_CASE), "you")
            .replace(Regex("\\bam\\b", RegexOption.IGNORE_CASE), "are")
            .trim()

        val responseText = if (matchedData != null && matchedLabel != null) {
            val phrase = cleanedPhrase.replace(Regex("\\b$type\\b"), "I")
            "My $phrase is $matchedData...".replaceFirstChar { it.uppercase() }
        } else {
            "I don’t know what $cleanedPhrase is."
        }

        messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
            //START of FUNCTION: onDataChange
            override fun onDataChange(snapshot: DataSnapshot){
                var newIndex = 1
                var newKey = "message$newIndex"

                //START of WHILE-LOOP:
                while(snapshot.hasChild(newKey)){
                    newIndex++
                    newKey = "message$newIndex"
                }//END of WHILE-LOOP

                val responseMessage = Message(aiKey, date, time, responseText)
                messageReference.child(newKey).setValue(responseMessage)
            }//END of FUNCTION: onDataChange

            //START of FUNCTION: onCancelled
            override fun onCancelled(error: DatabaseError){
            }//END of FUNCTION: onCancelled
        })
    }//END of FUNCTION: queryAI

    //START of FUNCTION: queryUser
    private fun queryUser(userKey: String, messageKey: String, query: String){
        val userReference = database.getReference("Palma/User")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val selfKey = setOf("my", "me", "mine", "i")
        val otherKey = setOf("their", "his", "her", "someone", "other", "they", "them", "of", "theirs", "him", "hers", "person", "user", "someone\'s", "somebody", "someones", "somebody\'s")
        val requirementKey = setOf("username", "user", "name", "birthdate", "birthday", "birth", "gender", "sex", "email", "mail", "address", "mobile", "phone", "number", "contact")
        val list = query.lowercase().replace(Regex("[^a-z0-9\\s@]"), "").trim().split(Regex("[^\\w']+"))
        val foundRequirement = list.map{it.removeSuffix("s")}.filter{it in requirementKey}
        val keywords = list.filter{it !in cancel && it !in requirementKey}.toSet()

        var foundUsername = ""
        var foundGender = ""
        var foundBirthdate = ""
        var foundMobile = ""
        var foundEmail = ""

        messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
            //START of FUNCTION: onDataChange
            override fun onDataChange(messageSnapshot: DataSnapshot){
                var index = 1
                var newLogKey = "message$index"

                //START of WHILE-LOOP:
                while(messageSnapshot.hasChild(newLogKey)){
                    index++
                    newLogKey = "message$index"
                }//END of WHILE-LOOP

                //START of IF-STATEMENT:
                if(list.any{it in selfKey}){
                    userReference.child("$userKey/Personal Information").get().addOnSuccessListener{ userSnapshot ->
                        foundUsername = userSnapshot.child("username").getValue(String::class.java).toString()
                        foundGender = userSnapshot.child("gender").getValue(String::class.java).toString()
                        foundBirthdate = userSnapshot.child("birthdate").getValue(String::class.java).toString()
                        foundMobile = userSnapshot.child("mobile").getValue(String::class.java).toString()
                        foundEmail = userSnapshot.child("email").getValue(String::class.java).toString()

                        val requirementMap = mapOf(
                            listOf("username", "user", "name") to foundUsername,
                            listOf("birthdate", "birthday", "birth") to foundBirthdate,
                            listOf("gender", "sex") to foundGender,
                            listOf("email", "mail", "address") to foundEmail,
                            listOf("mobile", "phone", "number", "contact") to foundMobile
                        )

                        val foundData = foundRequirement.mapNotNull{requirement ->
                            requirementMap.entries.firstOrNull{(keys, _) ->
                                keys.any{ requirement.contains(it, ignoreCase = true) }
                            }?.let{(_, value) ->
                                "$requirement is $value"
                            }
                        }

                        val joined = when(foundData.size){
                            1 -> foundData[0]
                            2 -> foundData.joinToString(" and ")
                            else -> foundData.dropLast(1).joinToString(", ") + ", and " + foundData.last()
                        }
                        val requirements = when(foundRequirement.size){
                            1 -> foundRequirement[0]
                            2 -> foundRequirement.joinToString(" and ")
                            else -> foundRequirement.dropLast(1).joinToString(", ") + ", and " + foundData.last()
                        }

                        val message = if(joined.isNotBlank()){
                            "Darling, your $joined."
                        }else{
                            "I'm sorry, Darling… I couldn't find the $requirements."
                        }

                        messageReference.child(newLogKey).setValue(Message(aiKey, date, time, message))
                    }
                }//END of IF-STATEMENT

                //START of IF-STATEMENT:
                if(list.any{it in otherKey}){
                    val foundList = mutableListOf<String>()

                    userReference.get().addOnSuccessListener{userSnapshot ->
                        //START of FOR-LOOP:
                        for(foundUser in userSnapshot.children){
                            foundUsername = foundUser.child("Personal Information/username").getValue(String::class.java).toString()
                            foundGender = foundUser.child("Personal Information/gender").getValue(String::class.java).toString()
                            foundBirthdate = foundUser.child("Personal Information/birthdate").getValue(String::class.java).toString()
                            foundMobile = foundUser.child("Personal Information/mobile").getValue(String::class.java).toString()
                            foundEmail = foundUser.child("Personal Information/email").getValue(String::class.java).toString()

                            foundList.addAll(listOf(foundUsername, foundGender, foundBirthdate, foundMobile, foundEmail))

                            Log.d("found user", foundList.joinToString(", "))

                            //START of IF-STATEMENT:
                            if(foundList.any{it in keywords}){
                                val requirementMap = mapOf(
                                    listOf("username", "user", "name") to foundUsername,
                                    listOf("birthdate", "birthday", "birth") to foundBirthdate,
                                    listOf("gender", "sex") to foundGender,
                                    listOf("email", "mail", "address") to foundEmail,
                                    listOf("mobile", "phone", "number", "contact") to foundMobile
                                )

                                val foundData = foundRequirement.mapNotNull{requirement ->
                                    requirementMap.entries.firstOrNull{(keys, _) ->
                                        keys.any{ requirement.contains(it, ignoreCase = true) }
                                    }?.let{(_, value) ->
                                        "$requirement is $value"
                                    }
                                }

                                val joined = when(foundData.size){
                                    1 -> foundData[0]
                                    2 -> foundData.joinToString(" and ")
                                    else -> foundData.dropLast(1).joinToString(", ") + ", and " + foundData.last()
                                }
                                val requirements = when(foundRequirement.size){
                                    1 -> foundRequirement[0]
                                    2 -> foundRequirement.joinToString(" and ")
                                    else -> foundRequirement.dropLast(1).joinToString(", ") + ", and " + foundData.last()
                                }

                                val message = if(joined.isNotBlank()){
                                    "Darling, $joined."
                                }else{
                                    "I'm sorry, Darling… I couldn't find the $requirements."
                                }

                                messageReference.child(newLogKey).setValue(Message(aiKey, date, time, message))
                            }//END of IF-STATEMENT

                            foundList.clear()
                        }//END of FOR-LOOP
                    }
                }//END of IF-STATEMENT
            }//END of FUNCTION: onDataChange

            //START of FUNCTION: onCancelled
            override fun onCancelled(error: DatabaseError){
            }//END of FUNCTION: onCancelled
        })
    }//END of FUNCTION: queryUser

    //START of FUNCTION: queryLog
    private fun queryLog(userKey: String, messageKey: String, classification: String, query: String){
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val interrogative = setOf("what", "whats", "what's", "when", "where", "which", "who", "whom", "whose", "why", "how", "could", "do")
        val list = query.lowercase().split(Regex("[^\\w']+")).map{it.removeSuffix("s")}.filter{it.isNotBlank()}
        val keywords = list.filter{it !in cancel}.toSet()
        val lastKeyword = keywords.lastOrNull() ?: ""
        var answers = mutableListOf<String>()
        var foundAnswer = ""
        var message = ""

        val cleaned = query
            .replace(Regex("(?i)\\b(is|are|am|you|was|were|do|does|did|know)\\b\\s*"), "")
            .replace(Regex("\\bmy\\b", RegexOption.IGNORE_CASE), "your")
            .replace(Regex("\\bI'm\\b", RegexOption.IGNORE_CASE), "you are")
            .replace(Regex("\\bI've\\b", RegexOption.IGNORE_CASE), "you have")
            .replace(Regex("\\bme\\b", RegexOption.IGNORE_CASE), "you")
            .removeSuffix("?")
            .trim()

        messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
            //START of FUNCTION: onDataChange
            override fun onDataChange(messageSnapshot: DataSnapshot){
                var index = 1
                var newLogKey = "message$index"
                var foundLogKey = "message$index"

                //START of WHILE-LOOP:
                while(messageSnapshot.hasChild(newLogKey)){
                    index++
                    newLogKey = "message$index"
                }//END of WHILE-LOOP

                //START of IF-STATEMENT:
                if((query.lowercase().contains(Regex("\\bare\\b"))) || (lastKeyword.endsWith("s") && !lastKeyword.endsWith("ss"))){
                    //START of WHILE-LOOP:
                    while(messageSnapshot.hasChild(foundLogKey)){
                        val foundUserKey = messageSnapshot.child("$foundLogKey/userKey").getValue(String::class.java).toString()

                        //START of IF-STATEMENT:
                        if(foundUserKey.startsWith("User")){
                            val foundMessage = messageSnapshot.child("$foundLogKey/message").getValue(String::class.java).toString()
                            val foundList = foundMessage.lowercase().replace(Regex("[^a-z0-9\\s@]"), "").trim().split(Regex("\\s+"))
                            Log.d("found message", foundMessage)

                            //START of IF-STATEMENT:
                            if(!(foundList.any{it in interrogative} || foundMessage.endsWith("?"))){
                                val normalizedKeywords = keywords.map{it.removeSuffix("s")}.toSet()
                                val normalizedList = foundList.map{it.removeSuffix("s")}.toSet()
                                val foundAnswer = foundList.filter{word -> word.removeSuffix("s") !in normalizedKeywords && word !in cancel}.joinToString(" ")

                                //START of IF-STATEMENT:
                                if(normalizedList.containsAll(keywords)){
                                    Log.d("found answer", foundAnswer)
                                    answers.add(foundAnswer)
                                }//END of IF-STATEMENT
                            }//END of IF-STATEMENT
                        }//END of IF-STATEMENT

                        index--
                        foundLogKey = "message$index"
                    }//END of WHILE-LOOP

                    //START of IF-STATEMENT:
                    if(answers.isNotEmpty()){
                        val joined = when(answers.size){
                            1 -> answers[0]
                            2 -> answers.joinToString(" and ")
                            else -> answers.dropLast(1).joinToString(", ") + ", and " + answers.last()
                        }
                        val pluralKeyword = if (lastKeyword.endsWith("s")) lastKeyword else lastKeyword + "s"
                        message = "$pluralKeyword are $joined.".replaceFirstChar{it.titlecase()}
                        messageReference.child(newLogKey).setValue(Message(aiKey, date, time, message))
                    }//END of IF-STATEMENT

                    //START of ELSE-STATEMENT:
                    else{
                        //START of IF-STATEMENT:
                        if((classification != "ai") && (classification != "user")){
                            message = "I don't know $cleaned are Darling."
                            messageReference.child(newLogKey).setValue(Message(aiKey, date, time, message))
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(classification == "ai"){
                            queryAI(messageKey, query)
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(classification == "user"){
                            queryUser(userKey, messageKey, query)
                        }//END of IF-STATEMENT
                    }//END of ELSE-STATEMENT
                }//END of IF-STATEMENT

                //START of IF-STATEMENT:
                else if(query.lowercase().matches(Regex("^(is|are|was|were|do|does|did)\\b.*", RegexOption.IGNORE_CASE))){
                    //START of WHILE-LOOP:
                    while(messageSnapshot.hasChild(foundLogKey)){
                        val foundUserKey = messageSnapshot.child("$foundLogKey/userKey").getValue(String::class.java).toString()

                        //START of IF-STATEMENT:
                        if(foundUserKey.startsWith("User")){
                            val foundMessage = messageSnapshot.child("$foundLogKey/message").getValue(String::class.java).toString()
                            val foundList = foundMessage.lowercase().replace(Regex("[^a-z0-9\\s@]"), "").trim().split(Regex("\\s+"))
                            Log.d("found message", foundMessage)

                            //START of IF-STATEMENT:
                            if(!(foundList.any{it in interrogative} || foundMessage.endsWith("?"))){
                                val normalizedKeywords = keywords.map{it.removeSuffix("s")}.toSet()

                                //START of IF-STATEMENT:
                                if(foundList.map{it.removeSuffix("s")}.any {it in normalizedKeywords}){
                                    foundAnswer = foundMessage
                                    Log.d("found answer", foundAnswer)
                                    answers = foundList.map{it.removeSuffix("s")}.toMutableList()
                                    break
                                }//END of IF-STATEMENT
                            }//END of IF-STATEMENT
                        }//END of IF-STATEMENT

                        index--
                        foundLogKey = "message$index"
                    }//END of WHILE-LOOP

                    //START of IF-STATEMENT:
                    if(answers.isNotEmpty()){
                        val replaced = cancel.fold(foundAnswer){text, word ->
                            val pattern = Regex("\\b${Regex.escape(word)}\\b", RegexOption.IGNORE_CASE)
                            text.replace(pattern){it.value.lowercase()}
                        }
                            .replace(Regex("\\bmy\\b", RegexOption.IGNORE_CASE), "your")
                            .replace(Regex("\\bI'm\\b", RegexOption.IGNORE_CASE), "you are")
                            .replace(Regex("\\bI've\\b", RegexOption.IGNORE_CASE), "you have")
                            .replace(Regex("\\bme\\b", RegexOption.IGNORE_CASE), "you")

                        //START of IF-STATEMENT:
                        if(answers.containsAll(keywords)){
                            message = "Yes Darling, $replaced.".replaceFirstChar{it.titlecase()}
                        }//END of IF-STATEMENT

                        //START of ELSE-STATEMENT:
                        else{
                            message = "No Darling, $replaced.".replaceFirstChar{it.titlecase()}
                        }//END of IF-STATEMENT

                        messageReference.child(newLogKey).setValue(Message(aiKey, date, time, message))
                    }//END of IF-STATEMENT

                    //START of ELSE-STATEMENT:
                    else{
                        //START of IF-STATEMENT:
                        if((classification != "ai") && (classification != "user")){
                            message = "I don't know $cleaned Darling."
                            messageReference.child(newLogKey).setValue(Message(aiKey, date, time, message))
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(classification == "ai"){
                            queryAI(messageKey, query)
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(classification == "user"){
                            queryUser(userKey, messageKey, query)
                        }//END of IF-STATEMENT
                    }//END of ELSE-STATEMENT
                }//END of IF-STATEMENT

                //START of ELSE-STATEMENT:
                else{
                    //START of WHILE-LOOP:
                    while(messageSnapshot.hasChild(foundLogKey)){
                        val foundUserKey = messageSnapshot.child("$foundLogKey/userKey").getValue(String::class.java).toString()

                        //START of IF-STATEMENT:
                        if(foundUserKey.startsWith("User")){
                            val foundMessage = messageSnapshot.child("$foundLogKey/message").getValue(String::class.java).toString()
                            val foundList = foundMessage.lowercase().replace(Regex("[^a-z0-9\\s@]"), "").trim().split(Regex("\\s+"))
                            Log.d("found message", foundMessage)

                            //START of IF-STATEMENT:
                            if(!(foundList.any{it in interrogative} || foundMessage.endsWith("?"))){
                                val normalizedKeywords = keywords.map{it.removeSuffix("s")}.toSet()
                                val normalizedList = foundList.map{it.removeSuffix("s")}.toSet()

                                //START of IF-STATEMENT:
                                if(normalizedList.containsAll(normalizedKeywords)){
                                    foundAnswer = foundMessage
                                    Log.d("found answer", foundAnswer)
                                    break
                                }//END of IF-STATEMENT
                            }//END of IF-STATEMENT
                        }//END of IF-STATEMENT

                        index--
                        foundLogKey = "message$index"
                    }//END of WHILE-LOOP

                    //START of IF-STATEMENT:
                    if(foundAnswer.isNotBlank()){
                        val replaced = cancel.fold(foundAnswer){text, word ->
                            val pattern = Regex("\\b${Regex.escape(word)}\\b", RegexOption.IGNORE_CASE)
                            text.replace(pattern){it.value.lowercase()}
                        }
                            .replace(Regex("\\bmy\\b", RegexOption.IGNORE_CASE), "your")
                            .replace(Regex("\\bI'm\\b", RegexOption.IGNORE_CASE), "you are")
                            .replace(Regex("\\bI've\\b", RegexOption.IGNORE_CASE), "you have")
                            .replace(Regex("\\bme\\b", RegexOption.IGNORE_CASE), "you")

                        message = "${replaced.replaceFirstChar{it.titlecase()}}, Darling."
                        messageReference.child(newLogKey).setValue(Message(aiKey, date, time, message))
                    }//END of IF-STATEMENT

                    //START of ELSE-STATEMENT:
                    else{
                        //START of IF-STATEMENT:
                        if((classification != "ai") && (classification != "user")){
                            message = "I don't know $cleaned is Darling."
                            messageReference.child(newLogKey).setValue(Message(aiKey, date, time, message))
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(classification == "ai"){
                            queryAI(messageKey, query)
                        }//END of IF-STATEMENT

                        //START of IF-STATEMENT:
                        if(classification == "user"){
                            queryUser(userKey, messageKey, query)
                        }//END of IF-STATEMENT
                    }//END of ELSE-STATEMENT
                }//END of ELSE-STATEMENT
            }//END of FUNCTION: onDataChange

            //START of FUNCTION: onCancelled
            override fun onCancelled(error: DatabaseError){
            }//END of FUNCTION: onCancelled
        })
    }//END of FUNCTION: queryLog
}//END of CLASS: Query