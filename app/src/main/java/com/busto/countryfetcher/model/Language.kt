package com.busto.countryfetcher.model


data class Language(
    val code: String,
    val iso639_2: String? = null,
    val name: String,
    val nativeName: String? = null
)