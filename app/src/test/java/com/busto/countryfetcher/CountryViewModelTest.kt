package com.busto.countryfetcher

import androidx.lifecycle.SavedStateHandle
import com.busto.countryfetcher.model.Country
import com.busto.countryfetcher.model.Currency
import com.busto.countryfetcher.model.Language
import com.busto.countryfetcher.repository.CountryRepository
import com.busto.countryfetcher.utils.CoroutineScheduler
import com.busto.countryfetcher.utils.Logger
import com.busto.countryfetcher.utils.ResourceResolver
import com.busto.countryfetcher.viewmodel.CountryListViewModel
import com.busto.countryfetcher.viewmodel.CountryListViewModelFactory
import com.busto.countryfetcher.viewmodel.CountryListViewState
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class CountryViewModelTest {

    // Set up test rule to execute operations synchronously
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule() //InstantTaskExecutorRule

    // Create a mocks
    private val mockRepository: CountryRepository = mockk()
    private val mockResourceResolver: ResourceResolver = mockk(relaxed = true)
    private val mockCoroutineScheduler: CoroutineScheduler = mockk()
    private val mockLogger: Logger = mockk(relaxed = true)

    @OptIn(DelicateCoroutinesApi::class)
    private val testingThread = newSingleThreadContext("vm_thread")

    private val fakeSavedStateHandle = SavedStateHandle()

    private val testSubject: CountryListViewModel by lazy {
        CountryListViewModelFactory(
            mockRepository,
            mockResourceResolver,
            mockCoroutineScheduler, // Provide the test dispatcher to the factory
            mockLogger,
            fakeSavedStateHandle
        ).create(CountryListViewModel::class.java)
    }


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

    @Before
    fun setup() {
        every { mockCoroutineScheduler.io() } returns Dispatchers.Unconfined
    }

    @Test
    fun `when start is called and we successfully fetch countries, then render the 'Content' state`() =
        runTest {
            // Set up mock data and behavior
            val screenTitle = "Countries List"

            coEvery { mockRepository.getCountries() } coAnswers { fakeCountriesList }
            every { mockResourceResolver.getString(R.string.country_list_title) } returns screenTitle

            // Collect the emitted states using collect
            val states = mutableListOf<CountryListViewState>()

            val job = launch {
                testSubject.viewStateFlow.collect { state ->
                    states.add(state)
                }
            }

            // Start the view model
            withContext(testingThread) {
                testSubject.start()
            }

            // Advance the coroutine dispatcher until all work is done
            advanceUntilIdle()

            // Assert the loading state is emitted first
            assert(states[0] is CountryListViewState.Loading) {
                "Invalid state! Should be Loading but is is ${states[0]}"
            }

            // Assert the content state with the expected list of countries is emitted next
            val contentState = states[1] as CountryListViewState.Content
            assert(fakeCountriesList == contentState.countries) {
                "Countries didn't match!"
            }

            //Verify that we used the ResourceResolver to get the country_list_title
            verify { mockResourceResolver.getString(R.string.country_list_title) }

            //Assert we set the correct title
            assert(screenTitle == contentState.title) {
                "Titles didn't match!"
            }

            //Assert that only the loading and content state was emitted and nothing else
            assert(states.size == 2) {
                "There should have only been two and only two states emitted but states.size = ${states.size}"
            }

            // Cancel the job
            job.cancel()
        }

    @Test
    fun `when start is called and we successfully fetch an empty countries list, then render the 'Empty' state`() =
        runTest {

            val screenTitle = "Countries List"

            // Set up mock data and behavior
            val emptyCountryList = emptyList<Country>()
            coEvery { mockRepository.getCountries() } coAnswers { emptyCountryList }
            every { mockResourceResolver.getString(R.string.country_list_title) } returns screenTitle

            // Collect the emitted states using collect
            val states = mutableListOf<CountryListViewState>()
            val job = launch {
                testSubject.viewStateFlow.collect { state ->
                    states.add(state)
                }
            }

            // Start the view model
            withContext(testingThread) {
                testSubject.start()
            }

            // Advance the coroutine dispatcher until all work is done
            advanceUntilIdle()

            // Assert the loading state is emitted first
            assert(states[0] is CountryListViewState.Loading) {
                "Invalid state! Should be Loading but is is ${states[0]}"
            }

            // Assert the content state with the empty list is emitted
            val contentState = states[1] as CountryListViewState.Content
            assert(contentState.countries.isEmpty()) {
                "The list of countries in the content state should be empty but size = ${contentState.countries.size}"
            }

            //Verify that we used the ResourceResolver to get the country_list_title
            verify { mockResourceResolver.getString(R.string.country_list_title) }

            //Assert we set the correct title
            assert(screenTitle == contentState.title) {
                "Titles didn't match!"
            }

            //Assert that only the loading and empty state was emitted and nothing else
            assert(states.size == 2) {
                "There should have only been two and only two states emitted but states.size = ${states.size}"
            }

            // Cancel the job
            job.cancel()
        }

    @Test
    fun `when start is called and we fail to fetch countries, then render the 'Error' state`() =
        runTest {
            // Set up mock error
            val mockError = Exception("Error fetching countries")
            coEvery { mockRepository.getCountries() } throws mockError

            // Collect the emitted states using collect
            val states = mutableListOf<CountryListViewState>()
            val job = launch {
                testSubject.viewStateFlow.collect { state ->
                    states.add(state)
                }
            }

            // Start the view model
            withContext(testingThread) {
                testSubject.start()
            }

            // Advance the coroutine dispatcher until all work is done
            advanceUntilIdle()

            // Assert the loading state is emitted first
            assert(states[0] is CountryListViewState.Loading) {
                "Invalid state! Should be Loading but is is ${states[0]}"
            }

            // Assert the error state with the expected exception is emitted
            val errorState = states[1] as CountryListViewState.Error

            assert(states[1] is CountryListViewState.Error) {
                "Invalid state! Should be Error but is is ${states[1]}"
            }

            assert(mockError.message == errorState.t.message) {
                "The error messages do not match!"
            }

            //Assert that only the loading and error state was emitted and nothing else
            assert(states.size == 2) {
                "There should have only been two and only two states emitted but states.size = ${states.size}"
            }

            //Verify that we logged the error from the Error state after the exception is thrown
            verify { mockLogger.error(t = errorState.t) }

            // Cancel the job
            job.cancel()
        }


}


@ExperimentalCoroutinesApi
class MainCoroutineRule : TestWatcher() {

    private val testDispatcher = UnconfinedTestDispatcher()

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

