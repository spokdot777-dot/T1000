package com.aura.ai.data.repository

import com.aura.ai.data.local.dao.MemoryDao
import com.aura.ai.data.local.entity.MemoryEntity
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class MemoryRepositoryTest {
    @Mock
    private lateinit var mockMemoryDao: MemoryDao

    private lateinit var repository: MemoryRepository

    @Before
    fun setup() {
        repository = MemoryRepository(mockMemoryDao)
    }

    @Test
    fun testRememberFact() = runTest {
        `when`(mockMemoryDao.insertMemory(org.mockito.ArgumentMatchers.any()))
            .thenReturn(1L)

        val id = repository.rememberFact("PREFERENCE", "User likes coffee", 8)

        assertEquals(1L, id)
        verify(mockMemoryDao).insertMemory(org.mockito.ArgumentMatchers.any())
    }

    @Test
    fun testSearchMemories() = runTest {
        val mockMemories = listOf(
            MemoryEntity(id = 1, type = "PREFERENCE", content = "Likes coffee")
        )
        `when`(mockMemoryDao.searchMemories("coffee"))
            .thenReturn(flowOf(mockMemories))

        repository.searchMemories("coffee").collect { memories ->
            assertEquals(1, memories.size)
            assertEquals("Likes coffee", memories[0].content)
        }
    }

    @Test
    fun testGetMemoriesByType() = runTest {
        val mockMemories = listOf(
            MemoryEntity(id = 1, type = "PREFERENCE", content = "Preference 1")
        )
        `when`(mockMemoryDao.getMemoriesByType("PREFERENCE"))
            .thenReturn(flowOf(mockMemories))

        repository.getMemoriesByType("PREFERENCE").collect { memories ->
            assertEquals(1, memories.size)
        }
    }
}
