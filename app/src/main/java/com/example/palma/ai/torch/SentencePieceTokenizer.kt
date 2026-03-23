package com.example.palma.ai.torch

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.File

class SentencePieceTokenizer(context: Context) {
    private val tag = "SentencePieceTokenizer"
    private val processor: Any?
    private val sp4jModel: Any?
    private val sp4jAlgorithm: Any?
    private val encodeMethod = "encodeAsIds"
    private val decodeMethod = "decodeIds"
    private val vocab: Map<String, Int>
    private val idToToken: Map<Int, String>
    private val unkId: Int

    init {
        val loadedVocab = loadVocab(copyVocabToInternalStorage(context))
        vocab = loadedVocab
        idToToken = loadedVocab.entries.associate { it.value to it.key }
        unkId = loadedVocab["<unk>"] ?: 3

        val modelPath = copyModelToInternalStorage(context)
        processor = tryCreateProcessorInstance(modelPath)
        val sp4j = tryCreateSentencePiece4J(modelPath)
        sp4jModel = sp4j?.first
        sp4jAlgorithm = sp4j?.second

        if (processor != null) {
            Log.i(tag, "Tokenizer mode: runtime SentencePiece")
        } else if (sp4jModel != null && sp4jAlgorithm != null) {
            Log.i(tag, "Tokenizer mode: SentencePiece4J")
        } else {
            Log.i(tag, "Tokenizer mode: vocab fallback")
        }
    }

    private fun copyModelToInternalStorage(context: Context): String {
        Log.d("SentencePieceTokenizer", "copyModelToInternalStorage")
        try {
            val outFile = File(context.filesDir, "spm_8k.model")
            if (!outFile.exists()) {
                context.assets.open("spm_8k.model").use { input ->
                    outFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            return outFile.absolutePath
        } catch (e: Exception) {
            throw RuntimeException("Failed to prepare SentencePiece model: ${e.message}", e)
        }
    }

    private fun copyVocabToInternalStorage(context: Context): File {
        Log.d("SentencePieceTokenizer", "copyVocabToInternalStorage")
        try {
            val outFile = File(context.filesDir, "vocab.txt")
            if (!outFile.exists()) {
                context.assets.open("vocab.txt").use { input ->
                    outFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            return outFile
        } catch (e: Exception) {
            throw RuntimeException("Failed to prepare vocab.txt: ${e.message}", e)
        }
    }

    private fun loadVocab(file: File): Map<String, Int> {
        Log.d("SentencePieceTokenizer", "loadVocab")
        val map = mutableMapOf<String, Int>()
        BufferedReader(file.reader()).use { reader ->
            reader.lineSequence().forEachIndexed { index, line ->
                val token = line.trim()
                if (token.isNotEmpty()) {
                    map[token] = index
                }
            }
        }
        if (map.isEmpty()) {
            throw RuntimeException("vocab.txt is empty")
        }
        return map
    }

    private fun tryCreateProcessorInstance(modelPath: String): Any? {
        Log.d("SentencePieceTokenizer", "tryCreateProcessorInstance")
        val candidates = listOf(
            "com.google.sentencepiece.SentencePieceProcessor",
            "org.tensorflow.lite.support.text.tokenizer.SentencePieceTokenizer"
        )

        for (className in candidates) {
            try {
                val clazz = Class.forName(className)
                val instance = clazz.getDeclaredConstructor().newInstance()

                try {
                    clazz.getMethod("load", String::class.java).invoke(instance, modelPath)
                    return instance
                } catch (_: NoSuchMethodException) {
                }

                try {
                    clazz.getMethod("initialize", String::class.java).invoke(instance, modelPath)
                    return instance
                } catch (_: NoSuchMethodException) {
                }
            } catch (t: Throwable) {
            }
        }

        return null
    }

    private fun tryCreateSentencePiece4J(modelPath: String): Pair<Any, Any>? {
        Log.d("SentencePieceTokenizer", "tryCreateSentencePiece4J")
        try {
            val modelClass = Class.forName("com.sentencepiece.Model")
            val algorithmClass = Class.forName("com.sentencepiece.SentencePieceAlgorithm")
            val scoringClass = Class.forName("com.sentencepiece.Scoring")

            val parseMethod = modelClass.getMethod("parseFrom", java.nio.file.Path::class.java)
            val pathObj = java.nio.file.Paths.get(modelPath)
            val model = parseMethod.invoke(null, pathObj)

            val scoring = scoringClass.getField("HIGHEST_SCORE").get(null)
            val algorithmCtor = algorithmClass.getConstructor(Boolean::class.javaPrimitiveType, scoringClass)
            val algorithm = algorithmCtor.newInstance(true, scoring)

            return Pair(model, algorithm)
        } catch (_: Throwable) {
            return null
        }
    }

    private fun runtimeTokenize(text: String): IntArray {
        Log.d("SentencePieceTokenizer", "runtimeTokenize")
        val p = processor ?: throw IllegalStateException("Runtime tokenizer unavailable")
        val method = p.javaClass.methods.firstOrNull {
            it.name == encodeMethod && it.parameterCount == 1 && it.parameterTypes[0] == String::class.java
        } ?: throw NoSuchMethodException("encodeAsIds(String) not found")

        val result = method.invoke(p, text)
        return when (result) {
            is IntArray -> result
            is LongArray -> result.map { it.toInt() }.toIntArray()
            is List<*> -> {
                val values = mutableListOf<Int>()
                for (item in result) {
                    if (item is Number) {
                        values.add(item.toInt())
                    }
                }
                values.toIntArray()
            }
            else -> throw IllegalStateException("Unsupported encode return type: ${result?.javaClass?.name}")
        }
    }

    private fun runtimeDecode(tokenIds: IntArray): String {
        Log.d("SentencePieceTokenizer", "runtimeDecode")
        val p = processor ?: throw IllegalStateException("Runtime tokenizer unavailable")
        val method = p.javaClass.methods.firstOrNull {
            it.name == decodeMethod && it.parameterCount == 1
        } ?: throw NoSuchMethodException("decodeIds(...) not found")

        val arg: Any = when {
            method.parameterTypes[0].isArray -> tokenIds
            List::class.java.isAssignableFrom(method.parameterTypes[0]) -> tokenIds.toList()
            else -> tokenIds.toList()
        }

        val result = method.invoke(p, arg)
        return result?.toString() ?: ""
    }

    private fun sp4jTokenize(text: String): IntArray {
        Log.d("SentencePieceTokenizer", "sp4jTokenize")
        val model = sp4jModel ?: throw IllegalStateException("SentencePiece4J model unavailable")
        val algorithm = sp4jAlgorithm ?: throw IllegalStateException("SentencePiece4J algorithm unavailable")

        val method = model.javaClass.getMethod("encodeNormalized", String::class.java, algorithm.javaClass)
        val result = method.invoke(model, text, algorithm)

        if (result is List<*>) {
            val values = mutableListOf<Int>()
            for (item in result) {
                if (item is Number) {
                    values.add(item.toInt())
                }
            }
            return values.toIntArray()
        }

        throw IllegalStateException("SentencePiece4J encode returned unsupported type: ${result?.javaClass?.name}")
    }

    private fun sp4jDecode(tokenIds: IntArray): String {
        Log.d("SentencePieceTokenizer", "sp4jDecode")
        val model = sp4jModel ?: throw IllegalStateException("SentencePiece4J model unavailable")
        val ids = ArrayList<Int>(tokenIds.size)
        for (id in tokenIds) {
            ids.add(id)
        }

        val method = model.javaClass.getMethod("decodeSmart", List::class.java)
        val result = method.invoke(model, ids)
        return result?.toString() ?: ""
    }

    private fun fallbackTokenize(text: String): IntArray {
        Log.d("SentencePieceTokenizer", "fallbackTokenize")
        val ids = mutableListOf<Int>()
        val words = text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

        for (word in words) {
            var remaining = "▁$word"
            while (remaining.isNotEmpty()) {
                var found: Int? = null
                var end = remaining.length
                while (end > 0) {
                    val piece = remaining.substring(0, end)
                    val id = vocab[piece]
                    if (id != null) {
                        found = id
                        ids.add(id)
                        remaining = remaining.substring(end)
                        break
                    }
                    end -= 1
                }

                if (found == null) {
                    ids.add(unkId)
                    break
                }
            }
        }

        return if (ids.isEmpty()) intArrayOf(unkId) else ids.toIntArray()
    }

    private fun fallbackDecode(tokenIds: IntArray): String {
        Log.d("SentencePieceTokenizer", "fallbackDecode")
        val special = setOf("<pad>", "<s>", "</s>", "<unk>")
        val pieces = mutableListOf<String>()
        for (id in tokenIds) {
            val token = idToToken[id]
            if (token != null && !special.contains(token)) {
                pieces.add(token)
            }
        }

        val sb = StringBuilder()
        for (piece in pieces) {
            if (piece.startsWith("▁")) {
                val content = piece.removePrefix("▁")
                if (content.isEmpty()) continue
                if (sb.isNotEmpty()) sb.append(' ')
                sb.append(content)
            } else {
                sb.append(piece)
            }
        }

        return sb.toString()
            .replace(Regex("\\s+([,.!?;:])"), "$1")
            .trim()
    }

    private fun sanitizeDecodedText(text: String): String {
        Log.d("SentencePieceTokenizer", "sanitizeDecodedText")
        return text
            .replace(Regex("<\\|[^|>]+\\|>"), " ")
            .replace("<pad>", " ")
            .replace("<s>", " ")
            .replace("</s>", " ")
            .replace("<unk>", " ")
            .replace(Regex("\\s+([,.!?;:])"), "$1")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun tokenize(text: String): IntArray {
        Log.d("SentencePieceTokenizer", "tokenize")
        try {
            return if (processor != null) {
                runtimeTokenize(text)
            } else if (sp4jModel != null && sp4jAlgorithm != null) {
                sp4jTokenize(text)
            } else {
                fallbackTokenize(text)
            }
        } catch (e: Exception) {
            throw RuntimeException("SentencePiece encode failed: ${e.message}", e)
        }
    }

    fun decode(tokenIds: IntArray): String {
        Log.d("SentencePieceTokenizer", "decode")
        try {
            val decoded = if (processor != null) {
                runtimeDecode(tokenIds)
            } else if (sp4jModel != null && sp4jAlgorithm != null) {
                sp4jDecode(tokenIds)
            } else {
                fallbackDecode(tokenIds)
            }
            return sanitizeDecodedText(decoded)
        } catch (e: Exception) {
            throw RuntimeException("SentencePiece decode failed: ${e.message}", e)
        }
    }
}