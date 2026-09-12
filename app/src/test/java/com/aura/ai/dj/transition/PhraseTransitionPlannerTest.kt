package com.aura.ai.dj.transition

import com.aura.ai.dj.domain.Track
import com.aura.ai.dj.selection.BpmMatcher
import com.aura.ai.dj.selection.KeyMatcher
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PhraseTransitionPlannerTest {
    private lateinit var planner: PhraseTransitionPlanner
    private lateinit var track1: Track
    private lateinit var track2: Track

    @Before
    fun setup() {
        planner = PhraseTransitionPlanner(BpmMatcher(), KeyMatcher())
        track1 = Track(
            id = "1",
            title = "Track 1",
            artist = "Artist 1",
            duration = 240000,
            bpm = 120,
            key = "C",
            energy = 7,
            intro = 8000,
            outro = 8000
        )
        track2 = Track(
            id = "2",
            title = "Track 2",
            artist = "Artist 2",
            duration = 240000,
            bpm = 122,
            key = "G",
            energy = 7,
            intro = 8000,
            outro = 8000
        )
    }

    @Test
    fun testPlanTransition() {
        val plan = planner.planTransition(track1, track2)

        assertEquals(track1, plan.currentTrack)
        assertEquals(track2, plan.nextTrack)
        assertTrue(plan.crossfadeDuration > 0)
    }

    @Test
    fun testBpmMatchDetection() {
        val plan = planner.planTransition(track1, track2)
        // BPM difference is 2, within tolerance
        assertTrue(plan.bpmMatch)
    }

    @Test
    fun testCalculateOutroOverlapTime() {
        val overlap = planner.calculateOutroOverlapTime(track1, track2)
        assertEquals(8000, overlap)
    }
}
