package com.example.miformacionctma.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.miformacionctma.domain.ActividadFormativa
import com.example.miformacionctma.domain.Prioridad
import com.example.miformacionctma.domain.estadoActividad

@Composable
fun TarjetaActividad(
    actividad: ActividadFormativa,
    onClick: () -> Unit
) {
    val estado = estadoActividad(actividad)
    val (colorPrioridad, iconoPrioridad) = when (actividad.prioridad) {
        Prioridad.ALTA -> Color(0xFFEF5350) to Icons.Default.Favorite
        Prioridad.MEDIA -> Color(0xFFFFA726) to Icons.Default.Star
        Prioridad.BAJA -> Color(0xFF66BB6A) to Icons.Default.Notifications
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                role = Role.Button
                stateDescription = "Estado: $estado"
            }
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = actividad.titulo,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = iconoPrioridad,
                    contentDescription = null,
                    tint = colorPrioridad,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = actividad.descripcion ?: "Sin descripción",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                InfoTag(
                    icon = Icons.Default.DateRange,
                    text = "${actividad.diasRestantes} días",
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = estado,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colorPrioridad
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progreso",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = "${actividad.progreso}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress = { actividad.progreso / 100f },
                    modifier = Modifier.fillMaxWidth(),
                    color = colorPrioridad,
                    trackColor = colorPrioridad.copy(alpha = 0.2f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun InfoTag(icon: ImageVector, text: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = color)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = color)
    }
}

@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true
)
@Composable
private fun TarjetaActividadPreview() {
    TarjetaActividad(
        actividad = ActividadFormativa(
            id = 999L,
            titulo = "Título de prueba corto",
            descripcion = "Actividad utilizada para probar el diseño.",
            progreso = 70,
            fecha = "2026-08-19",
            diasRestantes = 2,
            prioridad = Prioridad.MEDIA
        ),
        onClick = {}
    )
}
