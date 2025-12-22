package com.sgmobile.earthquake.feature.earthquake.domain

import com.sgmobile.earthquake.feature.earthquake.domain.models.Earthquake
import com.sgmobile.earthquake.feature.earthquake.domain.models.MagnitudeThreshold
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
    fun `invoke should return earthquake flow from repository`() {
        // Given
        val expectedEarthquakes = listOf(
            Earthquake(
                place = "California",
                magnitude = "4.5",
                magnitudeThreshold = MagnitudeThreshold.FOUR_PLUS,
                depth = "10km",
                date = "2023-10-27"
            )
        )
        fakeEarthquakeRepository.emitEarthquakes(expectedEarthquakes)

        // When
        val resultFlow = getEarthquakeFlowUseCase()

        // Then
        assertEquals(expectedEarthquakes, resultFlow.value)
    }
}
