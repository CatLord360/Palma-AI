package com.example.palma.ai.tom

import com.example.palma.models.Message
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.pow

//START of CLASS: Compute
class Compute{
    private val database = Firebase.database
    private val aiKey = "AI - 2"

    //START of FUNCTION: writeCompute
    fun writeCompute(userKey: String, messageKey: String, message: String){
        val list = message.lowercase().trim().split(" ")

        //START of IF-STATEMENT:
        if(list[1] == "sum"){
            computeSum(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "difference"){
            computeDifference(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "product"){
            computeProduct(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "quotient"){
            computeQuotient(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "modulus"){
            computeModulus(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "average"){
            computeAverage(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "square"){
            computeSquare(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "cube"){
            computeCube(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "factorial"){
            computeFactorial(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "gcd"){
            computeGCD(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "lcm"){
            computeLCM(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "percentage"){
            computePercentage(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "absolute"){
            computeAbsolute(userKey, messageKey, message)
        }//END of IF-STATEMENT

        //START of IF-STATEMENT:
        if(list[1] == "round"){
            computeRound(userKey, messageKey, message)
        }//END of IF-STATEMENT
    }//END of FUNCTION: writeCompute

    //START of FUNCTION: computeSum
    private fun computeSum(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val sum = list.sum()

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The sum of ${list.joinToString(" ")} is $sum..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeSum

    //START of FUNCTION: computeDifference
    private fun computeDifference(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val difference = list.drop(1).fold(list.first()) { acc, num -> acc - num }

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The difference of ${list.joinToString(" ")} is $difference..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeDifference

    //START of FUNCTION: computeProduct
    private fun computeProduct(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val product = list.drop(1).fold(list.first()) { acc, num -> acc * num }

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The product of ${list.joinToString(" ")} is $product..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeProduct

    //START of FUNCTION: computeQuotient
    private fun computeQuotient(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val quotient = list[0] / list[1]

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The quotient of ${list.joinToString(" ")} is $quotient..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeQuotient

    //START of FUNCTION: computeModulus
    private fun computeModulus(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val modulus = list[0] % list[1]

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The modulus of ${list.joinToString(" ")} is $modulus..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeModulus

    //START of FUNCTION: computeAverage
    private fun computeAverage(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val average = list.sum() / list.size

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The average of ${list.joinToString(" ")} is $average..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeAverage

    //START of FUNCTION: computeSquare
    private fun computeSquare(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val square = list[0].pow(2)

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The square of ${list.joinToString(" ")} is $square..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeSquare

    //START of FUNCTION: computeCube
    private fun computeCube(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val cube = list[0].pow(3)

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The cube of ${list.joinToString(" ")} is $cube..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeCube

    //START of FUNCTION: computeFactorial
    private fun computeFactorial(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toIntOrNull() }

                    //START of FUNCTION: factorial
                    fun factorial(n: Int): Long{
                        var result = 1L

                        //START of FOR-LOOP:
                        for(i in 2..n){
                            result *= i
                        }//END of FOR-LOOP

                        return result
                    }//END of FUNCTION: factorial

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The factorial of ${list.joinToString(" ")} is ${factorial(list[0])}..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeFactorial

    //START of FUNCTION: computeGCD
    private fun computeGCD(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toIntOrNull() }

                    //START of FUNCTION: gcd
                    fun gcd(a: Int, b: Int): Int {
                        var x = a
                        var y = b

                        //START of WHILE-LOOP:
                        while(y != 0) {
                            val temp = y
                            y = x % y
                            x = temp
                        }//END of WHILE-LOOP

                        return x
                    }//END of FUNCTION: gcd

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The gcd of ${list.joinToString(" ")} is ${list.reduce { acc, num -> gcd(acc, num)}}..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeGCD

    //START of FUNCTION: computeLCM
    private fun computeLCM(userKey: String, messageKey: String, message: String) {
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        //START of FUNCTION: gcd
        fun gcd(a: Int, b: Int): Int {
            var x = a
            var y = b

            //START of WHILE-LOOP:
            while (y != 0) {
                val temp = y
                y = x % y
                x = temp
            }//END of WHILE-LOOP

            return x
        }//END of FUNCTION: gcd

        //START of FUNCTION: lcm
        fun lcm(a: Int, b: Int): Int {
            return kotlin.math.abs(a * b) / gcd(a, b)
        }//END of FUNCTION: lcm

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object : ValueEventListener {

                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot) {
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull { it.toIntOrNull() }

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The lcm of ${list.joinToString(" ")} is ${list.reduce { acc, num -> lcm(acc, num) }}..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeLCM

    //START of FUNCTION: computePercentage
    private fun computePercentage(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val percentage = (list[1] / list[0]) * 100

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The percentage of ${list.joinToString(" ")} is $percentage%..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computePercentage

    //START of FUNCTION: computeAbsolute
    private fun computeAbsolute(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val absolute = kotlin.math.abs(list[0])

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The absolute of ${list.joinToString(" ")} is $absolute..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeAbsolute

    //START of FUNCTION: computeRound
    private fun computeRound(userKey: String, messageKey: String, message: String){
        val userReference = database.getReference("Palma/User/$userKey/Personal Information")
        val messageReference = database.getReference("Palma/Message/$messageKey")
        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        userReference.get().addOnSuccessListener{ snapshot ->
            messageReference.addListenerForSingleValueEvent(object: ValueEventListener{
                //START of FUNCTION: onDataChange
                override fun onDataChange(snapshot: DataSnapshot){
                    val index = snapshot.childrenCount.toInt() + 1
                    val key = "message$index"

                    val list = message.substringAfter("of", "").trim().split(" ").mapNotNull{ it.toDoubleOrNull() }
                    val round = kotlin.math.round(list[0])

                    messageReference.child(key).setValue(Message(aiKey, date, time, "The round of ${list.joinToString(" ")} is $round..."))
                }//END of FUNCTION: onDataChange

                //START of FUNCTION: onCancelled
                override fun onCancelled(error: DatabaseError){
                }//END of FUNCTION: onCancelled
            })
        }
    }//END of FUNCTION: computeRound
}//END of CLASS: Compute