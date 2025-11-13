package com.example.miniproyecto

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * @param entities Lista de las clases (tablas) que forman parte de la base de datos.
 * @param version Número de versión de la base de datos. Debe incrementarse
 */
@Database(entities = [Product::class], version = 2) // Versión incrementada a 2
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * @param context Contexto de la aplicación.
         * @return La instancia de AppDatabase.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "product_database" // Nombre del archivo de la base de datos
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}