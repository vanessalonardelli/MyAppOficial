package com.nutricore.pro.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutricore.pro.database.entities.AtividadeEntity

@Dao
interface AtividadeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(atividade: AtividadeEntity): Long

    @Query("SELECT * FROM atividades WHERE uid = :uid AND data = :data ORDER BY criadoEm ASC")
    suspend fun buscarPorData(uid: String, data: String): List<AtividadeEntity>

    @Query("SELECT * FROM atividades WHERE uid = :uid ORDER BY data DESC")
    suspend fun buscarTodas(uid: String): List<AtividadeEntity>

    @Query("SELECT SUM(caloriasGastas) FROM atividades WHERE uid = :uid AND data = :data")
    suspend fun totalCaloriasGastasDia(uid: String, data: String): Int?

    @Query("DELETE FROM atividades WHERE id = :id AND uid = :uid")
    suspend fun deletar(id: Int, uid: String)

    @Query("DELETE FROM atividades WHERE uid = :uid AND data = :data")
    suspend fun deletarPorData(uid: String, data: String)
}