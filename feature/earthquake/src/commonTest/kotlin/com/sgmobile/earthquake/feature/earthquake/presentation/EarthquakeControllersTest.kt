package com.sgmobile.earthquake.feature.earthquake.presentation

import com.sgmobile.earthquake.feature.earthquake.detail.presentation.EarthquakeDetailController
import com.sgmobile.earthquake.feature.earthquake.domain.FakeEarthquakeRepository
import com.sgmobile.earthquake.feature.earthquake.overview.domain.CountryRepository
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetCountriesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetIsEndReachedFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetUsgsEarthquakeStartTimeUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.LoadNextUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.RefreshUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Country
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.CountryBounds
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.feature.earthquake.overview.presentation.EarthquakeOverviewController
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

internal class EarthquakeControllersTest {

    @Test
    fun overviewOwnsFilteringRefreshPaginationAndDisplayMapping() = runBlocking {
        val repository = FakeEarthquakeRepository()
        val controller = makeOverviewController(repository)

        try {
            controller.state.first { repository.refreshCount == 1 && !it.isLoading }
            assertEquals("GLOBAL", controller.currentState.selectedCountry?.code)
            assertEquals(MagnitudeThreshold.TWO_PLUS, repository.refreshedMagnitude)

            controller.selectMagnitude(MagnitudeThreshold.FOUR_PLUS)
            controller.state.first { repository.refreshCount == 2 && !it.isLoading }
            assertEquals(MagnitudeThreshold.FOUR_PLUS, controller.currentState.selectedMagnitude)
            assertEquals(MagnitudeThreshold.FOUR_PLUS, repository.refreshedMagnitude)

            controller.selectCountry("TR")
            controller.state.first { repository.refreshCount == 3 && !it.isLoading }
            assertEquals("TR", controller.currentState.selectedCountry?.code)
            assertEquals(TURKEY_BOUNDS, repository.refreshedCountryBounds)

            repository.emitEarthquakes(listOf(EARTHQUAKE))
            controller.state.first { it.earthquakes.size == 1 }
            assertEquals(EARTHQUAKE.id, controller.currentState.earthquakes.single().id)
            assertEquals(EARTHQUAKE.place, controller.currentState.earthquakes.single().place)

            controller.loadMore()
            controller.state.first { repository.loadNextPageCount == 1 && !it.isLoading }
            repository.setEndReached(true)
            controller.state.first { it.isEndReached }
            controller.loadMore()
            delay(50)
            assertEquals(1, repository.loadNextPageCount)
        } finally {
            controller.close()
        }
    }

    @Test
    fun pullToRefreshSignalsCompletionAndClearsRefreshingState() = runBlocking {
        val repository = FakeEarthquakeRepository()
        val controller = makeOverviewController(repository)

        try {
            controller.state.first { repository.refreshCount == 1 && !it.isLoading }
            val completion = CompletableDeferred<Unit>()

            controller.refresh { completion.complete(Unit) }

            withTimeout(5_000) { completion.await() }
            assertEquals(2, repository.refreshCount)
            assertFalse(controller.currentState.isPullToRefresh)
        } finally {
            controller.close()
        }
    }

    @Test
    fun detailTracksTheSelectedEarthquakeFromSharedState() = runBlocking {
        val repository = FakeEarthquakeRepository().apply {
            emitEarthquakes(listOf(EARTHQUAKE))
        }
        val controller = EarthquakeDetailController(
            earthquakeId = EARTHQUAKE.id,
            getEarthquakeFlowUseCase = GetEarthquakeFlowUseCase(repository),
        )

        try {
            val state = controller.state.first { !it.isLoading }
            val earthquake = assertNotNull(state.earthquake)
            assertEquals(EARTHQUAKE.place, earthquake.place)
            assertEquals(EARTHQUAKE.latitude, earthquake.latitude)
            assertEquals(EARTHQUAKE.longitude, earthquake.longitude)
        } finally {
            controller.close()
        }
    }

    private fun makeOverviewController(repository: FakeEarthquakeRepository) =
        EarthquakeOverviewController(
            refreshUsgsEarthquakesUseCase = RefreshUsgsEarthquakesUseCase(
                earthquakeRepository = repository,
                getUsgsEarthquakeStartTimeUseCase = GetUsgsEarthquakeStartTimeUseCase(),
            ),
            loadNextUsgsEarthquakesUseCase = LoadNextUsgsEarthquakesUseCase(repository),
            getEarthquakeFlowUseCase = GetEarthquakeFlowUseCase(repository),
            getIsEndReachedFlowUseCase = GetIsEndReachedFlowUseCase(repository),
            getCountriesUseCase = GetCountriesUseCase(FakeCountryRepository),
        )

    private object FakeCountryRepository : CountryRepository {
        override suspend fun getCountries() = listOf(
            Country(
                code = "GLOBAL",
                name = "Global",
                flagUrl = "",
                bounds = GLOBAL_BOUNDS,
            ),
            Country(
                code = "TR",
                name = "Türkiye",
                flagUrl = "",
                bounds = TURKEY_BOUNDS,
            ),
        )
    }

    private companion object {
        val GLOBAL_BOUNDS = CountryBounds(
            minLongitude = -180.0,
            minLatitude = -90.0,
            maxLongitude = 180.0,
            maxLatitude = 90.0,
        )
        val TURKEY_BOUNDS = CountryBounds(
            minLongitude = 26.04,
            minLatitude = 35.82,
            maxLongitude = 44.79,
            maxLatitude = 42.14,
        )
        val EARTHQUAKE = Earthquake(
            id = "test-earthquake",
            place = "Test Place",
            magnitude = "4.20",
            magnitudeThreshold = MagnitudeThreshold.FOUR_PLUS,
            depth = "8.5 km",
            date = "29.09.2026 00:00",
            latitude = 38.0,
            longitude = 35.0,
        )
    }
}
