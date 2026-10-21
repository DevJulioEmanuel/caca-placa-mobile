package com.prefeitura.cacaplaca.feature.registration.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prefeitura.cacaplaca.feature.registration.data.PlateTypeOption
import com.prefeitura.cacaplaca.feature.registration.ui.components.RegistrationTopBar
import com.prefeitura.cacaplaca.ui.theme.*

@Composable
fun PlateSelectionContent(
    query: String,
    onQueryChanged: (String) -> Unit,
    availablePlates: List<PlateTypeOption>,
    tempSelectedPlate: PlateTypeOption?,
    onTempPlateSelected: (PlateTypeOption) -> Unit,
    onConfirmSelection: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Filtra as placas localmente com base na pesquisa
    val filteredPlates = availablePlates.filter {
        it.code.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CacaPlacaBackground)
    ) {
        RegistrationTopBar(
            title = "Selecionar código da placa",
            subtitle = "Dados da placa • Etapa 2 de 2",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceWhite,
                border = BorderStroke(1.dp, OutlineGray.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Buscar por código ou descrição",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = query,
                        onValueChange = onQueryChanged,
                        placeholder = { Text("R-", style = MaterialTheme.typography.bodyMedium) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { onQueryChanged("") }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Limpar")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CacaPlacaOrange,
                            unfocusedBorderColor = OutlineGray
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Selecione o código correspondente à placa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = CacaPlacaTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${filteredPlates.size} opções encontradas",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = CacaPlacaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        items(filteredPlates) { plate ->
                            val isSelected = tempSelectedPlate?.code == plate.code

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTempPlateSelected(plate) },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) CacaPlacaOrangeLight else SurfaceWhite,
                                border = BorderStroke(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) CacaPlacaOrange else OutlineGray
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = plate.code,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                                            color = CacaPlacaTextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = plate.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = CacaPlacaTextPrimary
                                        )
                                    }
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { onTempPlateSelected(plate) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = CacaPlacaOrange,
                                            unselectedColor = OutlineGray
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Fundo branco na zona do botão final
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = CacaPlacaBackground
        ) {
            Button(
                onClick = onConfirmSelection,
                enabled = tempSelectedPlate != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CacaPlacaOrange,
                    contentColor = CacaPlacaTextPrimary
                )
            ) {
                Text(
                    text = "Usar tipo selecionado",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}