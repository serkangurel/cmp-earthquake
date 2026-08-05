package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.overview.domain.GetEarthquakeFlowUseCase
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.overview.domain.models.MagnitudeThreshold
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class GetEarthquakeFlowUseCaseTest {

    private lateinit var fakeEarthquakeRepository: FakeEarthquakeRepository
    private lateinit var getEarthquakeFlowUseCase: GetEarthquakeFlowUseCase

    @BeforeTest
    fun setUp() {
        fakeEarthquakeRepository = FakeEarthquakeRepository()
        getEarthquakeFlowUseCase = GetEarthquakeFlowUseCase(fakeEarthquakeRepository)
    }

    @Test
    fun invokeShouldReturnEarthquakeFlowFromRepository() {
        // Given
        val expectedEarthquakes = listOf(
            Earthquake(
                id = "us7000abcd",
                place = "California",
                magnitude = "4.5",
                magnitudeThreshold = MagnitudeThreshold.FOUR_PLUS,
                depth = "10km",
                date = "2023-10-27",
                latitude = 37.7,
                longitude = -122.4
            )
        )
        fakeEarthquakeRepository.emitEarthquakes(expectedEarthquakes)

        // When
        val resultFlow = getEarthquakeFlowUseCase()

        // Then
        assertEquals(expectedEarthquakes, resultFlow.value)
    }
}
