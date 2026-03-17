package com.example.palma.ai

import android.util.Log
import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

//START of CLASS: Classification
class Classification(context: Context){
    private val interpreter: Interpreter by lazy{
        loadTFLiteModel(context, "context_classifier.tflite")
    }
    private var classification = ""
    private var type = ""

    //START of FUNCTION: classifyContext
    fun classifyContext(prompt: String): String{

        // Handle empty/null input safely
        if (prompt.isBlank()) {
            Log.e("TF", "Prompt is empty")
            return "query"
        }

        val list = Regex("[A-Za-z]+|\\d+|[-.,!?;:]")
            .findAll(prompt)
            .map { it.value }
            .toList()

        val countToken = list.size
        val countCharacter = prompt.length

        Log.d("token count", countToken.toString())
        Log.d("character count", countCharacter.toString())

        // ✅ FIX 1: Ensure correct shape [1,2]
        val inputFeatures = arrayOf(
            floatArrayOf(
                countToken.toFloat(),
                countCharacter.toFloat()
            )
        )

        // ✅ Output shape [1,4]
        val output = Array(1) { FloatArray(4) }

        try {
            interpreter.run(inputFeatures, output)
        } catch (e: Exception) {
            Log.e("TF", "Interpreter run failed: ${e.message}")
            return "query"
        }

        val prediction = output[0].indices.maxByOrNull { output[0][it] } ?: 0

        this.classification = when (prediction) {
            0 -> "command"
            1 -> "etiquette"
            2 -> "query"
            3 -> "forecast"
            else -> "query"
        }

        Log.d("found classification", this.classification)
        return classification
    }//END of FUNCTION: classifyContext

    fun loadTFLiteModel(context: Context, fileName: String): Interpreter{
        val fileDescriptor = context.assets.openFd(fileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel

        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength

        val buffer: MappedByteBuffer =
            fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

        inputStream.close() // ✅ prevent leaks

        val options = Interpreter.Options().apply {
            setNumThreads(2) // optional optimization
        }

        return Interpreter(buffer, options)
    }

    //START of FUNCTION: classifyCommand
    fun classifyCommand(prompt: String): String{

        Log.d("found classification", classification)
        return classification
    }//END of FUNCTION: classifyCommand

    //START of FUNCTION: typeList
    fun typeList(prompt: String): String{

        Log.d("found type", type)
        return type
    }//END of FUNCTION: typeList

    //START of FUNCTION: typeReminder
    fun typeReminder(prompt: String): String{

        Log.d("found type", type)
        return type
    }//END of FUNCTION: typeReminder

    //START of FUNCTION: typeContact
    fun typeContact(prompt: String): String{

        Log.d("found type", type)
        return type
    }//END of FUNCTION: typeContact

    //START of FUNCTION: typeEtiquette
    fun typeEtiquette(prompt: String): String{

        Log.d("found type", type)
        return type
    }//END of FUNCTION: typeEtiquette

    //START of FUNCTION: typeQuery
    fun typeQuery(prompt: String): String{

        Log.d("found type", type)
        return type
    }//END of FUNCTION: typeQuery

    //START of FUNCTION: typeForecast
    fun typeForecast(prompt: String): String{

        Log.d("found type", type)
        return type
    }//END of FUNCTION: typeForecast
}//END of CLASS: Classification