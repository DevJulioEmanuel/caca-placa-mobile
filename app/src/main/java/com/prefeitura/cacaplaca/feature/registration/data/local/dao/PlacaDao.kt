package com.prefeitura.cacaplaca.feature.registration.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.CachedPlacaEntity
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaDeleteSyncEntity
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaEventDeleteSyncEntity
import com.prefeitura.cacaplaca.feature.registration.data.local.entity.PlacaSyncEntity
import com.prefeitura.cacaplaca.feature.update.data.local.entity.PlacaUpdateSyncEntity

@Dao
interface PlacaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacaSync(placa: PlacaSyncEntity)

    @Query("SELECT * FROM placas_sync ORDER BY createdAt ASC")
    suspend fun getPendingPlacas(): List<PlacaSyncEntity>

    @Query("DELETE FROM placas_sync WHERE id = :id")
    suspend fun deletePlacaSync(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacaUpdateSync(placa: PlacaUpdateSyncEntity)

    @Query("SELECT * FROM placas_update_sync ORDER BY createdAt ASC")
    suspend fun getPendingUpdatePlacas(): List<PlacaUpdateSyncEntity>

    @Query("DELETE FROM placas_update_sync WHERE id = :id")
    suspend fun deletePlacaUpdateSync(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedPlacas(placas: List<CachedPlacaEntity>)

    @Query("SELECT * FROM cached_placas")
    suspend fun getCachedPlacas(): List<CachedPlacaEntity>

    @Query("DELETE FROM cached_placas")
    suspend fun clearCachedPlacas()

    @Query("DELETE FROM cached_placas WHERE id = :placaId")
    suspend fun deleteCachedPlaca(placaId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacaDeleteSync(entity: PlacaDeleteSyncEntity)

    @Query("SELECT * FROM placas_delete_sync ORDER BY createdAt ASC")
    suspend fun getPendingDeletions(): List<PlacaDeleteSyncEntity>

    @Query("DELETE FROM placas_delete_sync WHERE id = :id")
    suspend fun deletePlacaDeleteSync(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacaEventDeleteSync(entity: PlacaEventDeleteSyncEntity)

    @Query("SELECT * FROM placas_event_delete_sync ORDER BY createdAt ASC")
    suspend fun getPendingEventDeletions(): List<PlacaEventDeleteSyncEntity>

    @Query("DELETE FROM placas_event_delete_sync WHERE id = :id")
    suspend fun deletePlacaEventDeleteSync(id: Int)
}
