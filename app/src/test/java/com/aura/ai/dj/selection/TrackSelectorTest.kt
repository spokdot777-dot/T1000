package com.aura.ai.dj.selection

import com.aura.ai.dj.domain.DjMode
import com.aura.ai.dj.domain.Track
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TrackSelectorTest {
    private lateinit var trackSelector: TrackSelector
    private lateinit var testTracks: List<Track>

    @Before
    fun setup() {
        trackSelector = TrackSelector()
        testTracks = listOf(
            Track(
                id = "1",
                title = "Track 1",
                artist = "Artist 1",
                duration = 240000,
                bpm = 120,
                energy = 7,
                genre = "House"
            ),
            Track(
                id = "2",
                title = "Track 2",
                artist = "Artist 2",
                duration = 240000,
                bpm = 130,
                energy = 8,
                genre = "House"
            ),
            Track(
                id = "3",
                title = "Track 3",
                artist = "Artist 3",
                duration = 240000,
                bpm = 90,
                energy = 3,
                genre = "Lounge"
            )
        )
    }

    @Test
    fun testSelectByVibe() {
        val currentTrack = testTracks[0] // energy = 7
        val selected = trackSelector.selectNextTrack(
            currentTrack,
            testTracks.filter { it.id != currentTrack.id },
            DjMode.CURRENT_VIBE
        )

        // Should select track closest to current energy
        assertTrue(selected != null)
        assertTrue(selected!!.energy >= 6) // Close to 7
    }

    @Test
    fun testSelectByEnergyUp() {
        val currentTrack = testTracks[0] // energy = 7
        val selected = trackSelector.selectNextTrack(
            currentTrack,
            testTracks.filter { it.id != currentTrack.id },
            DjMode.ENERGY_UP
        )

        // Should select track with higher energy
        assertTrue(selected != null)
        assertTrue(selected!!.energy > currentTrack.energy)
    }

    @Test
    fun testSelectByEnergyDown() {
        val currentTrack = testTracks[1] // energy = 8
        val selected = trackSelector.selectNextTrack(
            currentTrack,
            testTracks.filter { it.id != currentTrack.id },
            DjMode.ENERGY_DOWN
        )

        // Should select track with lower energy
        assertTrue(selected != null)
        assertTrue(selected!!.energy < currentTrack.energy)
    }

    @Test
    fun testWeddingTrackSelection() {
        val selected = trackSelector.selectNextTrack(
            testTracks[0],
            testTracks,
            DjMode.WEDDING_BACKGROUND
        )

        // Wedding tracks should have moderate energy (3-6)
        assertTrue(selected != null)
        assertTrue(selected!!.energy in 3..6)
    }
}
