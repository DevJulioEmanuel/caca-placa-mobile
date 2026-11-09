package com.prefeitura.cacaplaca.feature.update

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
import com.prefeitura.cacaplaca.core.model.PlateTypeOption
import com.prefeitura.cacaplaca.feature.update.data.UpdatePlacaUiState
import com.prefeitura.cacaplaca.feature.update.data.repository.UpdatePlacaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class UpdatePlacaViewModel(
    private val repository: UpdatePlacaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdatePlacaUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _tempSelectedPlate = MutableStateFlow<PlateTypeOption?>(null)
    val tempSelectedPlate = _tempSelectedPlate.asStateFlow()

    fun initData(placa: PlacaItemResponse) {
        val endereco = placa.endereco
        val localizacaoFormatada = if (endereco != null) {
            "${endereco.logradouro}, ${endereco.bairro} - Quixadá/CE"
        } else {
            "Localização não informada"
        }

        _uiState.update {
            it.copy(
                placaId = placa.id ?: "",
                codigoPlaca = placa.codigoContran,
                localizacao = localizacaoFormatada,
                tipoPelicula = placa.tipoPelicula ?: "",
                tipoSubstrato = placa.tipoSubstrato ?: "",
                tipoSuporte = placa.tipoSuporte ?: ""
            )
        }
    }

    fun onPhotoSelected(uri: Uri?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun onTipoPeliculaChanged(tipo: String) {
        _uiState.update { it.copy(tipoPelicula = tipo) }
    }

    fun onTipoSubstratoChanged(tipo: String) {
        _uiState.update { it.copy(tipoSubstrato = tipo) }
    }

    fun onTipoSuporteChanged(tipo: String) {
        _uiState.update { it.copy(tipoSuporte = tipo) }
    }

    // Funções para a seleção do código da placa
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onTempPlateSelected(plate: PlateTypeOption) {
        _tempSelectedPlate.value = plate
    }

    fun confirmPlateSelection() {
        _tempSelectedPlate.value?.let { plate ->
            _uiState.update { it.copy(codigoPlaca = plate.code) }
        }
    }

    fun resetPlateSelection() {
        _searchQuery.value = ""
        _tempSelectedPlate.value = null
    }

    fun submitUpdate(imageFile: File?) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val currentState = _uiState.value

            val result = repository.updatePlaca(
                placaId = currentState.placaId,
                codigoContran = currentState.codigoPlaca,
                tipoPelicula = currentState.tipoPelicula,
                tipoSubstrato = currentState.tipoSubstrato,
                tipoSuporte = currentState.tipoSuporte,
                foto = imageFile
            )

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun resetState() {
        _uiState.update { UpdatePlacaUiState() }
    }
}