package com.nutricore.pro.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nutricore.pro.database.dao.AlimentoConsumoDao
import com.nutricore.pro.database.dao.AtividadeDao
import com.nutricore.pro.database.dao.PerfilUsuarioDao
import com.nutricore.pro.database.entities.AlimentoConsumoEntity
import com.nutricore.pro.database.entities.AtividadeEntity
import com.nutricore.pro.database.entities.PerfilUsuarioEntity

@Database(
    entities = [
        PerfilUsuarioEntity::class,
        AlimentoConsumoEntity::class,
        AtividadeEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NutricoreDatabase : RoomDatabase() {

    abstract fun perfilUsuarioDao(): PerfilUsuarioDao
    abstract fun alimentoConsumoDao(): AlimentoConsumoDao
    abstract fun atividadeDao(): AtividadeDao

    companion object {
        @Volatile
        private var INSTANCE: NutricoreDatabase? = null

        fun getInstance(context: Context): NutricoreDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutricoreDatabase::class.java,
                    "nutricore_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}