package com.example.consultacep

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EnderecoTest {

    @Test
    fun aceitaCepComOuSemTraco() {
        assertEquals("01001000", limparCep("01001-000"))
        assertEquals("01001000", limparCep("01001000"))
    }

    @Test
    fun recusaCepComTamanhoErrado() {
        assertNull(limparCep("0100100"))
        assertNull(limparCep(""))
    }
}
