package net.iesochoa.paulaboixvilella.tfgv1.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val surname: String,
    val phone: String,
    val address: String,
    val rol: String = "user",
    val profileImageUrl: String = ""
)

