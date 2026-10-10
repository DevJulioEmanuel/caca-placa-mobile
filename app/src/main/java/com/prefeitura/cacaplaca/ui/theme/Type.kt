package com.prefeitura.cacaplaca.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.prefeitura.cacaplaca.R // Ajuste para o pacote correto do seu R

val MontserratFontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium), // Peso 500
    Font(R.font.montserrat_bold, FontWeight.Bold), // Peso 700
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold) // Peso 800
)

val Typography = Typography(

    // Títulos Principais (Ex: "Olá, Agente Pedro!")[cite: 12]
    titleLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        color = TextPrimary
    ),

    // Texto de Apoio / Subtítulos (Ex: "Pronto para gerenciar a sinalização de hoje.")
    bodyMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
        color = TextSecondary
    ),

    // Botões e Ações (Ex: "ENTRAR")[cite: 14]
    labelLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp,
        lineHeight = 22.5.sp,
        letterSpacing = 0.38.sp,
    )
)