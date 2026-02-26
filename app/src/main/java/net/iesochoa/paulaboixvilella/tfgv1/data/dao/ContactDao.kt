package net.iesochoa.paulaboixvilella.tfgv1.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.iesochoa.paulaboixvilella.tfgv1.data.model.ContactEntity

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact")
    suspend fun getAllContacts(): List<ContactEntity>

    @Query("SELECT * FROM contact WHERE email = :email LIMIT 1")
    suspend fun getContactByEmail(email: String): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Delete
    suspend fun deleteContact(contact: ContactEntity)
}

