package com.example.consultacep

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var campoCep: TextInputLayout
    private lateinit var edCep: TextInputEditText
    private lateinit var btBuscar: MaterialButton
    private lateinit var carregando: CircularProgressIndicator
    private lateinit var cartao: MaterialCardView
    private lateinit var txLogradouro: TextView
    private lateinit var txBairro: TextView
    private lateinit var txCidade: TextView
    private lateinit var txCep: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        campoCep = findViewById(R.id.campo_cep)
        edCep = findViewById(R.id.ed_cep)
        btBuscar = findViewById(R.id.bt_buscar)
        carregando = findViewById(R.id.carregando)
        cartao = findViewById(R.id.cartao_endereco)
        txLogradouro = findViewById(R.id.tx_logradouro)
        txBairro = findViewById(R.id.tx_bairro)
        txCidade = findViewById(R.id.tx_cidade)
        txCep = findViewById(R.id.tx_cep)

        btBuscar.setOnClickListener { buscar() }
        edCep.setOnEditorActionListener { _, acao, _ ->
            if (acao == EditorInfo.IME_ACTION_SEARCH) buscar()
            acao == EditorInfo.IME_ACTION_SEARCH
        }
    }

    private fun buscar() {
        val cep = limparCep(edCep.text?.toString().orEmpty())
        if (cep == null) {
            campoCep.error = getString(R.string.erro_cep_invalido)
            return
        }
        campoCep.error = null
        esconderTeclado()
        mostrarCarregando(true)

        lifecycleScope.launch {
            val resultado = runCatching { ViaCep.buscar(cep) }
            mostrarCarregando(false)
            resultado
                .onSuccess { endereco ->
                    if (endereco == null) {
                        campoCep.error = getString(R.string.erro_cep_nao_encontrado)
                    } else {
                        mostrarEndereco(endereco)
                    }
                }
                .onFailure { campoCep.error = getString(R.string.erro_sem_internet) }
        }
    }

    private fun mostrarEndereco(endereco: Endereco) {
        val rua = listOf(endereco.logradouro, endereco.complemento).filter { it.isNotBlank() }
        txLogradouro.text = rua.joinToString(" — ").ifBlank { getString(R.string.cep_geral) }
        txBairro.text = endereco.bairro
        txBairro.isVisible = endereco.bairro.isNotBlank()
        txCidade.text = getString(R.string.cidade_uf, endereco.cidade, endereco.uf)
        txCep.text = getString(R.string.cep_ddd, endereco.cep, endereco.ddd)
        cartao.isVisible = true
    }

    private fun mostrarCarregando(sim: Boolean) {
        carregando.isVisible = sim
        btBuscar.isEnabled = !sim
        if (sim) cartao.isVisible = false
    }

    private fun esconderTeclado() {
        val teclado = getSystemService(InputMethodManager::class.java)
        teclado.hideSoftInputFromWindow(edCep.windowToken, 0)
        edCep.clearFocus()
    }
}
