package com.nutricore.pro.repository

import com.nutricore.pro.database.dao.PerfilUsuarioDao
import com.nutricore.pro.database.entities.PerfilUsuarioEntity
import java.time.LocalDateTime

class PerfilRepository(private val dao: PerfilUsuarioDao) {

    suspend fun garantirPerfil(uid: String, nome: String) {
        require(uid.isNotBlank()) { "uid inválido" }
        val agora = LocalDateTime.now().toString()
        dao.criarSeNaoExistir(
            PerfilUsuarioEntity(
                uid = uid,
                nome = nome,
                peso = 0.0,
                altura = 0,
                idade = 0,
                sexo = "",
                nivelAtividade = 1.2,
                objetivo = "maintain",
                tmb = 0,
                tdee = 0,
                metaCalorias = 2000,
                criadoEm = agora,
                atualizadoEm = agora
            )
        )
    }

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
        require(uid.isNotBlank()) { "uid inválido" }
        require(nome.isNotBlank()) { "nome inválido" }
        require(peso > 0 && altura > 0 && idade > 0) { "medidas inválidas" }
        require(sexo == "M" || sexo == "F") { "sexo inválido" }

        val agora = LocalDateTime.now().toString()
        val existente = dao.buscarPorUid(uid)
        dao.salvar(
            PerfilUsuarioEntity(
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
                criadoEm = existente?.criadoEm ?: agora,
                atualizadoEm = agora
            )
        )
    }

    suspend fun buscar(uid: String): PerfilUsuarioEntity? {
        return dao.buscarPorUid(uid)
    }

    suspend fun deletar(uid: String) {
        dao.deletar(uid)
    }
}