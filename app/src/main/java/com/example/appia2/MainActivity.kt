package com.example.appia2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
// Importe o tema gerado pelo Android Studio (ajuste se o nome for diferente)
import com.example.appia2.ui.theme.AppIA2Theme
// Importe a sua Screen e seu ViewModel
import com.example.appia2.ui.screens.VisionAiScreen
import com.example.appia2.view.MainViewModel


class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppIA2Theme {
                VisionAiScreen(viewModel)
            }
        }
    }
}
