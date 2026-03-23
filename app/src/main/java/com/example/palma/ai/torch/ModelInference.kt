package com.example.palma.ai.torch

import android.content.Context
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import kotlin.math.exp
import kotlin.random.Random

/** Handles TFLite model loading and text generation. */
class ModelInference(val context: Context) {
    private val tag = "ModelInference"
    private var interpreter: Interpreter? = null
    private lateinit var tokenizer: SentencePieceTokenizer
    private val padTokenId = 0
    private val bosTokenId = 1
    private val eosTokenId = 2
    private val unkTokenId = 3
    private val startOfTextTokenId = 4
    private val separatorTokenId = 5
    private val endOfTextTokenId = 6
    private val minTokensBeforeEos = 3
    private var vocabSize = 8000
    private var inputTensorIndex = 0
    private var outputTensorIndex = 0
    private var expectsInt64Input = false
    private var loadedModelName = ""

    init {
        loadModel(context)
        tokenizer = SentencePieceTokenizer(context)
    }

    /** Loads the TFLite model from assets and reads tensor metadata. */
    private fun loadModel(context: Context) {
        Log.d("ModelInference", "loadModel")
        val modelCandidates = listOf("model_quantized.tflite", "model_float32.tflite")
        val errors = mutableListOf<String>()

        for (modelName in modelCandidates) {
            try {
                val modelBuffer = FileUtil.loadMappedFile(context, modelName)
                val options = Interpreter.Options()
                options.setNumThreads(4)

                // Optional GPU delegate.
                // val gpuDelegate = GpuDelegate()
                // options.addDelegate(gpuDelegate)

                val created = Interpreter(modelBuffer, options)

                // Read tensor info from Android TFLite tensor APIs.
                // Some runtimes expose getInputDetails/getOutputDetails, but getInputTensor/getOutputTensor
                // is the stable Android API.
                val inputTensor = created.getInputTensor(0)
                val outputTensor = created.getOutputTensor(0)
                inputTensorIndex = 0
                outputTensorIndex = 0
                expectsInt64Input = inputTensor.dataType() == DataType.INT64
                val outShape = outputTensor.shape()
                vocabSize = if (outShape.isNotEmpty()) outShape.last() else vocabSize

                interpreter = created
                loadedModelName = modelName
                Log.i(tag, "Loaded model: $loadedModelName")
                Log.i(tag, "Input dtype int64: $expectsInt64Input, vocabSize: $vocabSize")
                return
            } catch (e: Exception) {
                errors.add("$modelName -> ${e.message}")
            }
        }

        throw RuntimeException(
            "Failed to load model. Tried ${modelCandidates.joinToString()} | ${errors.joinToString(" ; ")}"
        )
    }

    /** Generates text from a prompt using autoregressive decoding. */
    fun generateResponse(
        prompt: String,
        maxTokens: Int = 50,
        temperature: Float = 0.8f,
        topK: Int = 40
    ): String {
        Log.d("ModelInference", "generateResponse")
        if (interpreter == null) {
            throw RuntimeException("Model not loaded")
        }

        val inputTokens = tokenizer.tokenize(prompt)
        if (inputTokens.isEmpty()) {
            throw IllegalArgumentException("Prompt tokenization resulted in empty sequence")
        }
        if (inputTokens.all { it == unkTokenId }) {
            throw IllegalArgumentException("Prompt tokenization resulted in only <unk> tokens")
        }

        val outputTokens = mutableListOf<Int>()
        var currentSequence = inputTokens.toMutableList()

        for (step in 0 until maxTokens) {
            val nextInputToken = currentSequence.last()
            val inputObject: Any = if (expectsInt64Input) {
                arrayOf(longArrayOf(nextInputToken.toLong()))
            } else {
                arrayOf(intArrayOf(nextInputToken))
            }
            val outputObject = Array(1) { Array(1) { FloatArray(vocabSize) } }

            try {
                interpreter!!.run(inputObject, outputObject)
            } catch (e: Exception) {
                throw RuntimeException("Inference failed at step $step: ${e.message}", e)
            }

            val logits = outputObject[0][0]

            val safeTemp = if (temperature <= 1e-6f) 1e-6f else temperature
            val scaledLogits = logits.map { it / safeTemp }.toFloatArray()

            // Block special tokens from being sampled.
            if (padTokenId in scaledLogits.indices) scaledLogits[padTokenId] = Float.NEGATIVE_INFINITY
            if (bosTokenId in scaledLogits.indices) scaledLogits[bosTokenId] = Float.NEGATIVE_INFINITY
            if (unkTokenId in scaledLogits.indices) scaledLogits[unkTokenId] = Float.NEGATIVE_INFINITY
            if (startOfTextTokenId in scaledLogits.indices) scaledLogits[startOfTextTokenId] = Float.NEGATIVE_INFINITY
            if (separatorTokenId in scaledLogits.indices) scaledLogits[separatorTokenId] = Float.NEGATIVE_INFINITY
            if (step < minTokensBeforeEos && eosTokenId in scaledLogits.indices) {
                scaledLogits[eosTokenId] = Float.NEGATIVE_INFINITY
            }

            // Numerically stable softmax.
            val maxLogit = scaledLogits.maxOrNull() ?: 0f
            val expLogits = scaledLogits.map { exp(it - maxLogit) }.toFloatArray()
            val sumExp = expLogits.sum()
            val probabilities = if (sumExp > 0f) {
                expLogits.map { it / sumExp }.toFloatArray()
            } else {
                FloatArray(vocabSize) { 1f / vocabSize }
            }

            // Top-k sampling, fallback to greedy for edge cases.
            val nextToken = if (topK <= 1 || topK >= vocabSize) {
                probabilities.indices.maxByOrNull { probabilities[it] } ?: 1
            } else {
                val topKIndices = probabilities.withIndex()
                    .sortedByDescending { it.value }
                    .take(topK)
                    .map { it.index }

                val topKProbs = topKIndices.map { probabilities[it] }.toFloatArray()
                val sum = topKProbs.sum()
                val normalizedProbs = topKProbs.map { it / sum }.toFloatArray()

                var r = Random.nextFloat()
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

            // Stop when EOS is generated.
            if (nextToken == eosTokenId || nextToken == endOfTextTokenId) break
        }

        // Remove special tokens before decode.
        val cleanedTokens = outputTokens.filter {
            it != padTokenId &&
                    it != bosTokenId &&
                    it != eosTokenId &&
                    it != unkTokenId &&
                    it != startOfTextTokenId &&
                    it != separatorTokenId &&
                    it != endOfTextTokenId
        }
        val response = if (cleanedTokens.isNotEmpty()) {
            tokenizer.decode(cleanedTokens.toIntArray())
        } else {
            val raw = tokenizer.decode(outputTokens.toIntArray()).trim()
            if (raw.isNotEmpty()) raw else "No text generated. Try a longer prompt or higher temperature."
        }

        return response
    }

    /** Releases the interpreter resources. */
    fun release() {
        Log.d("ModelInference", "release")
        interpreter?.close()
        interpreter = null
    }
}