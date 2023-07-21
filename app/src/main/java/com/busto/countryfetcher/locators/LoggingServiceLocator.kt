package com.busto.countryfetcher.locators

import com.busto.countryfetcher.utils.Logger

object LoggingServiceLocator {

    fun providesLogger() = Logger()
}