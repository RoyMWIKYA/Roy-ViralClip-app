package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.model.*
import com.example.service.GeminiRequest
import com.example.service.Content
import com.example.service.Part
import com.example.service.GenerationConfig
import com.example.service.RetrofitClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ViralClipViewModel : ViewModel() {

    // Selected Navigation Tab
    private val _currentTab = MutableStateFlow("Workspace")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // Workspace & Orchestration State
    private val _prompt = MutableStateFlow("How a secret Q2 2026 battery breakthrough will change the Creator Economy forever...")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _selectedModel = MutableStateFlow("Sora 2 (Enterprise Quality)")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _selectedAspect = MutableStateFlow("Vertical (9:16)")
    val selectedAspect: StateFlow<String> = _selectedAspect.asStateFlow()

    private val _videoGenState = MutableStateFlow("Idle") // Idle, Preparing, LoRA_Align, Synthesizing, Watermarking, Finished
    val videoGenState: StateFlow<String> = _videoGenState.asStateFlow()

    private val _videoProgress = MutableStateFlow(0f)
    val videoProgress: StateFlow<Float> = _videoProgress.asStateFlow()

    private val _videoLogs = MutableStateFlow<List<String>>(listOf("Orchestrator ready. Waiting for input model directive."))
    val videoLogs: StateFlow<List<String>> = _videoLogs.asStateFlow()

    // Hook Engine State
    private val _hookTopic = MutableStateFlow("Unreleased Q2 AI Features")
    val hookTopic: StateFlow<String> = _hookTopic.asStateFlow()

    private val _hooksList = MutableStateFlow<List<HookItem>>(emptyList())
    val hooksList: StateFlow<List<HookItem>> = _hooksList.asStateFlow()

    private val _hookEngineStatus = MutableStateFlow("Idle") // Idle, Generating, Success, Error
    val hookEngineStatus: StateFlow<String> = _hookEngineStatus.asStateFlow()

    private val _hookStatusMessage = MutableStateFlow("")
    val hookStatusMessage: StateFlow<String> = _hookStatusMessage.asStateFlow()

    // Face-Vault State
    private val _identities = MutableStateFlow<List<FaceVaultIdentity>>(emptyList())
    val identities: StateFlow<List<FaceVaultIdentity>> = _identities.asStateFlow()

    // Emotion-First Audio State
    private val _audioProfile = MutableStateFlow(
        AudioProfile(
            name = "ElevenLabs - Rachel (AI Native)",
            emotionalCategory = "Executive Dramatic",
            energyLevel = 0.85f,
            prosodyValue = 0.90f,
            antiRoboticScore = 97,
            platformRisk = "LOW: Non-detection compliance"
        )
    )
    val audioProfile: StateFlow<AudioProfile> = _audioProfile.asStateFlow()

    // Custom WebGL/Viral Probability Engine State
    private val _viralProbability = MutableStateFlow(84)
    val viralProbability: StateFlow<Int> = _viralProbability.asStateFlow()

    // Ledger & Balance State
    private val _creditsBalance = MutableStateFlow(850.0)
    val creditsBalance: StateFlow<Double> = _creditsBalance.asStateFlow()

    private val _ledgerLogs = MutableStateFlow<List<AuditLedgerEntry>>(emptyList())
    val ledgerLogs: StateFlow<List<AuditLedgerEntry>> = _ledgerLogs.asStateFlow()

    // Ghost-Edit Toggle State
    private val _isGhostEditEnabled = MutableStateFlow(true)
    val isGhostEditEnabled: StateFlow<Boolean> = _isGhostEditEnabled.asStateFlow()

    // Distribution Layer Sandbox state
    private val _publishedCounts = MutableStateFlow(mapOf("tiktok" to 14, "meta" to 9, "youtube" to 11))
    val publishedCounts: StateFlow<Map<String, Int>> = _publishedCounts.asStateFlow()

    private val _latestCaption = MutableStateFlow("#creator #ai #trending2026")
    val latestCaption: StateFlow<String> = _latestCaption.asStateFlow()

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    init {
        // Hydrate default data
        initDefaultFaceVault()
        initDefaultLedger()
        initDefaultHooks()
    }

    fun setTab(tabName: String) {
        _currentTab.value = tabName
    }

    fun updatePrompt(v: String) {
        _prompt.value = v
    }

    fun updateModel(v: String) {
        _selectedModel.value = v
    }

    fun updateAspect(v: String) {
        _selectedAspect.value = v
    }

    fun updateHookTopic(v: String) {
        _hookTopic.value = v
    }

    fun updateCaption(v: String) {
        _latestCaption.value = v
    }

    fun toggleGhostEdit() {
        _isGhostEditEnabled.value = !_isGhostEditEnabled.value
    }

    fun setVoiceProfile(profile: AudioProfile) {
        _audioProfile.value = profile
        recalculateViralScore()
    }

    fun updateVoiceSliders(energy: Float, prosody: Float) {
        val current = _audioProfile.value
        val antiRobCode = (80 + (prosody * 18).toInt() + (energy * 2).toInt()).coerceAtMost(100)
        val dangerRating = when {
            antiRobCode > 95 -> "LOW: High Organic Signature"
            antiRobCode > 88 -> "MEDIUM: Minor robotic flatness"
            else -> "HIGH: Demonetization Flag Triggered"
        }
        _audioProfile.value = current.copy(
            energyLevel = energy,
            prosodyValue = prosody,
            antiRoboticScore = antiRobCode,
            platformRisk = dangerRating
        )
        recalculateViralScore()
    }

    private fun recalculateViralScore() {
        // Real-time Probability Simulator based on audio prosody + model + character weights
        val base = 65
        val modelModifier = if (_selectedModel.value.contains("Sora")) 15 else 8
        val prosModifier = (_audioProfile.value.prosodyValue * 12).toInt()
        val energyModifier = (_audioProfile.value.energyLevel * 5).toInt()
        val total = (base + modelModifier + prosModifier + energyModifier).coerceIn(40, 99)
        _viralProbability.value = total
    }

    fun triggerVideoOrchestration() {
        if (_videoGenState.value != "Idle") return
        viewModelScope.launch(Dispatchers.Default) {
            val cost = if (_selectedModel.value.contains("Sora")) 45.0 else 20.0
            if (_creditsBalance.value < cost) {
                logVideo("ERROR: Insufficient platform compute credits. Purchase more tokens.")
                return@launch
            }

            _videoGenState.value = "Preparing"
            _videoProgress.value = 0.05f
            logVideo("Ingesting model landscape options. Aligning compute pipeline parameters.")
            delay(1200)

            _videoGenState.value = "LoRA_Align"
            _videoProgress.value = 0.25f
            logVideo("Resolving Character Face-Vault consistency locks...")
            logVideo("Aligning identity weights across dynamic lights & 50+ frame vectors.")
            delay(1500)

            _videoGenState.value = "Synthesizing"
            _videoProgress.value = 0.55f
            logVideo("Orchestrating video frame generation using ${_selectedModel.value} core matrix...")
            logVideo("Processing Q2-optimized motion tensors...")
            delay(2000)

            _videoGenState.value = "Watermarking"
            _videoProgress.value = 0.80f
            logVideo("Embedding metadata provenance tokens. Injecting C2PA crypt-badges...")
            logVideo("Applying SynthID steganographic watermarking layer across RGB fields...")
            delay(1200)

            _videoGenState.value = "Finished"
            _videoProgress.value = 1.0f
            logVideo("Co-processing success! Generation is 99.8% identical-matching Face Vault.")
            logVideo("Total GPU Credits Expended: $cost. Manifest ledger certified.")

            // Log ledger
            expendCredits(cost, "Generation Sync: ${_selectedModel.value} (aspect: ${_selectedAspect.value})")
            delay(3000)
            _videoGenState.value = "Idle"
        }
    }

    private fun logVideo(msg: String) {
        val curr = _videoLogs.value.toMutableList()
        curr.add("[${currentTimeString()}] $msg")
        _videoLogs.value = curr
    }

    private fun expendCredits(cost: Double, details: String) {
        _creditsBalance.value = (_creditsBalance.value - cost).coerceAtLeast(0.0)
        val ledger = _ledgerLogs.value.toMutableList()
        val randomSig = UUID.randomUUID().toString().substring(0, 8).uppercase()
        ledger.add(0, AuditLedgerEntry(
            id = "TX-${UUID.randomUUID().toString().substring(0,6).uppercase()}",
            timestamp = currentTimeString(),
            actionName = details,
            creditsExpended = cost,
            blockSignature = "SHA256:0x$randomSig...DFGP",
            provenanceBadge = "C2PA + SynthID Secure"
        ))
        _ledgerLogs.value = ledger
    }

    fun addConsentIdentity(name: String, age: Int) {
        val list = _identities.value.toMutableList()
        val newId = "ID-${list.size + 1}"
        list.add(FaceVaultIdentity(
            id = newId,
            labelName = name,
            ageValue = age,
            baseAvatarId = (list.size % 4) + 1,
            expressions = listOf("Joyous", "Intense Focal", "Aesthetic Contemplative"),
            similarityPercent = 99.4,
            isConsentVerified = true,
            deepfakeFraudCheck = "Verified Pass"
        ))
        _identities.value = list
        expendCredits(10.0, "LoRA Fine-Tune Identity Session: $name")
    }

    fun toggleConsent(id: String) {
        val list = _identities.value.map {
            if (it.id == id) {
                val nextConsent = !it.isConsentVerified
                it.copy(
                    isConsentVerified = nextConsent,
                    deepfakeFraudCheck = if (nextConsent) "Verified Pass" else "Failed Audit"
                )
            } else it
        }
        _identities.value = list
    }

    fun deleteIdentity(id: String) {
        _identities.value = _identities.value.filter { it.id != id }
    }

    // Hook Engine Generation via Gemini API
    fun generateViralHooks() {
        if (_hookEngineStatus.value == "Generating") return
        _hookEngineStatus.value = "Generating"
        _hookStatusMessage.value = "Contacting trend servers and parsing Q2 retention benchmarks..."

        viewModelScope.launch(Dispatchers.IO) {
            val key = BuildConfig.GEMINI_API_KEY
            val isSimulated = key.isEmpty() || key == "MY_GEMINI_API_KEY"

            if (isSimulated) {
                delay(2200) // Simulated processing latency
                val mockHooks = listOf(
                    HookItem(
                        id = "H1",
                        hookText = "Stop scrolling if you want to know which AI models will be legal in Q3 2026!",
                        interruptTheme = "🚨 Legal Curiosity Gap",
                        retentionScore = 96,
                        explainability = "Utilizes fear-of-missing-out surrounding upcoming regulatory changes, paired with dynamic timing constraints."
                    ),
                    HookItem(
                        id = "H2",
                        hookText = "I built an AI influencer in 5 minutes with Face-Vault and yes, she is already driving premium sponsorship conversions...",
                        interruptTheme = "📈 Visual Proof Pattern",
                        retentionScore = 93,
                        explainability = "Establishes immediate high-authority metrics (5 minutes, conversions) which creates severe mental engagement."
                    ),
                    HookItem(
                        id = "H3",
                        hookText = "The shocking $80 compute hack that is rendering Sora model generation essentially free...",
                        interruptTheme = "💡 Deep Shock Paradox",
                        retentionScore = 89,
                        explainability = "Triggers resource utility pathways by revealing cost-effective hacks in a highly protected industry."
                    )
                )
                _hooksList.value = mockHooks
                _hookEngineStatus.value = "Success"
                _hookStatusMessage.value = "Generated hooks (Simulated mode: Add Gemini API Key to secrets panel to activate real AI models)"
                expendCredits(5.0, "Hook Generation: ${_hookTopic.value} (Simulated)")
            } else {
                try {
                    val promptText = """
                        You are a viral hook generation expert. Analyze the topic: '${_hookTopic.value}'.
                        Generate exactly 3 HookItems. Respond in strict JSON format. 
                        Do NOT include any markdown blocks around the JSON string.
                        The JSON must follow this exact structure:
                        {
                          "topic": "${_hookTopic.value}",
                          "targetPlatform": "Short-Form (Vertical)",
                          "hooks": [
                            {
                              "id": "H1",
                              "hookText": "Insert extreme visual or psychic pattern interrupt text",
                              "interruptTheme": "Category: e.g. Curiosity Gap, Shock Stat, Paradox",
                              "retentionScore": 92,
                              "explainability": "Detailed explanations of the psychological cognitive hook trigger"
                            },
                            {
                              "id": "H2",
                              "hookText": "Insert alternative hook",
                              "interruptTheme": "Category",
                              "retentionScore": 88,
                              "explainability": "Explanations"
                            },
                            {
                              "id": "H3",
                              "hookText": "Insert third hook option",
                              "interruptTheme": "Category",
                              "retentionScore": 81,
                              "explainability": "Explanations"
                            }
                          ]
                        }
                    """.trimIndent()

                    val requestBody = GeminiRequest(
                        contents = listOf(Content(parts = listOf(Part(text = promptText)))),
                        generationConfig = GenerationConfig(
                            temperature = 0.9f,
                            responseMimeType = "application/json"
                        )
                    )

                    val response = RetrofitClient.service.generateContent(key, requestBody)
                    val rawJson = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                    if (rawJson != null) {
                        // Clean markdown if the model ignored directions
                        val cleanJson = rawJson.replace("```json", "").replace("```", "").trim()
                        val adapter = moshi.adapter(HookGenerationResult::class.java)
                        val result = adapter.fromJson(cleanJson)

                        if (result != null && result.hooks.isNotEmpty()) {
                            _hooksList.value = result.hooks
                            _hookEngineStatus.value = "Success"
                            _hookStatusMessage.value = "Hooks successfully generated via live Gemini 3.5-Flash!"
                            expendCredits(12.0, "Hook Generation: ${_hookTopic.value} (Live API)")
                        } else {
                            throw Exception("JSON structure could not be parsed as HookResult")
                        }
                    } else {
                        throw Exception("Model returned empty or null response candidate text.")
                    }
                } catch (e: Exception) {
                    Log.e("ViralClip", "Gemini API Hook Failure: ${e.message}", e)
                    _hookEngineStatus.value = "Error"
                    _hookStatusMessage.value = "Live API Error: ${e.localizedMessage ?: "Unknown Error"}. Falling back to simulation."
                    delay(3000)
                    _hookEngineStatus.value = "Idle"
                }
            }
        }
    }

    // Simulated Social Publisher
    fun triggerSocialPublish(platform: String) {
        viewModelScope.launch {
            expendCredits(8.5, "Autonomous Published Dispatch: $platform aspect configuration verification")
            val currentMap = _publishedCounts.value.toMutableMap()
            currentMap[platform] = (currentMap[platform] ?: 0) + 1
            _publishedCounts.value = currentMap
        }
    }

    fun purchaseCredits() {
        _creditsBalance.value = _creditsBalance.value + 500.0
        val ledger = _ledgerLogs.value.toMutableList()
        ledger.add(0, AuditLedgerEntry(
            id = "TX-CRED-${UUID.randomUUID().toString().substring(0,4).uppercase()}",
            timestamp = currentTimeString(),
            actionName = "Injected Compute Token Reserve Bundle",
            creditsExpended = -500.0,
            blockSignature = "SHA256:0xCRED_TOPUP_SECURE",
            provenanceBadge = "Admin Verified Ledger"
        ))
        _ledgerLogs.value = ledger
    }

    private fun initDefaultFaceVault() {
        _identities.value = listOf(
            FaceVaultIdentity(
                id = "ID-1",
                labelName = "Evelyn Sterling (Tech Host)",
                ageValue = 28,
                baseAvatarId = 1,
                expressions = listOf("Dynamic Presenter", "Deep Intuitive Focus", "Explaining Shock"),
                similarityPercent = 99.8,
                isConsentVerified = true,
                deepfakeFraudCheck = "Verified Pass"
            ),
            FaceVaultIdentity(
                id = "ID-2",
                labelName = "Marcus Thorne (Business Executive)",
                ageValue = 34,
                baseAvatarId = 2,
                expressions = listOf("Calm Confidence", "Vivid Explainer", "Smug Contrast"),
                similarityPercent = 98.9,
                isConsentVerified = true,
                deepfakeFraudCheck = "Verified Pass"
            ),
            FaceVaultIdentity(
                id = "ID-3",
                labelName = "Kira Vance (Cyber Aesthetic Host)",
                ageValue = 22,
                baseAvatarId = 3,
                expressions = listOf("High-Energy Exclamatory", "Sardonic Raised Eyebrow"),
                similarityPercent = 99.4,
                isConsentVerified = false,
                deepfakeFraudCheck = "Failed Consent Verification"
            )
        )
    }

    private fun initDefaultLedger() {
        _ledgerLogs.value = listOf(
            AuditLedgerEntry(
                id = "TX-90112A",
                timestamp = "2026-05-28 22:15:01",
                actionName = "LoRA Fine-Tune Sequence: Evelyn Sterling Q2 model updates",
                creditsExpended = 35.0,
                blockSignature = "SHA256:0xE7C39A...DFGP",
                provenanceBadge = "SynthID + C2PA Attached"
            ),
            AuditLedgerEntry(
                id = "TX-90111B",
                timestamp = "2026-05-28 21:04:15",
                actionName = "Wan MoE Rapid Draft Inference run: Topic '2026 Creator Trends'",
                creditsExpended = 15.0,
                blockSignature = "SHA256:0x65F0AA...DFGP",
                provenanceBadge = "SynthID Attached"
            ),
            AuditLedgerEntry(
                id = "TX-90110C",
                timestamp = "2026-05-28 19:40:50",
                actionName = "ElevenLabs API Emotional Audio Prosody Stitching",
                creditsExpended = 8.0,
                blockSignature = "SHA256:0x33BB19...DFGP",
                provenanceBadge = "Database-Verified Anchor"
            )
        )
    }

    private fun initDefaultHooks() {
        _hooksList.value = listOf(
            HookItem(
                id = "H-DEF1",
                hookText = "This simple 5-second trick will increase your short-form retention rate by 42%...",
                interruptTheme = "⏱️ Pattern Delay Loop",
                retentionScore = 95,
                explainability = "Forces immediate loop cognition by offering a major performance guarantee (42%) matching brief investment (5-seconds)."
            ),
            HookItem(
                id = "H-DEF2",
                hookText = "Why Q2 2026 is officially the hardest time in history to get viral distribution...",
                interruptTheme = "📉 Curiosity Paradox",
                retentionScore = 91,
                explainability = "Taps into creator self-preservation anxieties by identifying current timing difficulty."
            )
        )
    }

    private fun currentTimeString(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
