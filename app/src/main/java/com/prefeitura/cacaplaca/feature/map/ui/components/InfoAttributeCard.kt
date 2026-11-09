package com.prefeitura.cacaplaca.feature.map.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaBackground
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrange
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextPrimary
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextSecondary
import com.prefeitura.cacaplaca.ui.theme.OutlineGray

@Composable
fun InfoAttributeCard(
    title: String,
    value: String,
    statusLabel: String,
    statusValue: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = CacaPlacaBackground,
        border = BorderStroke(1.dp, OutlineGray.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = CacaPlacaTextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaTextPrimary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = OutlineGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = CacaPlacaTextSecondary
                )
                Text(
                    text = statusValue,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = CacaPlacaOrange
                )
            }
        }
    }
}
