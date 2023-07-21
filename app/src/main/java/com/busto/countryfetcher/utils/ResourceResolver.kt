package com.busto.countryfetcher.utils

import android.content.Context

class ResourceResolver constructor (private val context: Context) {
    fun getString(resId: Int): String {
        return context.getString(resId)
    }
}
