package com.aura.ai.domain.usecase

import com.aura.ai.data.remote.OllamaApi
import com.aura.ai.domain.model.OllamaModel
import com.aura.ai.domain.model.OllamaModelsResponse
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class OllamaConnectionUseCaseTest {
    @Mock
    private lateinit var mockOllamaApi: OllamaApi

    private lateinit var useCase: OllamaConnectionUseCase

    @Before
    fun setup() {
        useCase = OllamaConnectionUseCase(mockOllamaApi)
    }

    @Test
    fun testDiscoverModels() = runTest {
        val mockModels = listOf(
            OllamaModel("llama2", "2024-01-01T00:00:00Z", 5000000, "abc123"),
            OllamaModel("mistral", "2024-01-01T00:00:00Z", 4000000, "def456")
        )
        `when`(mockOllamaApi.listModels()).thenReturn(OllamaModelsResponse(mockModels))

        val discovered = useCase.discoverModels()

        assertEquals(2, discovered.size)
        assertTrue(discovered.contains("llama2"))
        assertTrue(discovered.contains("mistral"))
    }

    @Test
    fun testSelectModel() = runTest {
        useCase.selectModel("llama2")
        assertEquals("llama2", useCase.getSelectedModel())
    }
}
