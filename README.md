# T1000 - Autonomous Personal AI Assistant

## Overview

T1000 is an advanced autonomous personal AI assistant for Android, powered by Ollama. It combines voice interaction, intelligent automation, memory persistence, financial tracking, trading capabilities, and creative media generation into a single, unified application.

## Architecture

T1000 follows **Clean Architecture** with clear separation of concerns:

```
com.aura.ai/
├── core/                 # Core utilities, security, permissions, logging
├── data/                 # Data layer (local DB, remote APIs, repositories)
├── domain/               # Business logic, use cases, interfaces
├── agent/                # Autonomous agent pipeline and tool registry
├── memory/               # Persistent memory system
├── skills/               # Skill learning and verification
├── ollama/               # Ollama AI engine integration
├── voice/                # Speech recognition, text-to-speech, wake word
├── androidcontrol/       # Android system integrations (apps, device, etc.)
├── webagent/             # Browser automation boundary
├── coding/               # Software development workflows
├── finance/              # Financial integrations
├── trading/              # Trading and market data
├── creative/             # Media generation providers
├── dj/                   # DJ Auto-Pilot engine
└── ui/                   # Jetpack Compose UI layer
```

## Key Components

### 1. Ollama AI Engine
- Real Ollama server integration (configurable URL)
- Connection testing and model discovery
- Model selection and switching
- Streaming response support
- Timeout, retry, and error handling
- Lifecycle-aware cancellation

### 2. Agent Core Pipeline
```
UNDERSTAND → PLAN → SELECT TOOL → CHECK PERMISSION → EXECUTE → VERIFY → RESPOND → REMEMBER
```
- Tool Registry with dynamic registration
- Permission levels (LOW_RISK, CONFIRM_REQUIRED, HIGH_IMPACT)
- State machine for action tracking
- Verification handlers before marking success
- Non-blocking UI execution

### 3. Memory System (Room Database)
- Persistent memory with types: PREFERENCE, FACT, WORKFLOW, INSTRUCTION, PROJECT_CONTEXT, USER_SETTING, SKILL_REFERENCE
- Relevance-based retrieval (not dumping entire DB)
- No passwords, credentials, or sensitive data in ordinary memory
- Explicit memory management (remember, forget, search, update)

### 4. Skills System
- Skill lifecycle: LEARN → UNDERSTAND → PRACTICE → TEST → VERIFY → STORE → USE → EVALUATE → IMPROVE
- Version tracking and evaluation history
- Test and verification criteria
- Skills must pass verification before trusting

### 5. Voice System
```
STANDBY → WAKE → LISTEN → SPEECH TO TEXT → AGENT → ACTION → VERIFY → TEXT TO SPEECH → STANDBY
```
- Wake word detection: "Hey T1000"
- Manual microphone control
- Lifecycle-aware state management
- Prevents recognizer races and TTS feedback loops
- Handles permission denial gracefully

### 6. DJ Auto-Pilot Engine
- Independent operation (works without Ollama)
- Modes: Current Vibe, Energy Up/Down, Wedding/Corporate, Party, Custom
- BPM and key-aware track selection
- Phrase-aware transitions with crossfading
- Energy management and safe fallbacks
- TAKE CONTROL for immediate manual override

### 7. Android Control Tools
- **Applications**: Launch apps, list installed apps
- **Device**: Get device info, battery status
- **Permissions**: Runtime permission checking and management
- **Other integrations**: SMS, calls, contacts, calendar, notifications, media, etc. (interface boundaries ready)

### 8. Financial & Trading Systems
- Finance: Account balances, transactions, categorization
- Trading: Market data, signals, positions, risk management
- Separate READ/PREPARE/EXECUTE boundaries
- Never fabricates data; requires provider confirmation

### 9. Web Agent
- Browser automation boundary with security respects
- Authentication, MFA, CAPTCHA, rate limits are respected
- Clear failure states for unhandled protections

### 10. Coding Agent
- Repository inspection
- File CRUD operations
- Build and test execution
- Git workflows

## Database Schema

### MemoryEntity
```kotlin
data class MemoryEntity(
    val id: Long,
    val type: String, // PREFERENCE, FACT, WORKFLOW, etc.
    val content: String,
    val importance: Int, // 1-10
    val createdTimestamp: Long,
    val updatedTimestamp: Long,
    val metadata: String // JSON
)
```

### SkillEntity
```kotlin
data class SkillEntity(
    val id: Long,
    val name: String,
    val description: String,
    val instructions: String,
    val status: String, // LEARN, UNDERSTAND, PRACTICE, TEST, VERIFY, STORE, USE, EVALUATE, IMPROVE
    val version: String,
    val testCriteria: String, // JSON array
    val verificationCriteria: String, // JSON array
    val evaluationData: String, // JSON object
    val lastEvaluatedTimestamp: Long?,
    val createdTimestamp: Long,
    val updatedTimestamp: Long
)
```

### AgentActionEntity
```kotlin
data class AgentActionEntity(
    val id: Long,
    val requestId: String,
    val tool: String,
    val status: String, // REQUESTED, PLANNED, AWAITING_PERMISSION, EXECUTING, VERIFYING, SUCCEEDED, FAILED, CANCELLED
    val parameters: String, // JSON
    val result: String?,
    val errorMessage: String?,
    val executionTimeMs: Long?,
    val createdTimestamp: Long,
    val completedTimestamp: Long?
)
```

## Dependency Injection (Hilt)

Modular DI setup with feature modules:
- **CoreModule**: Permissions, security, logging
- **DatabaseModule**: Room DAOs and database
- **NetworkModule**: Retrofit, OkHttp, JSON serialization
- **VoiceModule**: Speech recognition, TTS, wake word detection
- **AgentModule**: Tool registry, agent pipeline
- **DjModule**: DJ engine, track selection, transitions
- **AndroidControlModule**: App and device control
- **ExternalIntegrationModule**: Finance, Trading, Web Agent, Coding (with mock implementations)

## Security

- **EncryptedSharedPreferences** for sensitive data
- **Android Keystore** for credentials where needed
- **Permission framework** for runtime permissions
- **No hardcoded secrets**
- **No credentials in logs**
- **Explicit confirmation** for high-impact actions
- **No bypassing** of Android security mechanisms

## Building & Testing

### Build
```bash
./gradlew build
```

### Run Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Run Android Tests
```bash
./gradlew connectedAndroidTest
```

### Run Lint
```bash
./gradlew lint
```

### Build APK
```bash
./gradlew assembleDebug   # Debug APK
./gradlew assembleRelease # Release APK (requires signing config)
```

## Configuration

### Ollama Server
Default Ollama URL is `http://localhost:11434/`. This can be configured in NetworkModule.

### Permissions
Required Android permissions (declared in AndroidManifest.xml):
- `android.permission.INTERNET`
- `android.permission.RECORD_AUDIO` (voice)
- `android.permission.POST_NOTIFICATIONS` (notifications)
- `android.permission.FOREGROUND_SERVICE` (background tasks)
- `android.permission.RECEIVE_BOOT_COMPLETED` (startup)

## Development Workflow

Follow the **IMPLEMENT → TEST → FIX → DOCUMENT → CONTINUE** cycle:

1. **Implement** actual functionality (not mocks)
2. **Test** with unit tests and integration tests
3. **Fix** any failures before moving forward
4. **Document** status and decisions
5. **Continue** with next subsystem

## Real Integrations

Mock implementations are in place for external integrations. To connect real providers:

1. **Ollama**: Already integrated; configure server URL in NetworkModule
2. **Finance**: Implement `IFinanceRepository` interface
3. **Trading**: Implement `ITradingRepository` interface
4. **Web Agent**: Implement `IBrowserRepository` interface
5. **Coding**: Implement `ICodingRepository` interface (GitHub API)

All integrations respect authentication, security controls, and never fabricate results.

## Continuous Integration

Codemagic CI/CD pipeline configured in `codemagic.yaml`:
- Android build, lint, unit tests
- APK generation and release
- Web build for Next.js frontend

## Project Status

✅ **Completed**
- Core application foundation (Hilt, Compose, Material 3)
- Room database with memory, skills, and action history
- Ollama AI engine integration
- Agent core pipeline with tool registry
- Voice system (STT, TTS, wake word)
- DJ Auto-Pilot engine
- Android control tools
- Finance/Trading/Web Agent/Coding domain models
- Comprehensive unit tests
- Security infrastructure
- CI/CD workflow

🚀 **Next Priorities**
- Expanded UI screens and navigation
- Real provider integrations
- Integration tests
- Performance optimization
- User settings UI

## License

T1000 - Autonomous Personal AI Assistant

## Support

For issues, feature requests, or contributions, please refer to the repository's issue tracker.
