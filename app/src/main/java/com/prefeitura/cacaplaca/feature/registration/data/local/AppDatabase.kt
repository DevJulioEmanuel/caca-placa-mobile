package com.prefeitura.cacaplaca.feature.registration.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prefeitura.cacaplaca.feature.registration.data.local.dao.PlacaDao
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.CachedPlacaEntity
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaDeleteSyncEntity
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaEventDeleteSyncEntity
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaSyncEntity
import com.prefeitura.cacaplaca.feature.update.data.local.entity.PlacaUpdateSyncEntity

@Database(
    entities = [
        PlacaSyncEntity::class,
        CachedPlacaEntity::class,
        PlacaDeleteSyncEntity::class,
        PlacaEventDeleteSyncEntity::class,
        PlacaUpdateSyncEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placaDao(): PlacaDao
}
