package com.prefeitura.cacaplaca.feature.registration.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.CachedPlacaEntity
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaSyncEntity

@Dao
interface PlacaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacaSync(placa: PlacaSyncEntity)

    @Query("SELECT * FROM placas_sync ORDER BY createdAt ASC")
    suspend fun getPendingPlacas(): List<PlacaSyncEntity>

    @Query("DELETE FROM placas_sync WHERE id = :id")
    suspend fun deletePlacaSync(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedPlacas(placas: List<CachedPlacaEntity>)

    @Query("SELECT * FROM cached_placas")
    suspend fun getCachedPlacas(): List<CachedPlacaEntity>

    @Query("DELETE FROM cached_placas")
    suspend fun clearCachedPlacas()
}
