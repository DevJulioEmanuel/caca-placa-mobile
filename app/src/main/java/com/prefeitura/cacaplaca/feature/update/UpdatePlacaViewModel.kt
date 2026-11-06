package com.prefeitura.cacaplaca.feature.update

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prefeitura.cacaplaca.feature.registration.data.model.PlacaItemResponse
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
                eventoId = placa.ultimoEvento?.id ?: "",
                codigoPlaca = placa.codigoContran,
                localizacao = localizacaoFormatada,
                estadoPelicula = placa.ultimoEvento?.estadoPelicula ?: "",
                estadoSubstrato = placa.ultimoEvento?.estadoSubstrato ?: "",
                estadoSuporte = placa.ultimoEvento?.estadoSuporte ?: "",
                observacao = placa.ultimoEvento?.observacao ?: ""
            )
        }
    }

    fun onPhotoSelected(uri: Uri?) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun onEstadoPeliculaChanged(estado: String) {
        _uiState.update { it.copy(estadoPelicula = estado) }
    }

    fun onEstadoSubstratoChanged(estado: String) {
        _uiState.update { it.copy(estadoSubstrato = estado) }
    }

    fun onEstadoSuporteChanged(estado: String) {
        _uiState.update { it.copy(estadoSuporte = estado) }
    }

    fun onObservacaoChanged(texto: String) {
        _uiState.update { it.copy(observacao = texto) }
    }

    fun submitUpdate(imageFile: File?) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val currentState = _uiState.value

            val result = repository.updateEventoPlaca(
                placaId = currentState.placaId,
                eventoId = currentState.eventoId,
                estadoConservacao = currentState.estadoConservacao,
                estadoPelicula = currentState.estadoPelicula,
                estadoSubstrato = currentState.estadoSubstrato,
                estadoSuporte = currentState.estadoSuporte,
                observacao = currentState.observacao,
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