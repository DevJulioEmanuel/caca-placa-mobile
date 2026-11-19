package com.prefeitura.cacaplaca.feature.profile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1, // Fixado como 1, pois só existe o perfil do usuário logado localmente
    val remoteId: Int,
    val nome: String,
    val email: String,
    val role: String,
    val createdAt: String?
)
