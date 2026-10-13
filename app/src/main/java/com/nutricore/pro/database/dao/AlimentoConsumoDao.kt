package com.nutricore.pro.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nutricore.pro.database.entities.AlimentoConsumoEntity

@Dao
interface AlimentoConsumoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(alimento: AlimentoConsumoEntity): Long

    @Query("SELECT * FROM alimentos_consumo WHERE uid = :uid AND data = :data ORDER BY horario ASC")
    suspend fun buscarPorData(uid: String, data: String): List<AlimentoConsumoEntity>

    @Query("SELECT * FROM alimentos_consumo WHERE uid = :uid ORDER BY data DESC, horario DESC")
    suspend fun buscarTodos(uid: String): List<AlimentoConsumoEntity>

    @Query("SELECT SUM(calorias) FROM alimentos_consumo WHERE uid = :uid AND data = :data")
    suspend fun totalCaloriasDia(uid: String, data: String): Int?

    @Query("DELETE FROM alimentos_consumo WHERE id = :id AND uid = :uid")
    suspend fun deletar(id: Int, uid: String)

    @Query("DELETE FROM alimentos_consumo WHERE uid = :uid AND data = :data")
    suspend fun deletarPorData(uid: String, data: String)
}