package com.example.appia2.view


import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appia2.data.GeminiClient
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch



class MainViewModel : ViewModel() {
    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    fun analisarObjeto(bitmap: Bitmap) {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 720, 1280, true)
        uiState = UiState.Loading
        viewModelScope.launch {
            try {
                val response = GeminiClient.model.generateContent(
                    content {
                        image(resizedBitmap)
                        text("""
                        Analise esta imagem e responda estritamente neste formato:
                        OBJETO: [Nome do objeto]
                        DESCRIÇÃO: [Uma frase sobre o que é]
                        PREÇO MÉDIO: [Valor aproximado em R$ no Brasil]
                        ONDE COMPRAR: [Sugestões de lojas ou sites]
                    """.trimIndent())
                    }
                )
                uiState = UiState.Success(response.text ?: "Não consegui identificar.")
            } catch (e: Exception) {
                uiState = UiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }
}

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val data: String) : UiState()
    data class Error(val message: String) : UiState()
}