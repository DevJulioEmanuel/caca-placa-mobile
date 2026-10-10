package com.prefeitura.cacaplaca.ui.theme // Ajusta para o teu package correto

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.prefeitura.cacaplaca.R

// 1. Definição da Família de Fontes Montserrat
val MontserratFontFamily = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium), // 500
    Font(R.font.montserrat_bold, FontWeight.Bold), // 700
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold) // 800
)

// 2. Configuração da Tipografia do Material 3
val Typography = Typography(

    // Títulos Principais
    titleLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 28.sp,
    ),

    // --> ADICIONADO: Padrão do Compose para TextFields e Textos gerais <--
    bodyLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),

    // Texto de Apoio / Subtítulos
    bodyMedium = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
    ),

    // --> ADICIONADO: Padrão para textos mais pequenos <--
    bodySmall = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // Botões e Ações
    labelLarge = TextStyle(
        fontFamily = MontserratFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 15.sp,
        lineHeight = 22.5.sp,
        letterSpacing = 0.38.sp,
    )
)