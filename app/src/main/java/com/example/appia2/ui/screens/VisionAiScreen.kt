package com.example.appia2.ui.screens

import android.graphics.BitmapFactory
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.appia2.view.MainViewModel
import com.example.appia2.view.UiState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import java.nio.ByteBuffer

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VisionAiScreen(viewModel: MainViewModel) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        if (cameraPermissionState.status.isGranted) {
            CameraContent(viewModel)
        } else {
            PermissionRequestContent(permissionState = cameraPermissionState)
        }
    }
}

@Composable
private fun CameraContent(viewModel: MainViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val uiState = viewModel.uiState

    // Configuração do Controller da Câmera
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            bindToLifecycle(lifecycleOwner)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Camada da Câmera (Fundo)
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = controller
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 2. Camada de Resultado (Sobreposta no Topo)
        when (uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            is UiState.Success -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 48.dp) // Espaço para não cobrir a barra de status
                        .align(Alignment.TopCenter),
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Resultado da Análise:",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uiState.data,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            is UiState.Error -> {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .padding(top = 48.dp)
                        .align(Alignment.TopCenter),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = "Erro: ${uiState.message}",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
            else -> {}
        }

        // 3. Camada do Botão (Sobreposta na Base)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = {
                    val mainExecutor = ContextCompat.getMainExecutor(context)
                    controller.takePicture(
                        mainExecutor,
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                viewModel.analisarObjeto(image.toBitmap())
                                image.close()
                            }
                        }
                    )
                },
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Analisar",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionRequestContent(permissionState: com.google.accompanist.permissions.PermissionState) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (permissionState.status.shouldShowRationale) "A câmera é necessária para identificar objetos." else "Precisamos de acesso à sua câmera para usar a IA.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { permissionState.launchPermissionRequest() }) {
            Text("Conceder Permissão")
        }
    }
}

// Extensão necessária para converter o frame da câmera para Bitmap
fun ImageProxy.toBitmap(): android.graphics.Bitmap {
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}