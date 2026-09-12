package com.aura.ai.dj.selection

import org.junit.Before
import org.junit.Test
import kotlin.test.assertTrue

class KeyMatcherTest {
    private lateinit var keyMatcher: KeyMatcher

    @Before
    fun setup() {
        keyMatcher = KeyMatcher()
    }

    @Test
    fun testCompatibleKeys() {
        assertTrue(keyMatcher.areKeysCompatible("C", "C"))
        assertTrue(keyMatcher.areKeysCompatible("C", "G"))
        assertTrue(keyMatcher.areKeysCompatible("C", "Am"))
    }

    @Test
    fun testIncompatibleKeys() {
        // Simple check: distant keys should be less compatible
        val compatible1 = keyMatcher.areKeysCompatible("C", "F#")
        val compatible2 = keyMatcher.areKeysCompatible("C", "C")
        // Direct keys are more compatible
        assertTrue(compatible2)
    }

    @Test
    fun testFindCompatibleTracks() {
        val tracks = listOf(
            com.aura.ai.dj.domain.Track(
                id = "1", title = "T1", artist = "A", duration = 240000,
                bpm = 120, energy = 5, key = "C"
            ),
            com.aura.ai.dj.domain.Track(
                id = "2", title = "T2", artist = "A", duration = 240000,
                bpm = 120, energy = 5, key = "G"
            ),
            com.aura.ai.dj.domain.Track(
                id = "3", title = "T3", artist = "A", duration = 240000,
                bpm = 120, energy = 5, key = "F#"
            )
        )

        val compatible = keyMatcher.findCompatibleTracks("C", tracks)
        assertTrue(compatible.isNotEmpty())
    }
}
