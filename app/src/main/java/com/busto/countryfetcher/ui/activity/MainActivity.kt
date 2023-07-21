package com.busto.countryfetcher.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.busto.countryfetcher.R
import com.busto.countryfetcher.ui.fragment.CountryFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CountryFragment())
            .commit()
    }
}
