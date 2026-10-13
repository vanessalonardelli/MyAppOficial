package com.nutricore.pro.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil_usuario")
data class PerfilUsuarioEntity(
    @PrimaryKey
    val uid: String,
    val nome: String,
    val peso: Double,
    val altura: Int,
    val idade: Int,
    val sexo: String,
    val nivelAtividade: Double,
    val objetivo: String,
    val tmb: Int,
    val tdee: Int,
    val metaCalorias: Int,
    val criadoEm: String,
    val atualizadoEm: String
)