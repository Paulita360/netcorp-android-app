package net.iesochoa.paulaboixvilella.tfgv1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: Long = 0L,
    val importance: Int = 1,
    val participants: List<String> = emptyList(),
    val creatorUid: String = "",
    val type: EventType = EventType.EVENT
) {

    constructor() : this(
        id = "",
        title = "",
        description = "",
        timestamp = 0L,
        importance = 1,
        participants = emptyList(),
        creatorUid = "",
        type = EventType.EVENT
    )
}






