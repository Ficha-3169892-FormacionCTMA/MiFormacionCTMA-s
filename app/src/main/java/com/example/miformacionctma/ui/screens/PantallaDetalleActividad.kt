package com.example.miformacionctma.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.model.ActividadEstado
import com.example.miformacionctma.domain.model.Role
import com.example.miformacionctma.ui.components.EvidenciaSection
import com.example.miformacionctma.util.FileUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun obtenerUriSegura(context: Context): Uri? {
    return try {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "EVIDENCIA_${timeStamp}_"

        // Debe coincidir con la ruta definida en file_paths.xml ("evidencias/")
        val storageDir = File(context.getExternalFilesDir(null), "evidencias")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val file = File.createTempFile(fileName, ".jpg", storageDir)

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleActividad(
    actividadId: Long,
    actividades: List<ActividadFormativa>,
    userRole: Role,
    onBackClick: () -> Unit,
    onDeleteClick: (Long) -> Unit,
    onStatusUpdate: (Long, ActividadEstado) -> Unit,
    onEvidenciaCaptured: (Long, Uri, String, Long) -> Unit,
    onEvidenciaDelete: (Long) -> Unit = {}
) {
    val actividad = actividades.find { it.id == actividadId }
    val context = LocalContext.current

    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para tomar la foto
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && tempCameraUri != null && actividad != null) {
            val (mime, size) = FileUtils.getMetadata(context, tempCameraUri!!)
            onEvidenciaCaptured(actividad.id, tempCameraUri!!, mime, size)
        }
    }

    // Función desacoplada para lanzar la cámara con la Uri ya lista
    val ejecutarLanzamientoCamara = {
        val uri = obtenerUriSegura(context)
        if (uri != null) {
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para solicitar el permiso
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            ejecutarLanzamientoCamara()
        } else {
            Toast.makeText(context, "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // Función principal invocada al hacer clic
    val solicitarCamara = {
        val tienePermiso = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (tienePermiso) {
            ejecutarLanzamientoCamara()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalles", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (actividad != null && userRole == Role.INSTRUCTOR) {
                        IconButton(onClick = { onDeleteClick(actividad.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (actividad == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Actividad no encontrada")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = actividad.titulo,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(text = "Estado: ${actividad.estado}", color = MaterialTheme.colorScheme.primary)
                    }
                }

                EvidenciaSection(
                    evidencia = actividad.evidencia,
                    onEvidenciaCaptured = { _ ->
                        solicitarCamara()
                    },
                    onRemove = { solicitarCamara() },
                    onDelete = { onEvidenciaDelete(actividad.id) },
                    canEdit = userRole == Role.STUDENT
                )

                if (userRole == Role.INSTRUCTOR) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Cambiar Estado", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                ActividadEstado.entries.forEach { estado ->
                                    FilterChip(
                                        selected = actividad.estado == estado,
                                        onClick = { onStatusUpdate(actividad.id, estado) },
                                        label = { Text(estado.name) }
                                    )
                                }
                            }
                        }
                    }
                }

                DetailItem(label = "Descripción", value = actividad.descripcion ?: "Sin descripción")
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}