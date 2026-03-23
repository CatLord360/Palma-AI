package com.example.palma.ai.tensor

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.MappedByteBuffer
import com.example.palma.models.Message
import com.google.firebase.Firebase
import com.google.firebase.database.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//START of CLASS: Prompt
class Prompt(private val context: Context) {

    private val database = Firebase.database
    private var interpreter: Interpreter? = null

    init {
        loadModel()
    }

    // 🔥 Load prompt.tflite
    private fun loadModel() {
        val model: MappedByteBuffer = FileUtil.loadMappedFile(context, "prompt.tflite")
        interpreter = Interpreter(model)
    }

    //START of FUNCTION: writePrompt
    fun writePrompt(aiKey: String, messageKey: String, prompt: String) {

        val messageReference = database.getReference("Palma/Message/$messageKey")

        val current = LocalDateTime.now()
        val date = current.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val time = current.format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        // 🔥 Always run model
        val message = runModel(prompt)

        messageReference.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                var index = 1
                var key = "message$index"

                while (snapshot.hasChild(key)) {
                    index++
                    key = "message$index"
                }

                messageReference.child(key)
                    .setValue(Message(aiKey, date, time, message))
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
    //END of FUNCTION: writePrompt

    // 🔥 FEATURE VECTOR (must match Python input: 8 features)
    private fun featureVector(text: String): FloatArray {
        val lower = text.lowercase()
        val tokens = lower.split(" ")

        val greetingWords = listOf("hello", "hi", "hey", "greetings")
        val goodWords = listOf("morning", "afternoon", "evening", "night")
        val gratitudeWords = listOf("thank", "thanks")
        val farewellWords = listOf("bye", "goodbye", "farewell", "later", "see", "care")
        val queryWords = listOf("what", "who", "where", "when", "why", "how", "tell")

        return floatArrayOf(
            tokens.size.toFloat(),                                           // token count
            lower.length.toFloat(),                                          // char count
            if (lower.contains("?")) 1f else 0f,                             // question flag
            if (greetingWords.any { lower.contains(it) }) 1f else 0f,       // greeting flag
            if (lower.contains("good") && goodWords.any { lower.contains(it) }) 1f else 0f, // good words
            if (gratitudeWords.any { lower.contains(it) }) 1f else 0f,      // gratitude flag
            if (farewellWords.any { lower.contains(it) }) 1f else 0f,       // farewell flag
            if (queryWords.any { lower.contains(it) }) 1f else 0f           // query flag
        )
    }

    // 🔥 RUN MODEL SAFELY
    private fun runModel(prompt: String): String {

        // Ensure input is [1,8] as expected by model
        val input = arrayOf(featureVector(prompt))
        val output = Array(1) { FloatArray(5) }  // 🔥 now 5 classes to match Python model

        interpreter?.run(input, output)

        val result = output[0]
        val index = result.indices.maxByOrNull { result[it] } ?: 0

        // 🔥 RESPONSE MAP (5 responses)
        val responses = arrayOf(
            "Hello! How can I help you?",                                                      // 0 greeting
            "Good day! Hope you're doing well.",                                               // 1 good
            "You're welcome!",                                                                 // 2 gratitude
            "Goodbye! See you later!",                                                         // 3 farewell
            "I am your AI assistant. I can help with tasks and answer simple questions."      // 4 query
        )

        return responses[index]
    }
}
//END of CLASS: Prompt