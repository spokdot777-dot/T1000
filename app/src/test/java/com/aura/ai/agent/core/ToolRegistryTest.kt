package com.aura.ai.agent.core

import com.aura.ai.data.repository.AgentRepository
import com.aura.ai.domain.model.AgentState
import com.aura.ai.domain.model.PermissionLevel
import com.aura.ai.domain.model.Tool
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToolRegistryTest {
    private lateinit var toolRegistry: ToolRegistry

    @Before
    fun setup() {
        toolRegistry = ToolRegistry()
    }

    @Test
    fun testRegisterAndRetrieveTool() = runTest {
        val mockTool = Tool(
            name = "test_tool",
            description = "Test tool",
            parameters = mapOf("param1" to "String"),
            permissionLevel = PermissionLevel.LOW_RISK,
            handler = { Result.success("success") },
            verificationHandler = { _, _ -> true }
        )

        toolRegistry.registerTool(mockTool)
        val retrieved = toolRegistry.getTool("test_tool")

        assertEquals(mockTool.name, retrieved?.name)
    }

    @Test
    fun testToolExists() {
        val mockTool = Tool(
            name = "existing_tool",
            description = "Existing tool",
            parameters = emptyMap(),
            permissionLevel = PermissionLevel.LOW_RISK,
            handler = { Result.success("ok") },
            verificationHandler = { _, _ -> true }
        )

        toolRegistry.registerTool(mockTool)
        assertTrue(toolRegistry.toolExists("existing_tool"))
    }

    @Test
    fun testGetToolsByPermissionLevel() {
        val lowRiskTool = Tool(
            name = "low_risk",
            description = "Low risk",
            parameters = emptyMap(),
            permissionLevel = PermissionLevel.LOW_RISK,
            handler = { Result.success("ok") },
            verificationHandler = { _, _ -> true }
        )

        val highImpactTool = Tool(
            name = "high_impact",
            description = "High impact",
            parameters = emptyMap(),
            permissionLevel = PermissionLevel.HIGH_IMPACT,
            handler = { Result.success("ok") },
            verificationHandler = { _, _ -> true }
        )

        toolRegistry.registerTool(lowRiskTool)
        toolRegistry.registerTool(highImpactTool)

        val highImpactTools = toolRegistry.getToolsByPermissionLevel(PermissionLevel.HIGH_IMPACT)
        assertEquals(1, highImpactTools.size)
        assertEquals("high_impact", highImpactTools[0].name)
    }
}
