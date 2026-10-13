package com.nutricore.pro.repository

import com.nutricore.pro.database.dao.AlimentoConsumoDao
import com.nutricore.pro.database.entities.AlimentoConsumoEntity
import java.time.LocalDateTime

class AlimentoRepository(private val dao: AlimentoConsumoDao) {

    suspend fun salvar(
        uid: String,
        nome: String,
        quantidade: Double,
        calorias: Int,
        proteina: Double,
        carboidrato: Double,
        gordura: Double,
        refeicao: String,
        horario: String,
        data: String
    ): Long {
        val alimento = AlimentoConsumoEntity(
            uid = uid,
            nome = nome,
            quantidade = quantidade,
            calorias = calorias,
            proteina = proteina,
            carboidrato = carboidrato,
            gordura = gordura,
            refeicao = refeicao,
            horario = horario,
            data = data,
            criadoEm = LocalDateTime.now().toString()
        )
        return dao.inserir(alimento)
    }

    suspend fun buscarPorData(uid: String, data: String): List<AlimentoConsumoEntity> {
        return dao.buscarPorData(uid, data)
    }

    suspend fun buscarTodos(uid: String): List<AlimentoConsumoEntity> {
        return dao.buscarTodos(uid)
    }

    suspend fun totalCaloriasDia(uid: String, data: String): Int {
        return dao.totalCaloriasDia(uid, data) ?: 0
    }

    suspend fun deletar(id: Int, uid: String) {
        dao.deletar(id, uid)
    }

    suspend fun deletarPorData(uid: String, data: String) {
        dao.deletarPorData(uid, data)
    }
}