package com.sgmobile.earthquake.core.domain

import com.sgmobile.earthquake.core.domain.models.CountryBounds
import com.sgmobile.earthquake.core.domain.models.MagnitudeThreshold
import com.sgmobile.earthquake.core.domain.usecase.GetUsgsEarthquakeStartTimeUseCase
import com.sgmobile.earthquake.core.domain.usecase.RefreshUsgsEarthquakesUseCase
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
