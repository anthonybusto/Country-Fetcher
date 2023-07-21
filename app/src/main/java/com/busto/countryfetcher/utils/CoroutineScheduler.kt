package com.busto.countryfetcher.utils

import kotlinx.coroutines.Dispatchers

class CoroutineScheduler {
    fun io() = Dispatchers.IO
    fun main() = Dispatchers.Main
    fun default() = Dispatchers.Default
}