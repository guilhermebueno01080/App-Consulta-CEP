package com.example.consultacep

import org.json.JSONObject

data class Endereco(
    val cep: String,
    val logradouro: String,
    val complemento: String,
    val bairro: String,
    val cidade: String,
    val uf: String,
    val ddd: String,
)

/** Deixa só os números do CEP digitado. Retorna null se não tiver exatamente 8 dígitos. */
fun limparCep(texto: String): String? {
    val digitos = texto.filter { it.isDigit() }
    return digitos.takeIf { it.length == 8 }
}

/** Lê a resposta do ViaCEP. Retorna null quando o CEP não existe. */
fun lerEndereco(json: String): Endereco? {
    val obj = JSONObject(json)
    if (obj.has("erro")) return null
    return Endereco(
        cep = obj.optString("cep"),
        logradouro = obj.optString("logradouro"),
        complemento = obj.optString("complemento"),
        bairro = obj.optString("bairro"),
        cidade = obj.optString("localidade"),
        uf = obj.optString("uf"),
        ddd = obj.optString("ddd"),
    )
}
