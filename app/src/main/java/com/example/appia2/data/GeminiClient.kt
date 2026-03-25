package com.example.appia2.data

// Importe o BuildConfig do seu pacote específico
import com.example.appia2.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig

object GeminiClient {
    // Agora o código busca o valor que você definiu no local.properties
    private val API_KEY = BuildConfig.GEMINI_API_KEY

    val model = GenerativeModel(
        // Use o modelo disponível na sua região
        modelName = "gemini-3-flash-preview",
        apiKey = API_KEY,
        generationConfig = generationConfig {
            temperature = 0.7f
            topK = 40
            topP = 0.95f
        }
    )
}