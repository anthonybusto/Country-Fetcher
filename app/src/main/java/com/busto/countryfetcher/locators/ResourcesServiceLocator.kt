package com.busto.countryfetcher.locators

import android.app.Application
import com.busto.countryfetcher.utils.ResourceResolver
import java.lang.IllegalStateException

object ResourcesServiceLocator {

    private lateinit var application: Application

    fun register(application: Application) {
        this.application = application
    }

    fun providesResourceResolver(): ResourceResolver {
        if(!::application.isInitialized){
            throw IllegalStateException("Your resources service locator never registered its Application reference!")
        }
        return ResourceResolver(application)
    }
}