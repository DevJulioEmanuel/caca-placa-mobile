package com.prefeitura.cacaplaca.feature.registration.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "placas_delete_sync")
data class PlacaDeleteSyncEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val placaId: String,
    val createdAt: Long = System.currentTimeMillis()
)
