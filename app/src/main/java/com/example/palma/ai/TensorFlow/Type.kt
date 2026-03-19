package com.example.palma.ai.TensorFlow

import android.util.Log
import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

//START of CLASS: Type
class Type(context: Context){
    private val typeQueryInterpreter: Interpreter by lazy{
        interpretQuery(context, "query_type.tflite")
    }
    private val typeForecastInterpreter: Interpreter by lazy{
        interpretForecast(context, "forecast_type.tflite")
    }
    private val forecastTokenizerMap: Map<String, Int> = mapOf(
        "<OOV>" to 1,
        "what" to 2,
        "is" to 3,
        "the" to 4,
        "weather" to 5,
        "now" to 6,
        "did" to 7,
        "it" to 8,
        "rain" to 9,
        "yesterday" to 10,
        "tomorrow" to 11
    )
    private var type = ""

    // START of FUNCTION: typeQuery
    fun typeQuery(prompt: String): String{

        //START of IF-STATEMENT:
        if(prompt.isBlank()){
            Log.e("TF", "Prompt is empty")
            return "log"
        }//END of IF-STATEMENT

        val maxLength = 12
        val numClasses = 3

        val tokens = prompt.lowercase().split(" ").map{it.trim()}
        val floatSequence = FloatArray(maxLength){0f}

        //START of FOR-LOOP:
        for(i in tokens.indices){
            if(i >= maxLength) break
            floatSequence[i] = (tokens[i].hashCode() % 1500).toFloat()
            if(floatSequence[i] < 0) floatSequence[i] += 1500f
        }//END of FOR-LOOP

        val inputFeatures = arrayOf(floatSequence)
        val output = Array(1){FloatArray(numClasses)}

        //START of TRY:
        try{
            typeQueryInterpreter.run(inputFeatures, output)
        }//END of TRY

        //START of CATCH:
        catch(e: Exception){
            Log.e("TF", "Interpreter run failed: ${e.message}")
            return "log"
        }//END of CATCH

        val prediction = output[0].indices.maxByOrNull{output[0][it]} ?: 0

        this.type = when(prediction){
            0 -> "user"
            1 -> "ai"
            2 -> "log"
            else -> "log"
        }

        Log.d("found type", "${this.type} (confidence: ${output[0][prediction]})")
        return this.type
    }// END of FUNCTION: typeQuery

    //START of FUNCTION: interpretQuery
    private fun interpretQuery(context: Context, fileName: String): Interpreter{
        val fileDescriptor = context.assets.openFd(fileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val buffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

        inputStream.close()

        val options = Interpreter.Options().apply{setNumThreads(2)}
        return Interpreter(buffer, options)
    }//END of FUNCTION: interpretQuery

    // START of FUNCTION: typeForecast
    fun typeForecast(prompt: String): String{

        //START of IF-STATEMENT:
        if(prompt.isBlank()){
            Log.e("TF", "Forecast prompt is empty")
            return "current"
        }//END of IF-STATEMENT

        val maxLength = 12
        val numClasses = 3
        val tokens = prompt.lowercase().split(" ").map{it.trim()}
        val floatSequence = FloatArray(maxLength){0f}

        //START of FOR-LOOP:
        for(i in tokens.indices){
            if(i >= maxLength) break

            floatSequence[i] = forecastTokenizerMap.getOrDefault(tokens[i], forecastTokenizerMap["<OOV>"] ?: 1).toFloat()
        }//END of FOR-LOOP

        val input = arrayOf(floatSequence)
        val output = Array(1){FloatArray(numClasses)}

        //START of TRY:
        try{
            typeForecastInterpreter.run(input, output)
        }//END of TRY

        //START of CATCH:
        catch(e: Exception){
            Log.e("TF", "Forecast interpreter failed: ${e.message}")
            return "current"
        }//END of CATCH

        val prediction = output[0].indices.maxByOrNull { output[0][it] } ?: 1
        val confidence = output[0][prediction]

        this.type = when(prediction){
            0 -> "past"
            1 -> "current"
            2 -> "future"
            else -> "none"
        }

        Log.d("forecast type", "${this.type} (confidence: $confidence)")
        return this.type
    }// END of FUNCTION: typeForecast

    //START of FUNCTION: interpretForecast
    private fun interpretForecast(context: Context, fileName: String): Interpreter{
        val fileDescriptor = context.assets.openFd(fileName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val buffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

        inputStream.close()

        val options = Interpreter.Options().apply{setNumThreads(2)}

        return Interpreter(buffer, options)
    }//END of FUNCTION: interpretForecast
}//END of CLASS: Type