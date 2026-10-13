package com.nutricore.pro.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nutricore.pro.database.entities.PerfilUsuarioEntity

@Dao
interface PerfilUsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirOuAtualizar(perfil: PerfilUsuarioEntity)

    @Update
    suspend fun atualizar(perfil: PerfilUsuarioEntity)

    @Query("SELECT * FROM perfil_usuario WHERE uid = :uid LIMIT 1")
    suspend fun buscarPorUid(uid: String): PerfilUsuarioEntity?

    @Query("DELETE FROM perfil_usuario WHERE uid = :uid")
    suspend fun deletar(uid: String)
}