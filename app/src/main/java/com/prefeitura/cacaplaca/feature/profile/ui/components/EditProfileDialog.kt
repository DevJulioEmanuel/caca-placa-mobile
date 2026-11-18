package com.prefeitura.cacaplaca.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.prefeitura.cacaplaca.feature.profile.data.model.ProfileUiModel
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaNavy
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrange

@Composable
fun EditProfileDialog(
    profile: ProfileUiModel,
    isUpdating: Boolean,
    onDismiss: () -> Unit,
    onSave: (nome: String, email: String) -> Unit
) {
    var nome by remember { mutableStateOf(profile.nome) }
    var email by remember { mutableStateOf(profile.email) }

    val isValid = nome.isNotBlank() && email.isNotBlank()

    Dialog(onDismissRequest = { if (!isUpdating) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Editar Perfil",
                    style = MaterialTheme.typography.titleLarge,
                    color = com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextPrimary
                )
                Text(
                    text = "Atualize seus dados pessoais cadastrais",
                    style = MaterialTheme.typography.bodyMedium,
                    color = com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(CacaPlacaNavy, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Dados Pessoais",
                    style = MaterialTheme.typography.titleMedium,
                    color = CacaPlacaOrange,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome Completo*") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isUpdating
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail*") },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isUpdating
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = profile.role,
                    onValueChange = { },
                    label = { Text("Tipo de Usuário") },
                    leadingIcon = {
                        Icon(Icons.Default.Badge, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = profile.dataCadastro,
                    onValueChange = { },
                    label = { Text("Data de Cadastro") },
                    leadingIcon = {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    enabled = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF1F5F9),
                            contentColor = Color.Black
                        ),
                        enabled = !isUpdating
                    ) {
                        Text("Cancelar", style = MaterialTheme.typography.labelLarge)
                    }

                    Button(
                        onClick = { onSave(nome, email) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CacaPlacaNavy,
                            contentColor = CacaPlacaOrange,
                            disabledContainerColor = CacaPlacaNavy.copy(alpha = 0.5f)
                        ),
                        enabled = isValid && !isUpdating
                    ) {
                        if (isUpdating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = CacaPlacaOrange,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Salvar", color = CacaPlacaOrange, style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
    }
}
