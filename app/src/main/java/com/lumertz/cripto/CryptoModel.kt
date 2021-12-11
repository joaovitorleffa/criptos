package com.lumertz.cripto

import java.io.Serializable

const val CRYPTO_MODEL_KEY = "CRYPTO_MODEL"

class CryptoModel (
    val name: String,
    val code: String
): Serializable { }