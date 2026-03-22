package com.example.palma.ai

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * SentencePieceTokenizer: Wrapper for SentencePiece tokenization.
 * Loads vocabulary from assets and provides encode/decode functionality.
 */
class SentencePieceTokenizer(context: Context) {
    private val vocabulary = mutableMapOf<String, Int>()
    private val reverseVocab = mutableMapOf<Int, String>()
    private val unkToken = "<unk>"
    private val padToken = "<pad>"
    
    init {
        loadVocabulary(context)
    }
    
    /**
     * Load SentencePiece vocabulary from assets
     */
    private fun loadVocabulary(context: Context) {
        try {
            val assetManager = context.assets
            val inputStream = assetManager.open("spm_vocab.txt")
            
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var tokenId = 0
                var line: String?
                
                while (reader.readLine().also { line = it } != null) {
                    val parts = line!!.trim().split("\t")
                    
                    if (parts.isNotEmpty()) {
                        val token = parts[0]
                        vocabulary[token] = tokenId
                        reverseVocab[tokenId] = token
                        tokenId++
                    }
                }
            }
            
            inputStream.close()
            println("✓ Vocabulary loaded: ${vocabulary.size} tokens")
            
        } catch (e: Exception) {
            throw RuntimeException("Failed to load vocabulary: ${e.message}", e)
        }
    }
    
    /**
     * Tokenize text into token IDs
     */
    fun tokenize(text: String): IntArray {
        // Simple whitespace tokenization
        // For production, use official SentencePiece library or export tokens
        val tokens = text.split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .map { word ->
                vocabulary[word] ?: vocabulary[unkToken] ?: 1 // Fallback to UNK
            }
            .toIntArray()
        
        return tokens
    }
    
    /**
     * Decode token IDs back to text
     */
    fun decode(tokenIds: IntArray): String {
        val tokens = tokenIds.mapNotNull { id ->
            reverseVocab[id]?.let { token ->
                // Remove special tokens and sentencepiece markers
                when {
                    token.startsWith("▁") || token.startsWith("_") -> 
                        " " + token.drop(1)
                    token.startsWith("<") && token.endsWith(">") -> 
                        ""  // Skip special tokens like <pad>, <unk>
                    else -> token
                }
            }
        }
        
        return tokens.joinToString("").trim()
    }
    
    /**
     * Get vocabulary size
     */
    fun getVocabSize(): Int = vocabulary.size
    
    /**
     * Get token ID for a specific string
     */
    fun getTokenId(token: String): Int? = vocabulary[token]
    
    /**
     * Get token string for a specific ID
     */
    fun getToken(id: Int): String? = reverseVocab[id]
}
