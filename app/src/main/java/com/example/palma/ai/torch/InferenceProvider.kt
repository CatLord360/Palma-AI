package com.example.palma.ai.torch

import android.content.Context
import android.util.Log

object InferenceProvider{
    @Volatile
    private var instance: ModelInference? = null

    fun get(context: Context): ModelInference {
        Log.d("InferenceProvider", "get")
        val existing = instance
        if (existing != null) return existing

        return synchronized(this) {
            val recheck = instance
            if (recheck != null) {
                recheck
            } else {
                ModelInference(context.applicationContext).also { instance = it }
            }
        }
    }

    fun release() {
        synchronized(this) {
            instance?.release()
            instance = null
        }
    }
}