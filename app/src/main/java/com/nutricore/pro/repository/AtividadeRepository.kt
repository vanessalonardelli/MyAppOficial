package com.nutricore.pro.repository

import com.nutricore.pro.database.dao.AtividadeDao
import com.nutricore.pro.database.entities.AtividadeEntity
import java.time.LocalDateTime

class AtividadeRepository(private val dao: AtividadeDao) {

    suspend fun salvar(
        uid: String,
        nome: String,
        duracaoMin: Int,
        met: Double,
        caloriasGastas: Int,
        data: String
    ): Long {
        val atividade = AtividadeEntity(
            uid = uid,
            nome = nome,
            duracaoMin = duracaoMin,
            met = met,
            caloriasGastas = caloriasGastas,
            data = data,
            criadoEm = LocalDateTime.now().toString()
        )
        return dao.inserir(atividade)
    }

    suspend fun buscarPorData(uid: String, data: String): List<AtividadeEntity> {
        return dao.buscarPorData(uid, data)
    }

    suspend fun buscarTodas(uid: String): List<AtividadeEntity> {
        return dao.buscarTodas(uid)
    }

    suspend fun totalCaloriasGastasDia(uid: String, data: String): Int {
        return dao.totalCaloriasGastasDia(uid, data) ?: 0
    }

    suspend fun deletar(id: Int, uid: String) {
        dao.deletar(id, uid)
    }

    suspend fun deletarPorData(uid: String, data: String) {
        dao.deletarPorData(uid, data)
    }
}