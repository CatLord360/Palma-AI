package com.example.palma.ai.mid

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
    private val aiKey = "AI - 4"

    //START of FUNCTION: writeQuery
    fun writeQuery(userKey: String, messageKey: String, message: String){
        val stopWords = setOf("is","am","are","was","were","do","did","does","my","the","a","an","of", "in","on","for","to","give","whats","what's","i","me","could","would")
        val coreInterrogative = setOf("what","who","whom","whose","which","when","where","why","how")
        val auxiliaryInterrogative = setOf("is","am","are","was","were", "do","does","did", "can","could","will","would", "should","shall","may","might", "have","has","had")
        val ai = setOf("you", "your", "you're")
        val userDataFields = setOf("username","gender","birthdate","birthday", "mobile","email","contact","name","number")

        val cleanedMessage = message.lowercase().replace(Regex("[^a-z0-9\\s@]"), "").trim()
        val list = cleanedMessage.split(Regex("\\s+"))
        val coreIndex = list.indexOfFirst { it in coreInterrogative }

        val startQuery = if(coreIndex != -1){coreIndex}
        else{list.indexOfFirst{it in auxiliaryInterrogative}}

        val query = if(startQuery != -1){list.subList(startQuery, list.size).joinToString(" ")}
        else{cleanedMessage}

        val keywords = list.filter{it.isNotBlank() && it !in stopWords}
        val isAiQuery = keywords.any{it in ai}
        val isUserQuery = keywords.any{it in userDataFields}

        //START of IF-STATEMENT:
        if(isAiQuery){
            queryAI(messageKey, query)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(isUserQuery){
            queryUser(userKey, messageKey, query)
        }//END of IF-STATEMENT

        //START of ELSE-STATEMENT:
        else{
            queryMessage(userKey, messageKey, query)}//END of ELSE-STATEMENT
    }//END of FUNCTION: writeQuery

    //START of FUNCTION: queryAI
    private fun queryAI(messageKey: String, message: String){
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val username = "Mid"
        val gender = "male"
        val mobile = "44444"
        val email = "mid@ai.com"
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
            "My fucking $phrase is $matchedData...".replaceFirstChar { it.uppercase() }
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
    private fun queryUser(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val queryMessage = message.lowercase().replace(Regex("[^a-z0-9\\s@]"), "").trim()
        val words = queryMessage.split(Regex("[^\\w']+"))

        val selfIndicators = setOf("my", "me", "mine", "i")
        val otherIndicators = setOf("their", "his", "her", "someone", "other", "they", "them")

        val isSelfQuery = words.any { it in selfIndicators }
        val isOtherQuery = !isSelfQuery && (
                words.any { it in otherIndicators } ||
                        queryMessage.contains(Regex("of\\s+[a-z0-9]+"))
                )

        val queryType = when {
            isSelfQuery -> "self"
            isOtherQuery -> "other"
            else -> "unknown"
        }

        //START of IF-STATEMENT:
        if(queryType == "self"){
            userReference.addListenerForSingleValueEvent(object : ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val username = snapshot.child("username").getValue(String::class.java)
                    val gender = snapshot.child("gender").getValue(String::class.java)
                    val birthdate = snapshot.child("birthdate").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)
                    val mobile = snapshot.child("mobile").getValue(String::class.java)

                    val fieldMap = mapOf(
                        "username" to Pair(setOf("username", "user", "name"), username),
                        "gender" to Pair(setOf("gender", "sex"), gender),
                        "birthdate" to Pair(setOf("birthdate", "birthday", "birth"), birthdate),
                        "email" to Pair(setOf("email", "mail"), email),
                        "mobile" to Pair(setOf("mobile", "phone", "number", "contact"), mobile)
                    )

                    val parts = mutableListOf<String>()

                    //START of FOR-LOOP:
                    for((label, pair) in fieldMap){
                        val (keywords, value) = pair

                        //START of IF-STATEMENT:
                        if(words.any { it in keywords }){
                            parts.add("$label is $value...")
                        }//END of IF-STATEMENT
                    }//END of FOR-LOOP

                    val responseText = if (parts.isNotEmpty()) {
                        val joined = when (parts.size) {
                            1 -> parts[0]
                            2 -> parts.joinToString(" and ")
                            else -> parts.dropLast(1).joinToString(", ") + ", and " + parts.last() + "..."
                        }
                        "Your $joined."
                    } else {
                        "I couldn't determine which specific information you're asking about, but your username is $username, gender is $gender, and birthdate is $birthdate."
                    }

                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                        //START of FUNCTION: onDataChange
                        override fun onDataChange(messageSnapshot: DataSnapshot){
                            var index = 1
                            var key = "message$index"

                            //START of WHILE-LOOP:
                            while(messageSnapshot.hasChild(key)){
                                index++
                                key = "message$index"
                            }//END of WHILE-LOOP

                            val responseMessage = Message(aiKey, date, time, responseText)
                            messageReference.child(key).setValue(responseMessage)
                        }//END of FUNCTION: onDataChange

                        //START of FUNCTION: onCancelled
                        override fun onCancelled(error: DatabaseError){
                        }//END of FUNCTION: onCancelled
                    })
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(queryType == "other"){
            val otherReference = database.getReference("Palma/User")
            val dataKeywords = mapOf(
                "username" to setOf("username", "user", "name"),
                "gender" to setOf("gender", "sex"),
                "birthdate" to setOf("birthdate", "birthday", "birth"),
                "email" to setOf("email", "mail"),
                "mobile" to setOf("mobile", "phone", "number", "contact")
            )

            val queryWords = words.toSet()

            val foundData = mutableListOf<String>()

            //START of FOR-LOOP:
            for((field, keywords) in dataKeywords){
                //START of IF-STATEMENT:
                if(queryWords.any { it in keywords }){
                    foundData.add(field)
                }//END of IF-STATEMENT
            }//END of FOR-LOOP

            val knownWords = dataKeywords.values.flatten().toSet() + setOf(
                "what", "whats", "what's", "is", "the", "of", "and", "their", "his", "her", "someone", "other", "they", "them",
                "when", "where", "how", "who", "whose", "which", "do", "does", "did", "a", "an", "to", "for", "about"
            )

            val clues = queryWords.filterNot { it in knownWords }

            otherReference.addListenerForSingleValueEvent(object : ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    var foundUserKey: String? = null
                    var foundUserData: Map<String, String>? = null
                    var foundUsername = "unknown"

                    //START of FOR-LOOP
                    loop@ for(userSnapshot in snapshot.children){
                        val personalInfo = userSnapshot.child("Personal Information")
                        val usernameVal = personalInfo.child("username").getValue(String::class.java).toString()
                        val emailVal = personalInfo.child("email").getValue(String::class.java).toString()
                        val mobileVal = personalInfo.child("mobile").getValue(String::class.java).toString()

                        //START of IF-STATEMENT:
                        if(clues.any { clue ->
                                usernameVal.lowercase().contains(clue.lowercase()) ||
                                        emailVal.lowercase().contains(clue.lowercase()) ||
                                        mobileVal.lowercase().contains(clue.lowercase())
                            }){
                            foundUserKey = userSnapshot.key
                            foundUsername = personalInfo.child("username").getValue(String::class.java).toString()
                            foundUserData = mapOf(
                                "username" to foundUsername,
                                "gender" to (personalInfo.child("gender").getValue(String::class.java).toString()),
                                "birthdate" to (personalInfo.child("birthdate").getValue(String::class.java).toString()),
                                "email" to (personalInfo.child("email").getValue(String::class.java).toString()),
                                "mobile" to (personalInfo.child("mobile").getValue(String::class.java).toString())
                            )
                            break@loop
                        }//END of IF-STATEMENT
                    }//END of FOR-LOOP

                    val responseText = if (foundUserData != null && foundData.isNotEmpty()) {
                        val parts = foundData.map { field ->
                            "$field is ${foundUserData[field]}"
                        }

                        val joinedParts = when (parts.size) {
                            1 -> parts[0]
                            2 -> parts.joinToString(" and ")
                            else -> parts.dropLast(1).joinToString(", ") + ", and " + parts.last()
                        }

                        "$foundUsername's $joinedParts."
                    } else if (foundUserData != null) {
                        "${foundUsername}'s username is ${foundUserData["username"]}, gender is ${foundUserData["gender"]}, and birthdate is ${foundUserData["birthdate"]}."
                    } else {
                        "I couldn't find anyone matching the clues: ${clues.joinToString(", ")}."
                    }

                    messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
                        //START of FUNCTION: onDataChange
                        override fun onDataChange(messageSnapshot: DataSnapshot){
                            var index = 1
                            var key = "message$index"

                            //START of WHILE-LOOP:
                            while(messageSnapshot.hasChild(key)){
                                index++
                                key = "message$index"
                            }//END of WHILE-LOOP

                            val responseMessage = Message(aiKey, date, time, responseText)
                            messageReference.child(key).setValue(responseMessage)
                        }//END of FUNCTION: onDataChange

                        //START of FUNCTION: onCancelled
                        override fun onCancelled(error: DatabaseError){
                        }//END of FUNCTION: onCancelled
                    })
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }//END of IF-STATEMENT
    }//END of FUNCTION: queryUser

    //START of FUNCTION: queryMessage
    private fun queryMessage(userKey: String, messageKey: String, message: String){
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        val stopWords = setOf(
            "what", "whats", "what's", "when", "where", "which", "who", "whom", "whose", "why", "how", "could", "would",
            "is", "are", "am", "was", "were", "do", "does", "did",
            "the", "a", "an", "and", "or", "of", "to", "for", "in", "on", "at",
            "my", "your", "his", "her", "their", "our", "someone", "something",
            "other", "they", "them", "that", "this", "these", "those", "give"
        )

        val lowerMessage = message.lowercase()
        val questionWords = setOf("what", "whats", "what's", "when", "where", "which", "who", "whom", "whose", "why", "how", "could")
        val isQuestion = lowerMessage.trim().endsWith("?") ||
                lowerMessage.split(Regex("[^\\w']+")).any { it in questionWords }

        if (!isQuestion) return

        val messageWords = lowerMessage.split(Regex("[^\\w']+")).filter { it.isNotBlank() }
        val keywords = messageWords.filter { it !in stopWords }.toSet()

        val lastKeyword = keywords.lastOrNull() ?: ""
        val isPluralQuery = lowerMessage.contains(Regex("\\bare\\b")) || (lastKeyword.endsWith("s") && !lastKeyword.endsWith("ss"))
        val isYesNoQuery = lowerMessage.matches(Regex("^(is|are|was|were|do|does|did)\\b.*", RegexOption.IGNORE_CASE))

        messageReference.addListenerForSingleValueEvent(object : ValueEventListener{
            //START of FUNCTION: onDataChange
            override fun onDataChange(snapshot: DataSnapshot){
                val messagesList = snapshot.children.mapNotNull { child ->
                    val msg = child.getValue(Message::class.java)
                    if (msg != null) Pair(child.key ?: "", msg) else null
                }.sortedBy {
                    it.first.removePrefix("message").toIntOrNull() ?: Int.MAX_VALUE
                }

                val matchedAnswers = mutableListOf<String>()
                var relatedAnswer: String? = null
                var hasKeywordContext = false

                //START of FOR-LOOP:
                for((_, prevMsg) in messagesList.asReversed()){
                    val prevText = prevMsg.message?.lowercase() ?: ""
                    val prevUser = prevMsg.userKey
                    val prevIsQuestion = prevText.trim().endsWith("?") ||
                            prevText.split(Regex("\\W+")).any { it in questionWords }

                    //START of IF-STATEMENT:
                    if(prevUser == userKey && !prevIsQuestion){
                        val prevWords = prevText.split(Regex("\\W+")).toSet()

                        val allKeywordsMatch = keywords.all { keyword ->
                            keyword in prevWords ||
                                    (keyword.endsWith("s") && keyword.removeSuffix("s") in prevWords) ||
                                    (keyword + "s" in prevWords)
                        }

                        val partialKeywordMatch = keywords.any { keyword ->
                            keyword in prevWords ||
                                    (keyword.endsWith("s") && keyword.removeSuffix("s") in prevWords) ||
                                    (keyword + "s" in prevWords)
                        }

                        //START of IF-STATEMENT:
                        if(allKeywordsMatch){
                            matchedAnswers.add(prevMsg.message ?: "")
                        }//END of IF-STATEMENT

                        //START of ELSE-IF STATEMENT:
                        else if (partialKeywordMatch){
                            hasKeywordContext = true
                            relatedAnswer = prevMsg.message
                        }//END of ELSE-IF STATEMENT
                    }//END of IF-STATEMENT
                }//END of FOR-LOOP

                val responseText = if (matchedAnswers.isNotEmpty()){
                    val replaced = matchedAnswers[0]
                        .replace(Regex("\\bmy\\b", RegexOption.IGNORE_CASE), "your")
                        .replace(Regex("\\bI'm\\b", RegexOption.IGNORE_CASE), "you are")
                        .replace(Regex("\\bI've\\b", RegexOption.IGNORE_CASE), "you have")
                        .replace(Regex("\\bme\\b", RegexOption.IGNORE_CASE), "you")

                    //START of IF-STATEMENT:
                    if(isYesNoQuery){
                        "Fuck yeah, $replaced".replaceFirstChar { it.titlecase() }
                    }//END of IF-STATEMENT

                    //START of ELSE-IF STATEMENT:
                    else if(isPluralQuery){
                        val extractedAnswers = matchedAnswers.mapNotNull { ans ->
                            val words = ans.lowercase().split(Regex("[^\\w']+")).filter { it.isNotBlank() && it !in stopWords }
                            val keyword = lastKeyword.lowercase().removeSuffix("s")

                            val indexOfIs = words.indexOf("is")
                            val candidate = if (indexOfIs > 0) words[indexOfIs - 1] else null

                            val chosen = when {
                                candidate != null && candidate != keyword -> candidate
                                else -> words.find { it != keyword }
                            }

                            chosen
                        }.distinct()

                        val joined = when (extractedAnswers.size){
                            0 -> "unknown"
                            1 -> extractedAnswers[0]
                            2 -> extractedAnswers.joinToString(" and ")
                            else -> extractedAnswers.dropLast(1).joinToString(", ") + ", and " + extractedAnswers.last()
                        }

                        val pluralKeyword = if (lastKeyword.endsWith("s")) lastKeyword else lastKeyword + "s"

                        //START of IF-STATEMENT:
                        if("favorite" in keywords){
                            "Your favorite $pluralKeyword are $joined".replaceFirstChar { it.titlecase() }
                        }//END of IF-STATEMENT

                        //START of ELSE-STATEMENT:
                        else{
                            "$pluralKeyword are $joined".replaceFirstChar { it.titlecase() }
                        }//END of ELSE-STATEMENT
                    }//END of ELSE-IF STATEMENT

                    //START of ELSE-STATEMENT
                    else{
                        replaced.replaceFirstChar { it.titlecase() }
                    }//END of ELSE-STATEMENT
                }

                //START of ELSE-IF STATEMENT:
                else if(isYesNoQuery && hasKeywordContext && relatedAnswer != null){
                    val replaced = relatedAnswer
                        .replace(Regex("\\bmy\\b", RegexOption.IGNORE_CASE), "your")
                        .replace(Regex("\\bI'm\\b", RegexOption.IGNORE_CASE), "you are")
                        .replace(Regex("\\bI've\\b", RegexOption.IGNORE_CASE), "you have")
                        .replace(Regex("\\bme\\b", RegexOption.IGNORE_CASE), "you")

                    "Fuck No, $replaced".replaceFirstChar { it.titlecase() }
                }//END of ELSE-IF STATEMENT

                //START of ELSE-STATEMENT:
                else{
                    val cleaned = message
                        .replace(Regex("(?i)\\b(what|is|are|am|was|were|do|does|did)\\b\\s*"), "")
                        .replace(Regex("\\bmy\\b", RegexOption.IGNORE_CASE), "your")
                        .replace(Regex("\\bI'm\\b", RegexOption.IGNORE_CASE), "you are")
                        .replace(Regex("\\bI've\\b", RegexOption.IGNORE_CASE), "you have")
                        .replace(Regex("\\bme\\b", RegexOption.IGNORE_CASE), "you")
                        .removeSuffix("?")
                        .trim()

                    //START of IF-STATEMENT:
                    if(isYesNoQuery){
                        "I don't fucking know if $cleaned"
                    }//END of IF-STATEMENT

                    //START of ELSE-IF STATEMENT:
                    else if(isPluralQuery){
                        "I don't fucking know what $cleaned are"
                    }//END of ELSE-IF STATEMENT

                    //START of ELSE-STATEMENT:
                    else{
                        "I don't fucking know what $cleaned is"
                    }//END of ELSE-STATEMENT
                }//END of ELSE-STATEMENT

                var newIndex = 1
                var newKey = "message$newIndex"

                //START of WHILE-LOOP:
                while(snapshot.hasChild(newKey)){
                    newIndex++
                    newKey = "message$newIndex"
                }//END of WHILE-LOOP

                val responseMessage = Message(userKey = aiKey, date = date, time = time, message = responseText)
                messageReference.child(newKey).setValue(responseMessage)
            }//END of FUNCTION: onDataChange

            //START of FUNCTION: onCancelled
            override fun onCancelled(error: DatabaseError){
            }//END of FUNCTION: onCancelled
        })
    }//END of FUNCTION: queryMessage
}//END of CLASS: Query