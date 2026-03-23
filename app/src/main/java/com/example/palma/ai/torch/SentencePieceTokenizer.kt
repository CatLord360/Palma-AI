package com.example.palma.ai.torch

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.BufferedReader

class SentencePieceTokenizer(context: Context) {
    private val vocab: Map<String, Int>
    private val idToToken: Map<Int, String>

    init {
        val vocabFile = copyVocabToInternalStorage(context)
        vocab = loadVocab(vocabFile)
        idToToken = vocab.entries.associate { it.value to it.key }
    }

    private fun copyVocabToInternalStorage(context: Context): File {
        val outFile = File(context.filesDir, "vocab.txt")
        if (!outFile.exists()) {
            context.assets.open("vocab.txt").use { input ->
                FileOutputStream(outFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        return outFile
    }

    private fun loadVocab(file: File): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        BufferedReader(file.reader()).use { reader ->
            reader.lineSequence().forEachIndexed { index, line ->
                val token = line.trim()
                if (token.isNotEmpty()) {
                    map[token] = index
                }
            }
        }
        return map
    }

    fun tokenize(text: String): IntArray {
        // Simple whitespace tokenizer; for true SentencePiece, use your pre-tokenized vocab
        val tokens = text.split(" ")
        return tokens.map { token -> vocab[token] ?: vocab["<unk>"] ?: 0 }.toIntArray()
    }

    fun decode(tokenIds: IntArray): String {
        val tokens = tokenIds.map { idToToken[it] ?: "<unk>" }
        return tokens.joinToString(" ")
    }
}