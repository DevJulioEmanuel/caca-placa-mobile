package com.prefeitura.cacaplaca.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prefeitura.cacaplaca.R
import com.prefeitura.cacaplaca.ui.components.CacaPlacaHeader
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaBackground
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
                CacaPlacaHeader(
                    onProfileClicked = onProfileClicked
                )

                Spacer(modifier = Modifier.height(24.dp))

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

            ActionCard(
                title = "Visualizar placas",
                subtitle = "Consulte as placas cadastradas no mapa.",
                icon = painterResource(R.drawable.map),
                isHighlight = true,
                onClick = onViewPlatesClicked
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionCard(
                title = "Cadastrar placa",
                subtitle = "Registre uma nova placa.",
                icon = painterResource(R.drawable.circle_plus),
                isHighlight = false,
                onClick = onRegisterPlateClicked
            )

            Spacer(modifier = Modifier.height(16.dp))

            ActionCard(
                title = "Atualizar placa",
                subtitle = "Adicione novos registros à uma placa existente.",
                icon = painterResource(R.drawable.update_plate),
                isHighlight = false,
                onClick = onUpdatePlateClicked
            )
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
