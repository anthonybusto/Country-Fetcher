package com.busto.countryfetcher

import com.busto.countryfetcher.mapper.CountryMapper
import com.busto.countryfetcher.model.Country
import com.busto.countryfetcher.model.Currency
import com.busto.countryfetcher.model.Language
import com.busto.countryfetcher.ui.renderables.CountryRenderable
import org.junit.Test

class CountryMapperTest {

    private val fakeCountriesList = listOf(
        Country(
            capital = "London",
            code = "GB",
            currency = Currency("GBP", "Brithish pound", "£"),
            flag = "https://restcountries.eu/data/gbr.svg",
            language = Language(
                code = "en",
                name = "English",
            ),
            name = "United States of America",
            region = "NA"
        ),

        Country(
            capital = "Washington, D.C.",
            code = "US",
            currency = Currency("USD", "United States dollar", "$"),
            flag = "https://restcountries.eu/data/usa.svg",
            language = Language(
                code = "en", iso639_2 = "eng", name = "English", nativeName = "English"
            ),
            name = "United States of America",
            region = "NA"
        ),
    )

    private val testSubject: CountryMapper = CountryMapper()

    @Test
    fun `when mapCountryToRenderable is called, then confirm the correct list of CountryRenderable is returned`() {
        val expectedRenderables = listOf(
            CountryRenderable(
                capital = "London",
                code = "GB",
                name = "United States of America",
                region = "NA"
            ),

            CountryRenderable(
                capital = "Washington, D.C.",
                code = "US",
                name = "United States of America",
                region = "NA"
            )
        )


        // Ac
        val mappedRenderables = testSubject.mapCountryToRenderable(fakeCountriesList)

        // Assert
        assert(expectedRenderables == mappedRenderables) {
            "Countries weren't mapped as expected!"
        }
    }

}