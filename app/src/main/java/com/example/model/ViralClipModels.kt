package com.example.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HookItem(
    val id: String,
    val hookText: String,
    val interruptTheme: String,
    val retentionScore: Int,
    val explainability: String
)

@JsonClass(generateAdapter = true)
data class HookGenerationResult(
    val topic: String,
    val targetPlatform: String,
    val hooks: List<HookItem>
)

data class FaceVaultIdentity(
    val id: String,
    val labelName: String,
    val ageValue: Int,
    val baseAvatarId: Int, // local simulated index
    val expressions: List<String>,
    val similarityPercent: Double,
    val isConsentVerified: Boolean,
    val deepfakeFraudCheck: String // "Verified Pass", "Pending", "Failed"
)

data class AudioProfile(
    val name: String,
    val emotionalCategory: String, // "Curious", "Dramatic", "Authoritative", "Excited"
    val energyLevel: Float, // 0-1
    val prosodyValue: Float, // 0-1
    val antiRoboticScore: Int, // 0-100
    val platformRisk: String // "LOW", "MEDIUM", "HIGH"
)

data class AuditLedgerEntry(
    val id: String,
    val timestamp: String,
    val actionName: String,
    val creditsExpended: Double,
    val blockSignature: String,
    val provenanceBadge: String // "SynthID + C2PA Attached"
)
