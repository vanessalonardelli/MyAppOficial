package com.nutricore.pro.repository

import com.nutricore.pro.database.dao.PerfilUsuarioDao
import com.nutricore.pro.database.entities.PerfilUsuarioEntity
import java.time.LocalDateTime

class PerfilRepository(private val dao: PerfilUsuarioDao) {

    suspend fun salvar(
        uid: String,
        nome: String,
        peso: Double,
        altura: Int,
        idade: Int,
        sexo: String,
        nivelAtividade: Double,
        objetivo: String,
        tmb: Int,
        tdee: Int,
        metaCalorias: Int
    ) {
        val agora = LocalDateTime.now().toString()
        val perfil = PerfilUsuarioEntity(
            uid = uid,
            nome = nome,
            peso = peso,
            altura = altura,
            idade = idade,
            sexo = sexo,
            nivelAtividade = nivelAtividade,
            objetivo = objetivo,
            tmb = tmb,
            tdee = tdee,
            metaCalorias = metaCalorias,
            criadoEm = agora,
            atualizadoEm = agora
        )
        dao.inserirOuAtualizar(perfil)
    }

    suspend fun buscar(uid: String): PerfilUsuarioEntity? {
        return dao.buscarPorUid(uid)
    }

    suspend fun deletar(uid: String) {
        dao.deletar(uid)
    }
}