package com.example.consultacep

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/** Consulta a API gratuita do ViaCEP (https://viacep.com.br). */
object ViaCep {

    /** Retorna o endereço, ou null se o CEP não existe. Lança exceção se não tiver internet. */
    suspend fun buscar(cep: String): Endereco? = withContext(Dispatchers.IO) {
        val conexao = URL("https://viacep.com.br/ws/$cep/json/").openConnection() as HttpURLConnection
        try {
            conexao.connectTimeout = 10_000
            conexao.readTimeout = 10_000
            val json = conexao.inputStream.bufferedReader().use { it.readText() }
            lerEndereco(json)
        } finally {
            conexao.disconnect()
        }
    }
}
