package com.example.palma.ai

import android.content.Context
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * ModelInference: Handles TFLite model loading and inference execution.
 * The agent's response is stored in: val response = tokenizer.decode(outputTokens.toIntArray())
 */
class ModelInference(private val context: Context) {
    private var interpreter: Interpreter? = null
    private lateinit var tokenizer: SentencePieceTokenizer
    private val vocabSize = 8000
    private val maxSeqLength = 256
    
    init {
        loadModel(context)
        tokenizer = SentencePieceTokenizer(context)
    }
    
    /**
     * Load the TFLite model from assets
     */
    private fun loadModel(context: Context) {
        try {
            val modelBuffer = FileUtil.loadMappedFile(context, "model_quantized.tflite")
            val options = Interpreter.Options()
            options.setNumThreads(4)  // Adjust based on device cores
            
            // Uncomment for GPU acceleration (requires GPU delegate dependency)
            // val gpuDelegate = GpuDelegate()
            // options.addDelegate(gpuDelegate)
            
            interpreter = Interpreter(modelBuffer, options)
            println("✓ Model loaded successfully")
        } catch (e: Exception) {
            throw RuntimeException("Failed to load model: ${e.message}", e)
        }
    }
    
    /**
     * Generate a response using the model.
     * 
     * ============================================
     * AGENT RESPONSE LOCATION:
     * The response is stored in the val response variable.
     * This can be retrieved and used in your Activity like:
     * 
     *     onSuccess(response)  // Pass to callback
     *     updateUI(response)   // Update UI elements
     *     sendToServer(response) // Send to backend
     * ============================================
     */
    fun generateResponse(
        prompt: String,
        maxTokens: Int = 50,
        temperature: Float = 0.8f,
        topK: Int = 40
    ): String {
        if (interpreter == null) {
            throw RuntimeException("Model not loaded")
        }
        
        // Tokenize input
        val inputTokens = tokenizer.tokenize(prompt)
        if (inputTokens.isEmpty()) {
            throw IllegalArgumentException("Prompt tokenization resulted in empty sequence")
        }
        
        val outputTokens = mutableListOf<Int>()
        var currentSequence = inputTokens.toMutableList()
        
        // Generate tokens iteratively
        for (step in 0 until maxTokens) {
            // Prepare input: take the last token
            val nextInputToken = currentSequence.last()
            val inputBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder())
            inputBuffer.putInt(nextInputToken)
            inputBuffer.rewind()
            
            // Prepare output buffer (8000 logits)
            val outputBuffer = ByteBuffer.allocateDirect(vocabSize * 4)
                .order(ByteOrder.nativeOrder())
            
            // Run inference
            try {
                interpreter!!.run(inputBuffer, outputBuffer)
            } catch (e: Exception) {
                throw RuntimeException("Inference failed at step $step: ${e.message}", e)
            }
            
            // Process logits
            outputBuffer.rewind()
            val logits = FloatArray(vocabSize)
            outputBuffer.asFloatBuffer().get(logits)
            
            // Apply temperature
            val scaledLogits = logits.map { it / temperature }.toFloatArray()
            
            // Apply softmax for probabilities
            val expLogits = scaledLogits.map { kotlin.math.exp(it) }.toFloatArray()
            val sumExp = expLogits.sum()
            val probabilities = expLogits.map { it / sumExp }.toFloatArray()
            
            // Sample top-k token (or use greedy if k >= vocab size)
            val nextToken = if (topK >= vocabSize) {
                // Greedy: take highest probability
                probabilities.indices.maxByOrNull { probabilities[it] } ?: 1
            } else {
                // Top-k sampling
                val topKIndices = probabilities.withIndex()
                    .sortedByDescending { it.value }
                    .take(topK)
                    .map { it.index }
                
                val topKProbs = topKIndices.map { probabilities[it] }.toFloatArray()
                val sum = topKProbs.sum()
                val normalizedProbs = topKProbs.map { it / sum }.toFloatArray()
                
                // Sample from top-k
                val random = kotlin.math.random.Random()
                var r = random.nextFloat()
                var selected = topKIndices[0]
                for (i in normalizedProbs.indices) {
                    r -= normalizedProbs[i]
                    if (r <= 0) {
                        selected = topKIndices[i]
                        break
                    }
                }
                selected
            }
            
            outputTokens.add(nextToken)
            currentSequence.add(nextToken)
            
            // Stop if end token reached
            if (nextToken == 0 || nextToken == 1) break
        }
        
        // ============================================
        // IMPORTANT: This is where the agent response is stored
        // You can use this variable to:
        // - Store in a database
        // - Send to a server
        // - Display in UI
        // - Process further
        val response = tokenizer.decode(outputTokens.toIntArray())
        // ============================================
        
        return response
    }
    
    /**
     * Clean up resources
     */
    fun release() {
        interpreter?.close()
        interpreter = null
    }
}
