package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetUsgsEarthquakeStartTimeUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.RefreshUsgsEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.CountryBounds
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RefreshUsgsEarthquakesUseCaseTest {

    @Test
    fun invokeShouldForwardSelectedCountryBoundsToRepository() = runBlocking {
        // Given
        val repository = FakeEarthquakeRepository()
        val useCase = RefreshUsgsEarthquakesUseCase(
            earthquakeRepository = repository,
            getUsgsEarthquakeStartTimeUseCase = GetUsgsEarthquakeStartTimeUseCase(),
        )
        val countryBounds = CountryBounds(
            minLongitude = 26.04,
            minLatitude = 35.82,
            maxLongitude = 44.79,
            maxLatitude = 42.14,
        )

        // When
        useCase(
            pageSize = 25,
            selectedMagnitude = MagnitudeThreshold.FOUR_PLUS,
            selectedCountryBounds = countryBounds,
        )

        // Then
        assertEquals(25, repository.refreshedPageSize)
        assertEquals(MagnitudeThreshold.FOUR_PLUS, repository.refreshedMagnitude)
        assertEquals(countryBounds, repository.refreshedCountryBounds)
    }
}
