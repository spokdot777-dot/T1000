package com.aura.ai.voice.wake

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class WakeWordDetectorTest {
    @Mock
    private lateinit var mockContext: Context

    private lateinit var wakeWordDetector: WakeWordDetector

    @Before
    fun setup() {
        wakeWordDetector = WakeWordDetector(mockContext)
    }

    @Test
    fun testDetectWakeWord() {
        assertTrue(wakeWordDetector.detectWakeWord("Hey T1000, what's the weather?"))
        assertTrue(wakeWordDetector.detectWakeWord("hey t1000"))
        assertTrue(wakeWordDetector.detectWakeWord("HEY T1000"))
    }

    @Test
    fun testNoWakeWord() {
        val result = wakeWordDetector.detectWakeWord("What's the weather?")
        assertTrue(!result)
    }
}
