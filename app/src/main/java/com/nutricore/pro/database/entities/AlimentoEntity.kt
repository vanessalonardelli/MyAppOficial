package com.nutricore.pro.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alimentos_consumo",
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
data class AlimentoConsumoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String,
    val nome: String,
    val quantidade: Double,
    val calorias: Int,
    val proteina: Double,
    val carboidrato: Double,
    val gordura: Double,
    val refeicao: String,
    val horario: String,
    val data: String,
    val criadoEm: String
)