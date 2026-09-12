package com.aura.ai.dj.selection

import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BpmMatcherTest {
    private lateinit var bpmMatcher: BpmMatcher

    @Before
    fun setup() {
        bpmMatcher = BpmMatcher()
    }

    @Test
    fun testFindCompatibleTracks() {
        val tracks = listOf(
            com.aura.ai.dj.domain.Track(
                id = "1", title = "T1", artist = "A", duration = 240000,
                bpm = 120, energy = 5
            ),
            com.aura.ai.dj.domain.Track(
                id = "2", title = "T2", artist = "A", duration = 240000,
                bpm = 125, energy = 5
            ),
            com.aura.ai.dj.domain.Track(
                id = "3", title = "T3", artist = "A", duration = 240000,
                bpm = 150, energy = 5
            )
        )

        val compatible = bpmMatcher.findCompatibleTracks(120, tracks, tolerance = 10)
        assertEquals(2, compatible.size)
    }

    @Test
    fun testCanTransition() {
        assertTrue(bpmMatcher.canTransition(120, 125, tolerance = 10))
        assertFalse(bpmMatcher.canTransition(120, 150, tolerance = 10))
    }

    @Test
    fun testCalculateBpmRamp() {
        val ramp = bpmMatcher.calculateBpmRamp(120, 140, 5)
        assertEquals(5, ramp.size)
        assertEquals(120, ramp[0])
        assertEquals(140, ramp[4])
        assertTrue(ramp.zipWithNext().all { (a, b) -> b >= a })
    }
}
