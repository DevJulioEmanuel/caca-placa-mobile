package com.prefeitura.cacaplaca.feature.registration.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.prefeitura.cacaplaca.feature.registration.data.model.*
import com.prefeitura.cacaplaca.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegistrationAdditionalInfo(
    expanded: Boolean,
    afastamentoInput: String,
    estadoConservacao: EstadoConservacao,
    tipoPelicula: TipoPelicula?,
    tipoSubstrato: TipoSubstrato?,
    tipoSuporte: TipoSuporte?,
    observacaoInput: String,
    onToggleExpanded: () -> Unit,
    onAfastamentoChanged: (String) -> Unit,
    onEstadoConservacaoSelected: (EstadoConservacao) -> Unit,
    onTipoPeliculaSelected: (TipoPelicula?) -> Unit,
    onTipoSubstratoSelected: (TipoSubstrato?) -> Unit,
    onTipoSuporteSelected: (TipoSuporte?) -> Unit,
    onObservacaoChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SurfaceWhite,
        border = BorderStroke(1.dp, OutlineGray.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpanded() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Informações adicionais",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Text(
                        text = "Opcional • Afastamento e materiais",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextSecondary
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = CacaPlacaTextPrimary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    HorizontalDivider(color = OutlineGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Afastamento da via (metros)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = afastamentoInput,
                        onValueChange = onAfastamentoChanged,
                        placeholder = { Text("Ex: 1.5", style = MaterialTheme.typography.bodyMedium) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Estado de conservação geral",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EstadoConservacao.entries.forEach { estado ->
                            FilterChip(
                                selected = estadoConservacao == estado,
                                onClick = { onEstadoConservacaoSelected(estado) },
                                label = { Text(estado.name, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tipo de película",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TipoPelicula.entries.forEach { item ->
                            FilterChip(
                                selected = tipoPelicula == item,
                                onClick = {
                                    onTipoPeliculaSelected(if (tipoPelicula == item) null else item)
                                },
                                label = { Text(item.name, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tipo de substrato",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TipoSubstrato.entries.forEach { item ->
                            FilterChip(
                                selected = tipoSubstrato == item,
                                onClick = {
                                    onTipoSubstratoSelected(if (tipoSubstrato == item) null else item)
                                },
                                label = { Text(item.name, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tipo de suporte",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TipoSuporte.entries.forEach { item ->
                            FilterChip(
                                selected = tipoSuporte == item,
                                onClick = {
                                    onTipoSuporteSelected(if (tipoSuporte == item) null else item)
                                },
                                label = { Text(item.name, style = MaterialTheme.typography.bodySmall) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Observação",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = observacaoInput,
                        onValueChange = onObservacaoChanged,
                        placeholder = { Text("Ex: Placa pichada, poste inclinado...", style = MaterialTheme.typography.bodyMedium) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )
                }
            }
        }
    }
}
