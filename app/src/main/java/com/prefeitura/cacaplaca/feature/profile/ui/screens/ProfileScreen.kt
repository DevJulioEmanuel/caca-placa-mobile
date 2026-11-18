package com.prefeitura.cacaplaca.feature.profile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prefeitura.cacaplaca.feature.profile.ui.components.EditProfileDialog
import com.prefeitura.cacaplaca.feature.profile.ui.components.PersonalInfoCard
import com.prefeitura.cacaplaca.feature.profile.ui.components.ProfileAvatarCard
import com.prefeitura.cacaplaca.feature.profile.ui.viewmodel.ProfileViewModel
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaBackground
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaNavy
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrange
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextSecondary
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextPrimary
import com.prefeitura.cacaplaca.ui.components.CacaPlacaHeader
import org.koin.androidx.compose.koinViewModel

import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onLogoutClicked: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            showEditDialog = false
            showSuccessDialog = true
        }
    }

    LaunchedEffect(uiState.updateError) {
        uiState.updateError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetUpdateState()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CacaPlacaBackground)
    ) {
        // Header
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
                CacaPlacaHeader()

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = CacaPlacaTextPrimary
                        )
                    }
                    Column {
                        Text(
                            text = "Meu Perfil",
                            style = MaterialTheme.typography.titleLarge,
                            color = CacaPlacaTextPrimary
                        )
                        Text(
                            text = "Visualize suas informações",
                            color = CacaPlacaTextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(color = CacaPlacaOrange)
                }
                uiState.errorMessage != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = uiState.errorMessage!!, color = Color.Red)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = CacaPlacaOrange)
                        ) {
                            Text("Tentar Novamente")
                        }
                    }
                }
                uiState.profile != null -> {
                    val profile = uiState.profile!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ProfileAvatarCard(role = profile.role)

                        PersonalInfoCard(
                            profile = profile,
                            onEditClicked = { showEditDialog = true }
                        )

                        Button(
                            onClick = onLogoutClicked,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFEE2E2),
                                contentColor = Color(0xFFDC2626)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Sair")
                            Spacer(Modifier.width(8.dp))
                            Text("Sair da conta", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    if (showEditDialog) {
                        EditProfileDialog(
                            profile = profile,
                            isUpdating = uiState.isUpdating,
                            onDismiss = { showEditDialog = false },
                            onSave = { nome, email ->
                                viewModel.updateProfile(nome, email)
                            }
                        )
                    }

                    if (showSuccessDialog) {
                        com.prefeitura.cacaplaca.feature.profile.ui.components.ProfileUpdateSuccessDialog(
                            onDismiss = {
                                showSuccessDialog = false
                                viewModel.resetUpdateState()
                            }
                        )
                    }
                }
            }
        }
    }
}
