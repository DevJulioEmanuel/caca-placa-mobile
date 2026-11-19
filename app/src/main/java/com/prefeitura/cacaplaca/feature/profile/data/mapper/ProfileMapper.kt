package com.prefeitura.cacaplaca.feature.profile.data.mapper

import com.prefeitura.cacaplaca.feature.profile.data.local.entity.UserProfileEntity
import com.prefeitura.cacaplaca.feature.profile.data.model.ProfileUiModel
import com.prefeitura.cacaplaca.feature.profile.data.model.UserProfileResponse

fun UserProfileResponse.toEntity(): UserProfileEntity {
    return UserProfileEntity(
        remoteId = this.id,
        nome = this.nome,
        email = this.email,
        role = this.role,
        createdAt = this.createdAt
    )
}

fun UserProfileEntity.toUiModel(): ProfileUiModel {
    val dateFormatted = createdAt?.let {
        if (it.length >= 10) {
            val parts = it.substring(0, 10).split("-")
            if (parts.size == 3) {
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } else {
                it
            }
        } else {
            it
        }
    } ?: "Indisponível"

    return ProfileUiModel(
        id = this.remoteId,
        nome = this.nome,
        email = this.email,
        role = formatRole(this.role),
        dataCadastro = dateFormatted
    )
}

fun UserProfileResponse.toUiModel(): ProfileUiModel {
    val dateFormatted = createdAt?.let {
        if (it.length >= 10) {
            val parts = it.substring(0, 10).split("-")
            if (parts.size == 3) {
                "${parts[2]}/${parts[1]}/${parts[0]}"
            } else {
                it
            }
        } else {
            it
        }
    } ?: "Indisponível"

    return ProfileUiModel(
        id = this.id,
        nome = this.nome,
        email = this.email,
        role = formatRole(this.role),
        dataCadastro = dateFormatted
    )
}

private fun formatRole(role: String): String {
    return when(role.lowercase()) {
        "admin" -> "Administrador"
        "agente" -> "Agente"
        else -> role.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}
