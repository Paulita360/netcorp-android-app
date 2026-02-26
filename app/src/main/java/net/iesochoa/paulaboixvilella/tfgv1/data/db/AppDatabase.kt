package net.iesochoa.paulaboixvilella.tfgv1.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.iesochoa.paulaboixvilella.tfgv1.data.dao.ContactDao
import net.iesochoa.paulaboixvilella.tfgv1.data.dao.EventDao
import net.iesochoa.paulaboixvilella.tfgv1.data.dao.MessageDao
import net.iesochoa.paulaboixvilella.tfgv1.data.dao.UserProfileDao
import net.iesochoa.paulaboixvilella.tfgv1.data.model.ContactEntity
import net.iesochoa.paulaboixvilella.tfgv1.data.model.EventEntity
import net.iesochoa.paulaboixvilella.tfgv1.data.model.MessageEntity
import net.iesochoa.paulaboixvilella.tfgv1.data.model.UserProfileEntity

@Database(
    entities = [
        ContactEntity::class,
        MessageEntity::class,
        EventEntity::class,
        UserProfileEntity::class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun eventDao(): EventDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


