package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeStartTimeUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.RefreshEarthquakesUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.CountryBounds
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.EarthquakeSource
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

internal class RefreshEarthquakesUseCaseTest {

    @Test
    fun invokeShouldForwardSelectedCountryBoundsToRepository() = runBlocking {
        // Given
        val repository = FakeEarthquakeRepository()
        val useCase = RefreshEarthquakesUseCase(
            earthquakeRepository = repository,
            getEarthquakeStartTimeUseCase = GetEarthquakeStartTimeUseCase(),
        )
        val countryBounds = CountryBounds(
            minLongitude = 26.04,
            minLatitude = 35.82,
            maxLongitude = 44.79,
            maxLatitude = 42.14,
        )

        // When
        useCase(
            source = EarthquakeSource.USGS,
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
