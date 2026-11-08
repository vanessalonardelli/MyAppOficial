package com.nutricore.pro.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.nutricore.pro.database.entities.PerfilUsuarioEntity

@Dao
interface PerfilUsuarioDao {

    @Upsert
    suspend fun salvar(perfil: PerfilUsuarioEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun criarSeNaoExistir(perfil: PerfilUsuarioEntity)

    @Query("SELECT * FROM perfil_usuario WHERE uid = :uid LIMIT 1")
    suspend fun buscarPorUid(uid: String): PerfilUsuarioEntity?

    @Query("DELETE FROM perfil_usuario WHERE uid = :uid")
    suspend fun deletar(uid: String)
}