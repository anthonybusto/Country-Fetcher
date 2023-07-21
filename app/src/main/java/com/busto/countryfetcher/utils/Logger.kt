package com.busto.countryfetcher.utils

import android.util.Log

class Logger {

    fun error(tag: String? = null, t: Throwable) {
        Log.e(tag, t.message, t)
    }
}