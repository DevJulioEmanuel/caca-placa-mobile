package com.prefeitura.cacaplaca.feature.registration.data

import kotlinx.coroutines.delay

class RegistrationRepository(
    private val api: RegistrationApi
) {
    // 100% Mockado por enquanto! Só prepara o terreno para o futuro.
    suspend fun submitRegistrationMock(
        data: PlacaCreateData,
        photo: CapturedPhoto,
        simulateOffline: Boolean = false,
        simulateSaveError: Boolean = false
    ): Result<Boolean> { // true = sincronizado online, false = salvo offline no aparelho
        delay(1200) // Simula o tempo do estado "Enviando cadastro..."
        return when {
            simulateSaveError -> Result.failure(Exception("Não foi possível salvar"))
            simulateOffline -> Result.success(false)
            else -> Result.success(true)
        }
    }
}