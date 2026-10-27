package com.prefeitura.cacaplaca.feature.registration.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.prefeitura.cacaplaca.feature.registration.data.local.dao.PlacaDao
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaSyncEntity

@Database(entities = [PlacaSyncEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placaDao(): PlacaDao
}
