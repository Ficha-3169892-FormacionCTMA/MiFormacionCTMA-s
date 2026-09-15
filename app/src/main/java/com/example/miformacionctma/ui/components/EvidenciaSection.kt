package com.example.miformacionctma.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.miformacionctma.domain.model.Evidencia
import com.example.miformacionctma.domain.model.EvidenciaStatus

@Composable
fun EvidenciaSection(
    evidencia: Evidencia?,
    onEvidenciaCaptured: (Uri) -> Unit,
    onRemove: () -> Unit,
    onDelete: () -> Unit = {},
    canEdit: Boolean = true
) {
    var showFullScreen by remember { mutableStateOf(false) }

    // Launcher únicamente para seleccionar imágenes de la Galería
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onEvidenciaCaptured(it) }
    }

    if (showFullScreen && evidencia != null) {
        AlertDialog(
            onDismissRequest = { showFullScreen = false },
            confirmButton = {
                TextButton(onClick = { showFullScreen = false }) { Text("Cerrar") }
            },
            text = {
                AsyncImage(
                    model = evidencia.uri,
                    contentDescription = "Pantalla completa",
                    modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Evidencia de la tarea",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (evidencia != null) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    Surface(
                        onClick = { showFullScreen = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AsyncImage(
                            model = evidencia.uri,
                            contentDescription = "Vista previa",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    if (evidencia.status == EvidenciaStatus.SUBIENDO) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }

                    if (canEdit && evidencia.status != EvidenciaStatus.SUBIENDO) {
                        Row(
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                IconButton(onClick = onRemove) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Cambiar",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                IconButton(onClick = onDelete) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.onError
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (evidencia.status == EvidenciaStatus.SUBIENDO) {
                        Text(
                            text = "Subiendo a la nube...",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    } else {
                        Text(
                            text = "Estado: ${evidencia.status}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (evidencia.status == EvidenciaStatus.FALLIDA) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else if (canEdit) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            // Emite una señal limpia para que PantallaDetalleActividad verifique permisos y abra la cámara de forma segura
                            onEvidenciaCaptured(Uri.EMPTY)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cámara")
                    }

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Galería")
                    }
                }
            } else {
                Text("No hay evidencia adjunta.")
            }
        }
    }
}