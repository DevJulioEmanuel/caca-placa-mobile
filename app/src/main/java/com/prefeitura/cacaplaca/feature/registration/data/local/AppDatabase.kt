package com.prefeitura.cacaplaca.feature.registration.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prefeitura.cacaplaca.feature.registration.data.local.dao.PlacaDao
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.CachedPlacaEntity
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaSyncEntity
import com.prefeitura.cacaplaca.feature.update.data.local.entity.PlacaUpdateSyncEntity

@Database(
    entities = [
        PlacaSyncEntity::class, 
        CachedPlacaEntity::class, 
        PlacaUpdateSyncEntity::class
    ], 
    version = 3, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placaDao(): PlacaDao
}
