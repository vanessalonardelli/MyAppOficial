package com.nutricore.pro.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "atividades",
    foreignKeys = [
        ForeignKey(
            entity = PerfilUsuarioEntity::class,
            parentColumns = ["uid"],
            childColumns = ["uid"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["uid"]),
        Index(value = ["data"]),
        Index(value = ["uid", "data"])
    ]
)
data class AtividadeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String,
    val nome: String,
    val duracaoMin: Int,
    val met: Double,
    val caloriasGastas: Int,
    val data: String,
    val criadoEm: String
)