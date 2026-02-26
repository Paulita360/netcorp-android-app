package net.iesochoa.paulaboixvilella.tfgv1.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contact",
    indices = [Index(value = ["firebaseUid"], unique = true)]
)
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firebaseUid: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String
)



