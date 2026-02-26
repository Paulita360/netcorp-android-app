package net.iesochoa.paulaboixvilella.tfgv1.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventEntity
import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventType

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<EventEntity>)

    @Query("SELECT * FROM events ORDER BY timestamp ASC")
    fun observeAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE type = :type ORDER BY timestamp ASC")
    fun observeByType(type: EventType): Flow<List<EventEntity>>

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM events")
    suspend fun getAllEventsOnce(): List<EventEntity>
}


