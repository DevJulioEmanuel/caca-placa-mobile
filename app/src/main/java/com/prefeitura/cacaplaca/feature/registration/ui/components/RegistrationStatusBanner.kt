package com.prefeitura.cacaplaca.feature.registration.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prefeitura.cacaplaca.feature.registration.ui.SaveBannerState
import com.prefeitura.cacaplaca.ui.theme.*

@Composable
fun RegistrationStatusBanner(
    bannerState: SaveBannerState,
    modifier: Modifier = Modifier
) {
    if (bannerState == SaveBannerState.NONE) return

    val isError = bannerState == SaveBannerState.ERROR
    val bgColor = if (isError) Color(0xFFFEF2F2) else CacaPlacaOrangeLight.copy(alpha = 0.6f)
    val borderColor = if (isError) Color(0xFFFECACA) else CacaPlacaOrange
    val textColor = if (isError) PinRed else CacaPlacaOrangeDark

    val (title, subtitle) = when (bannerState) {
        SaveBannerState.OFFLINE_WARNING ->
            "Você está sem conexão" to "O cadastro e a foto foram salvos no aparelho. O envio será tentado quando houver conexão."
        SaveBannerState.SAVING ->
            "Enviando cadastro..." to "Aguarde a confirmação do servidor."
        SaveBannerState.ERROR ->
            "Não foi possível salvar" to "Os dados foram preservados. Tente novamente."
        SaveBannerState.NONE -> "" to ""
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = textColor
            )
        }
    }
}