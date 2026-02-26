package net.iesochoa.paulaboixvilella.tfgv1.data.db

import android.content.Context
import net.iesochoa.paulaboixvilella.tfgv1.data.dao.EventDao

object DatabaseProvider {
    private var database: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        return database ?: AppDatabase.getInstance(context).also { database = it }
    }

    fun provideEventDao(context: Context): EventDao {
        return provideDatabase(context).eventDao()
    }
}

