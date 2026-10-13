package com.prefeitura.cacaplaca.feature.home.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaBackground
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaNavy
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrange
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrangeDark
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrangeLight
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextPrimary
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextSecondary
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onViewPlatesClicked: () -> Unit = {},
    onRegisterPlateClicked: () -> Unit = {},
    onUpdatePlateClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        modifier = modifier,
        uiState = uiState,
        onViewPlatesClicked = onViewPlatesClicked,
        onRegisterPlateClicked = onRegisterPlateClicked,
        onUpdatePlateClicked = onUpdatePlateClicked,
        onProfileClicked = onProfileClicked,
    )
}

@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    onViewPlatesClicked: () -> Unit = {},
    onRegisterPlateClicked: () -> Unit = {},
    onUpdatePlateClicked: () -> Unit = {},
    onProfileClicked: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CacaPlacaBackground)
    ) {
        // Header Section (White background)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                // Logo and Profile Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // App Logo
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(CacaPlacaNavy),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CacaPlacaOrange,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "CAÇAPLACAS",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp,
                            color = CacaPlacaOrange,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Profile Icon Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9))
                            .clickable { onProfileClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Perfil",
                            tint = CacaPlacaTextSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Greeting Section
                Text(
                    text = "Olá, ${uiState.agentName}!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = CacaPlacaTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Pronto para gerenciar a sinalização de hoje.",
                    fontSize = 14.sp,
                    color = CacaPlacaTextSecondary
                )
            }
        }

        // Main Content Section
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Text(
                text = "O que deseja fazer?",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = CacaPlacaTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 1: Visualizar placas (Highlight Card)
            ActionCard(
                title = "Visualizar placas",
                subtitle = "Consulte as placas cadastradas no mapa.",
                icon = Icons.Outlined.Map,
                isHighlight = true,
                onClick = onViewPlatesClicked
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Cadastrar placa
            ActionCard(
                title = "Cadastrar placa",
                subtitle = "Registre uma nova placa.",
                icon = Icons.Outlined.AddCircleOutline,
                isHighlight = false,
                onClick = onRegisterPlateClicked
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 3: Atualizar placa
            ActionCard(
                title = "Atualizar placa",
                subtitle = "Adicione novos registros à uma placa existente.",
                icon = Icons.Outlined.Sync,
                isHighlight = false,
                onClick = onUpdatePlateClicked
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isHighlight: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isHighlight) CacaPlacaOrange else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Icon Container
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isHighlight) Color.White else CacaPlacaOrangeLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CacaPlacaOrange,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Center Text Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isHighlight) Color.White else CacaPlacaTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = if (isHighlight) Color.White.copy(alpha = 0.9f) else CacaPlacaTextSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right Arrow Circle
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isHighlight) CacaPlacaOrangeDark else CacaPlacaOrangeLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = if (isHighlight) Color.White else CacaPlacaOrange,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    CacaPlacaTheme {
        HomeScreenContent(
            uiState = HomeUiState(agentName = "Agente Pedro")
        )
    }
}
