package com.prefeitura.cacaplaca.feature.map.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.prefeitura.cacaplaca.feature.map.data.model.PlacaUiModel
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaBackground
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrange
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrangeDark
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrangeLight
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextPrimary
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextSecondary
import java.util.Locale

private fun getPlateDescription(code: String): String {
    return when (code.uppercase()) {
        "R-1" -> "Parada obrigatória"
        "R-2" -> "Dê a preferência"
        "R-6A", "R-6a" -> "Proibido estacionar"
        else -> "Sinalização $code"
    }
}

private fun formatEnumText(text: String?): String {
    if (text.isNullOrEmpty()) return "Não informado"
    return text.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
}

@Composable
fun PlacaDetailsCard(
    placa: PlacaUiModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember(placa.id) { mutableStateOf(false) }
    var selectedTab by remember(placa.id) { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .verticalScroll(rememberScrollState())
            .animateContentSize()
    ) {
        // Title and Close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = getPlateDescription(placa.codigoContran),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                color = CacaPlacaTextPrimary
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = CacaPlacaTextPrimary
                )
            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Column(
                    modifier = Modifier
                        .clickable { selectedTab = 0 }
                        .padding(bottom = 8.dp, end = 24.dp)
                ) {
                    Text(
                        text = "Situação atual",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (selectedTab == 0) CacaPlacaOrange else CacaPlacaTextSecondary
                    )
                    if (selectedTab == 0) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(3.dp)
                                .background(CacaPlacaOrange, RoundedCornerShape(1.5.dp))
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .clickable { selectedTab = 1 }
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "Histórico",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (selectedTab == 1) CacaPlacaOrange else CacaPlacaTextSecondary
                    )
                    if (selectedTab == 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(3.dp)
                                .background(CacaPlacaOrange, RoundedCornerShape(1.5.dp))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CacaPlacaBackground)
                ) {
                    val fotoUrl = placa.ultimoEvento?.fotoUrl
                    if (!fotoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = fotoUrl,
                            contentDescription = "Foto da placa",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = CacaPlacaTextSecondary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${placa.codigoContran} • ${placa.id?.take(8)?.uppercase() ?: "---"}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (isExpanded) {
                        val estadoGeral = placa.ultimoEvento?.estadoConservacao ?: "REGULAR"
                        val geralText = when (estadoGeral.uppercase()) {
                            "BOM" -> "Bom"
                            "REGULAR" -> "Regular"
                            "RUIM" -> "Ruim"
                            else -> estadoGeral
                        }
                        Text(
                            text = "Conservação geral: $geralText",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = CacaPlacaOrange
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }

                    val dateStr = placa.createdAt?.take(10)?.split("-")?.reversed()?.joinToString("/") ?: "N/A"
                    Text(
                        text = "Registro: $dateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextSecondary
                    )

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(2.dp))
                        val createdBy = placa.ultimoEvento?.createdBy?.let { "Agente #$it" } ?: "Agente Sistema"
                        Text(
                            text = "Responsável: $createdBy",
                            style = MaterialTheme.typography.bodySmall,
                            color = CacaPlacaTextSecondary
                        )
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        val estado = placa.ultimoEvento?.estadoConservacao ?: "REGULAR"
                        val situacaoText = when (estado.uppercase()) {
                            "BOM" -> "Situação: Bom"
                            "REGULAR" -> "Situação: Regular"
                            "RUIM" -> "Situação: Ruim"
                            else -> "Situação: $estado"
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CacaPlacaOrangeLight
                        ) {
                            Text(
                                text = situacaoText,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = CacaPlacaOrangeDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val logradouro = placa.endereco?.logradouro ?: "Endereço não informado"
            val bairro = placa.endereco?.bairro
            val addressDisplay = if (!bairro.isNullOrEmpty()) {
                if (isExpanded) "$logradouro • $bairro • Quixadá, CE" else "$logradouro • $bairro"
            } else {
                logradouro
            }

            if (isExpanded) {
                Text(
                    text = "Localização",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaTextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(
                text = addressDisplay,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CacaPlacaTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isExpanded) {
                    "Latitude ${String.format(Locale.US, "%.4f", placa.latitude)} • Longitude ${String.format(Locale.US, "%.4f", placa.longitude)}"
                } else {
                    "Quixadá, CE • ${String.format(Locale.US, "%.4f", placa.latitude)}, ${String.format(Locale.US, "%.4f", placa.longitude)}"
                },
                style = MaterialTheme.typography.bodySmall,
                color = CacaPlacaTextSecondary
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                InfoAttributeCard(
                    title = "Tipo de película",
                    value = formatEnumText(placa.tipoPelicula),
                    statusLabel = "Conservação da película",
                    statusValue = formatEnumText(placa.ultimoEvento?.estadoPelicula)
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoAttributeCard(
                    title = "Tipo de substrato",
                    value = formatEnumText(placa.tipoSubstrato),
                    statusLabel = "Conservação do substrato",
                    statusValue = formatEnumText(placa.ultimoEvento?.estadoSubstrato)
                )

                Spacer(modifier = Modifier.height(12.dp))

                InfoAttributeCard(
                    title = "Tipo de suporte",
                    value = formatEnumText(placa.tipoSuporte),
                    statusLabel = "Conservação do suporte",
                    statusValue = formatEnumText(placa.ultimoEvento?.estadoSuporte)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Observações",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = placa.ultimoEvento?.observacao ?: "Nenhuma observação registrada para esta placa.",
                    style = MaterialTheme.typography.bodySmall,
                    color = CacaPlacaTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (!isExpanded) {
                Button(
                    onClick = { isExpanded = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CacaPlacaOrange)
                ) {
                    Text(
                        text = "Ver informações completas",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { /* New report action */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, CacaPlacaOrange),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CacaPlacaOrange)
                ) {
                    Text(
                        text = "Cadastrar novo relatório",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaOrange
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Histórico de eventos",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = CacaPlacaTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = CacaPlacaBackground
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = placa.ultimoEvento?.tipoEvento ?: "Criação",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val ts = placa.ultimoEvento?.timestamp?.take(10)?.split("-")?.reversed()?.joinToString("/") ?: placa.createdAt?.take(10) ?: ""
                    Text(
                        text = "Data: $ts",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Estado: ${placa.ultimoEvento?.estadoConservacao ?: "N/A"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
