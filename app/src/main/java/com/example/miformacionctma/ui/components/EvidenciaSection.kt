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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.miformacionctma.domain.model.Evidencia
import com.example.miformacionctma.domain.model.EvidenciaStatus

@Composable
fun EvidenciaSection(
    evidencias: List<Evidencia>,
    onEvidenciaCaptured: (Uri) -> Unit,
    onRemove: () -> Unit,
    onDelete: (Long) -> Unit = {},
    canEdit: Boolean = true,
    isInstructor: Boolean = false
) {
    var showFullScreenUri by remember { mutableStateOf<String?>(null) }

    // Launcher únicamente para seleccionar imágenes de la Galería
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onEvidenciaCaptured(it) }
    }

    if (showFullScreenUri != null) {
        AlertDialog(
            onDismissRequest = { showFullScreenUri = null },
            confirmButton = {
                TextButton(onClick = { showFullScreenUri = null }) { Text("Cerrar") }
            },
            text = {
                AsyncImage(
                    model = showFullScreenUri,
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
                text = "Evidencias de la tarea (${evidencias.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (evidencias.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    evidencias.forEach { evidencia ->
                        EvidenciaItem(
                            evidencia = evidencia,
                            canEdit = canEdit,
                            isInstructor = isInstructor,
                            onFullScreen = { showFullScreenUri = it },
                            onRemove = onRemove,
                            onDelete = { onDelete(evidencia.id ?: 0L) }
                        )
                    }
                }
                
                if (canEdit) {
                    Spacer(modifier = Modifier.height(16.dp))
                    AccionesSubida(
                        onCámara = { onEvidenciaCaptured(Uri.EMPTY) },
                        onGalería = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                }
            } else if (canEdit) {
                AccionesSubida(
                    onCámara = { onEvidenciaCaptured(Uri.EMPTY) },
                    onGalería = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            } else {
                Text("No hay evidencias adjuntas.")
            }
        }
    }
}

@Composable
fun EvidenciaItem(
    evidencia: Evidencia,
    canEdit: Boolean,
    isInstructor: Boolean,
    onFullScreen: (String) -> Unit,
    onRemove: () -> Unit,
    onDelete: () -> Unit
) {
    Column {
        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            Surface(
                onClick = { onFullScreen(evidencia.uri) },
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

            if ((canEdit || isInstructor) && evidencia.status != EvidenciaStatus.SUBIENDO) {
                Row(
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (canEdit) {
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

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val textoAutor = if (evidencia.userName != null) "Subido por: ${evidencia.userName}" else "Evidencia legada"
            Text(
                text = if (evidencia.status == EvidenciaStatus.SUBIENDO) "Subiendo..." else textoAutor,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Estado: ${evidencia.status}",
                style = MaterialTheme.typography.labelSmall,
                color = if (evidencia.status == EvidenciaStatus.FALLIDA) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AccionesSubida(
    onCámara: () -> Unit,
    onGalería: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onCámara,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cámara")
        }

        OutlinedButton(
            onClick = onGalería,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Galería")
        }
    }
}
