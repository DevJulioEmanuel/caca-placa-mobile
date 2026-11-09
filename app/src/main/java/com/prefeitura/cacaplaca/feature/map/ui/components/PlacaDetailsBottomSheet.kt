package com.prefeitura.cacaplaca.feature.map.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.prefeitura.cacaplaca.feature.map.data.model.PlacaUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlacaDetailsBottomSheet(
    visible: Boolean,
    placa: PlacaUiModel?,
    onDismiss: () -> Unit,
    onUpdateSuccess: () -> Unit
) {
    if (visible && placa != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            PlacaDetailsCard(
                placa = placa,
                onDismiss = onDismiss,
                onUpdateSuccess = onUpdateSuccess
            )
        }
    }
}
