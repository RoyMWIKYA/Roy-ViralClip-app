package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.*
import com.example.ui.theme.*
import com.example.viewmodel.ViralClipViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    containerColor = PremiumBg
                ) { innerPadding ->
                    ViralClipWorkspaceScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ViralClipWorkspaceScreen(
    modifier: Modifier = Modifier,
    viewModel: ViralClipViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val creditsBalance by viewModel.creditsBalance.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PremiumBg)
    ) {
        // --- Bento App Header ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PremiumBg)
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "VERIFICATION PIPELINE 6/6",
                    color = ElectricCyan.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.SansSerif
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "ViralClip",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 24.sp,
                        letterSpacing = (-0.5).sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "v2.6",
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }
            }

            // Credits Balance Tracker Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, CircleShape)
                    .clickable {
                        viewModel.purchaseCredits()
                        Toast.makeText(context, "Injected +500 GPU Compute Credits", Toast.LENGTH_SHORT).show()
                    }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(ElectricCyan)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format(Locale.US, "%.1f GPU Credits", creditsBalance),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // --- Section Navigation Row (Modern Scrollable Tab Hub) ---
        ScrollableTabRow(
            selectedTabIndex = getTabIndex(currentTab),
            containerColor = PremiumBg,
            contentColor = ElectricCyan,
            edgePadding = 12.dp,
            divider = { HorizontalDivider(color = CardBorder, thickness = 1.dp) },
            indicator = { tabPositions ->
                val tabIndex = getTabIndex(currentTab)
                if (tabIndex < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                        color = ElectricCyan
                    )
                }
            }
        ) {
            val tabs = listOf("Workspace", "Hook Engine", "Face-Vault", "Audio Lab", "Publish Sync", "Security Ledger")
            tabs.forEach { tab ->
                Tab(
                    selected = currentTab == tab,
                    onClick = { viewModel.setTab(tab) },
                    text = {
                        Text(
                            text = tab.uppercase(),
                            color = if (currentTab == tab) Color.White else Color.White.copy(alpha = 0.5f),
                            fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                )
            }
        }

        // --- Active Window Display Panel ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            when (currentTab) {
                "Workspace" -> WorkspacePanel(viewModel)
                "Hook Engine" -> HookEnginePanel(viewModel)
                "Face-Vault" -> FaceVaultPanel(viewModel)
                "Audio Lab" -> AudioLabPanel(viewModel)
                "Publish Sync" -> PublishSyncPanel(viewModel)
                "Security Ledger" -> SecurityLedgerPanel(viewModel)
            }
        }

        // --- Footer Pipeline Health Status Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PremiumBg)
                .border(1.dp, CardBorder)
                .padding(vertical = 8.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "C2PA Verified • EU AI ACT COMPLIANT",
                    color = GrayMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "SECURE SANDBOX NODE",
                color = ElectricCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

// --- WORKSPACE & ORCHESTRATION PANEL ---
@Composable
fun WorkspacePanel(viewModel: ViralClipViewModel) {
    val prompt by viewModel.prompt.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val selectedAspect by viewModel.selectedAspect.collectAsState()
    val videoGenState by viewModel.videoGenState.collectAsState()
    val videoProgress by viewModel.videoProgress.collectAsState()
    val videoLogs by viewModel.videoLogs.collectAsState()
    val viralProbability by viewModel.viralProbability.collectAsState()
    val isGhostEditEnabled by viewModel.isGhostEditEnabled.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Row holding settings on left and Viral Score custom indicator dial on the right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1.3f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "VIDEO PROMPT STRUCT & ORCHESTRATION",
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { viewModel.updatePrompt(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("prompt_input"),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentGreen,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = PremiumBg,
                        unfocusedContainerColor = PremiumBg
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Model Selector Combo
                Text(
                    text = "TARGET VIDEO GENERATION MODEL",
                    color = GrayMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val models = listOf("Sora 2 (Enterprise Quality)", "Wan 2.1 (Rapid 2-Pass MoE)")
                    models.forEach { model ->
                        val isSelected = selectedModel == model
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) SurfaceDarkLighter else PremiumBg)
                                .border(
                                    1.dp,
                                    if (isSelected) AccentGreen else CardBorder,
                                    RoundedCornerShape(20.dp)
                               )
                                .clickable { viewModel.updateModel(model) }
                                .padding(vertical = 12.dp, horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = model,
                                color = if (isSelected) Color.White else GrayMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Live Viral Probability Canvas Dial
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "VIRAL PROBABILITY",
                    color = GrayMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(90.dp)
                ) {
                    val animatedPercentage by animateFloatAsState(
                        targetValue = viralProbability.toFloat() / 100f,
                        animationSpec = tween(1200, easing = FastOutSlowInEasing),
                        label = "viralPercentageAnim"
                    )

                    Canvas(modifier = Modifier.size(84.dp)) {
                        // Background track
                        drawCircle(
                            color = CardBorder,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                        // Progress ring segment
                        drawArc(
                            brush = Brush.linearGradient(
                                colors = listOf(AccentGreen, ElectricCyan)
                            ),
                            startAngle = -90f,
                            sweepAngle = 360f * animatedPercentage,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$viralProbability%",
                            color = AccentGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "RETENTION",
                            color = Color.White,
                            fontSize = 7.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                // Explanatory Tag
                Text(
                    text = "Model: ${if (selectedModel.contains("Sora")) "High" else "Medium"} dynamic retention matching.",
                    color = GrayMuted,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Row for select dynamic aspect ration + non-destructive edit toggler
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Aspect Options
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "ASPECT COMPLIANCE LAYER",
                    color = GrayMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val aspects = listOf("Vertical (9:16)", "Horizontal (16:9)", "Square (1:1)")
                    aspects.forEach { aspect ->
                        val isSelected = selectedAspect == aspect
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) SurfaceDarkLighter else PremiumBg)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricCyan else CardBorder,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.updateAspect(aspect) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = aspect.replace(" (", "\n("),
                                color = if (isSelected) Color.White else GrayMuted,
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Non-Destructive Ghost Edit Toggle
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Ghost-Edit™ Timeline",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Non-destructive Q2 spec",
                            color = GrayMuted,
                            fontSize = 9.sp
                        )
                    }
                    Switch(
                        checked = isGhostEditEnabled,
                        onCheckedChange = { viewModel.toggleGhostEdit() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AccentGreen,
                            checkedTrackColor = SurfaceDarkLighter,
                            uncheckedThumbColor = GrayMuted,
                            uncheckedTrackColor = SurfaceDarkLighter
                        ),
                        modifier = Modifier.scale(0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "If enabled, frame structures are held in dynamic WebGL buffer matrices for near zero-latency manipulation.",
                    color = GrayMuted,
                    fontSize = 8.sp,
                    lineHeight = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- Active Simulation Monitor and Launcher Box ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Orchestration & Media Core".uppercase(),
                            color = AccentGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "State pipeline render sequence viewport",
                            color = GrayMuted,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = { viewModel.triggerVideoOrchestration() },
                        enabled = videoGenState == "Idle",
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricCyan,
                            contentColor = PremiumBg,
                            disabledContainerColor = SurfaceDarkLighter,
                            disabledContentColor = GrayMuted
                        ),
                        shape = CircleShape,
                        modifier = Modifier.testTag("orchestrate_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run Icon")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (videoGenState == "Idle") "LAUNCH PIPELINE" else "RUNNING CORE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Render Simulation Frame / Cinematic 16:9 Viewport
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black)
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
                ) {
                    // Vertical Ambient Dark overlay gradient
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                    )

                    // Top Left watermark tag
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (videoGenState == "Idle") GrayMuted else ElectricCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedModel.contains("Sora")) "SORA 2.0 • LIVE PREVIEW" else "WAN 2.1 • LIVE VIEW",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // Content based on IDLE / GENERATING
                    if (videoGenState == "Idle") {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Ready state",
                                tint = CardBorder.copy(alpha = 0.8f),
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "CO-PROCESSOR STANDBY",
                                color = GrayMuted,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Ready to ingest video prompt vector",
                                color = GrayMuted.copy(alpha = 0.6f),
                                fontSize = 9.sp
                            )
                        }
                    } else {
                        // Rendering details overlay
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "RENDERING: ${videoGenState.uppercase()}",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            LinearProgressIndicator(
                                progress = { videoProgress },
                                modifier = Modifier.fillMaxWidth().height(6.dp),
                                color = ElectricCyan,
                                trackColor = Color.White.copy(alpha = 0.1f),
                                strokeCap = StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = String.format(Locale.US, "Synthesizing frames - %.0f%% complete", videoProgress * 100),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Bottom interface Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (videoGenState == "Idle") "00:00 / 00:30" else String.format(Locale.US, "00:%02d / 00:30", (videoProgress * 30).toInt()),
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        // Playback icons row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⏮",
                                color = Color.White.copy(alpha = if (videoGenState == "Idle") 0.3f else 0.8f),
                                fontSize = 12.sp,
                                modifier = Modifier.clickable(enabled = videoGenState != "Idle") { }
                            )
                            Text(
                                text = if (videoGenState != "Idle") "⏸" else "▶",
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { }
                            )
                            Text(
                                text = "⏭",
                                color = Color.White.copy(alpha = if (videoGenState == "Idle") 0.3f else 0.8f),
                                fontSize = 12.sp,
                                modifier = Modifier.clickable(enabled = videoGenState != "Idle") { }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pipeline Log output block
                Text(
                    text = "REAL-TIME RENDERING CONSOLE",
                    color = GrayMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PremiumBg)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    videoLogs.asReversed().forEach { logLine ->
                        Text(
                            text = logLine,
                            color = if (logLine.contains("ERROR")) ErrorCrimson else if (logLine.contains("co-process")) SuccessGreen else WhiteSoft,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- TIER 1/2 HOOK ENGINE PANEL ---
@Composable
fun HookEnginePanel(viewModel: ViralClipViewModel) {
    val hookTopic by viewModel.hookTopic.collectAsState()
    val hooksList by viewModel.hooksList.collectAsState()
    val hookEngineStatus by viewModel.hookEngineStatus.collectAsState()
    val hookStatusMessage by viewModel.hookStatusMessage.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "HOOK ENGINE: COGNITIVE OVERRIDE INGEST",
            color = AccentGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Process trending signals across networks and trigger 3 immediate pattern interrupts",
            color = GrayMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Topic input + run trigger
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = hookTopic,
                onValueChange = { viewModel.updateHookTopic(it) },
                label = { Text("Trending Topic Vector", color = GrayMuted, fontSize = 11.sp) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("hook_topic_input"),
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentGreen,
                    unfocusedBorderColor = CardBorder,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark
                )
            )

            Button(
                onClick = { viewModel.generateViralHooks() },
                enabled = hookEngineStatus != "Generating",
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = PremiumBg),
                modifier = Modifier
                    .height(56.dp)
                    .testTag("hook_generate_button")
            ) {
                if (hookEngineStatus == "Generating") {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = PremiumBg)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Gen Icon")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("DECODE HOOKS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        if (hookStatusMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceDarkLighter)
                    .border(1.dp, CardBorder, RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = hookStatusMessage,
                    color = if (hookEngineStatus == "Error") ErrorCrimson else ElectricCyan,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "DECODED PATTERN INTERRUPTION SEGMENTS",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (hooksList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No active hooks loaded in buffer. Launch the decode sequence.",
                    color = GrayMuted,
                    fontSize = 11.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                hooksList.forEach { hookItem ->
                    HookItemRow(hookItem)
                }
            }
        }
    }
}

@Composable
fun HookItemRow(item: HookItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Theme Category Label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceDarkLighter)
                            .border(1.dp, CardBorder, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = item.interruptTheme.uppercase(),
                            color = AccentGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // Retention Match Tag score
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "RETENTION SCORE: ",
                            color = GrayMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${item.retentionScore}%",
                            color = if (item.retentionScore >= 90) AccentGreen else GoldWarm,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Segmented Retention Bar (Bento-style indicator)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        modifier = Modifier.width(60.dp)
                    ) {
                        val activeBars = when {
                            item.retentionScore >= 95 -> 3
                            item.retentionScore >= 85 -> 2
                            else -> 1
                        }
                        for (i in 1..3) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(CircleShape)
                                    .background(if (i <= activeBars) AccentGreen else CardBorder)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // The exact hook verbal trigger
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PremiumBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "\"${item.hookText}\"",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explanation / Psych triggers
            Text(
                text = "NEURO-EXPLANABILITY AUDIT",
                color = GrayMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
            Text(
                text = item.explainability,
                color = GrayMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

// --- CHARACTER CONSISTENCY FACE VAULT PANEL ---
@Composable
fun FaceVaultPanel(viewModel: ViralClipViewModel) {
    val identities by viewModel.identities.collectAsState()
    var newName by remember { mutableStateOf("") }
    var newAge by remember { mutableStateOf("25") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "FACE-VAULT: DIGITAL IDENTITY SYSTEM",
            color = AccentGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Maintain character similarity >98% across 50+ dynamically lit scenes (EU AI Act Consent verified)",
            color = GrayMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Top Row layout representing the Bento architecture: LoRA Trainer + Consistency summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Create new identity form block
            Card(
                modifier = Modifier
                    .weight(1.2f)
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TRAIN NEW IDENTITY LORA MODEL",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Consent verified scene pipeline",
                        color = GrayMuted,
                        fontSize = 9.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("id_name_input"),
                        placeholder = { Text("Label (e.g. Male Host)", color = GrayMuted, fontSize = 11.sp) },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PurpleAesthetic,
                            unfocusedBorderColor = CardBorder,
                            focusedContainerColor = PremiumBg,
                            unfocusedContainerColor = PremiumBg
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newAge,
                            onValueChange = { newAge = it.filter { ch -> ch.isDigit() } },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("id_age_input"),
                            placeholder = { Text("Age", color = GrayMuted, fontSize = 11.sp) },
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurpleAesthetic,
                                unfocusedBorderColor = CardBorder,
                                focusedContainerColor = PremiumBg,
                                unfocusedContainerColor = PremiumBg
                            )
                        )

                        Button(
                            onClick = {
                                if (newName.isNotBlank()) {
                                    viewModel.addConsentIdentity(newName, newAge.toIntOrNull() ?: 25)
                                    newName = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAesthetic, contentColor = Color.White),
                            shape = CircleShape,
                            modifier = Modifier.weight(1.8f)
                        ) {
                            Text("FINE TUNE LoRA", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Purple Consistency Bento Card
            Card(
                modifier = Modifier
                    .weight(0.8f)
                    .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "FACE-VAULT",
                            color = PurpleAesthetic,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(PurpleAesthetic.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "LOCKED",
                                color = PurpleAesthetic,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pulsing / Dashed round likeness placeholder
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(1.5.dp, PurpleAesthetic, CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(PurpleAesthetic.copy(alpha = 0.2f))
                        ) {
                            // Sparkles / Key icon or label
                            Text(
                                text = "🔑",
                                fontSize = 14.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Consistency Score",
                        color = GrayMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "99.8%",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loaded Identity List
        Text(
            text = "VERIFIED CHARACTER REPOSITORY",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            identities.forEach { identity ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Avatar graphic indicator
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SurfaceDarkLighter)
                                .border(1.3.dp, AccentGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "L${identity.baseAvatarId}",
                                color = AccentGreen,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Details Column
                        Column(modifier = Modifier.weight(1.5f)) {
                            Text(
                                text = identity.labelName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Simul: ${identity.similarityPercent}% | Age: ${identity.ageValue}",
                                    color = GrayMuted,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Compliance Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (identity.isConsentVerified) Color(0xFF132A1F) else Color(0xFF2E1719))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = identity.deepfakeFraudCheck.uppercase(),
                                    color = if (identity.isConsentVerified) SuccessGreen else ErrorCrimson,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Consent Verification Switch & Delete buttons
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Consent",
                                    color = GrayMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Switch(
                                    checked = identity.isConsentVerified,
                                    onCheckedChange = { viewModel.toggleConsent(identity.id) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = SuccessGreen,
                                        checkedTrackColor = SurfaceDarkLighter,
                                        uncheckedThumbColor = ErrorCrimson,
                                        uncheckedTrackColor = SurfaceDarkLighter
                                    ),
                                    modifier = Modifier.scale(0.6f)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.deleteIdentity(identity.id) },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = ErrorCrimson,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- AUDIO LAB (EMOTION ARCHITECTURE) PANEL ---
@Composable
fun AudioLabPanel(viewModel: ViralClipViewModel) {
    val audioProfile by viewModel.audioProfile.collectAsState()
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "EMOTION-FIRST VOCAL INTERFACE",
            color = AccentGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Calibrate fine-grained prosody indices, preventing automated robotic triggers & demonetization audits",
            color = GrayMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Prebuilt profile selector grid
        Text(
            text = "ELEVENLABS ADVANCED SPEECH PROFILE presets",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(6.dp))

        val voices = listOf(
            AudioProfile("Rachel (Creator Host)", "Executive Dramatic", 0.85f, 0.90f, 97, "LOW"),
            AudioProfile("Antony (Authority Spec)", "Deep Informational", 0.60f, 0.94f, 98, "LOW"),
            AudioProfile("Cynthia (High Emotion)", "Excited Fast-Cut", 0.95f, 0.70f, 89, "MEDIUM"),
            AudioProfile("Robo-Classic (Flat Text)", "Stiff Informative", 0.40f, 0.20f, 61, "HIGH")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            voices.take(2).forEach { pc ->
                val SelectedVoice = audioProfile.name.contains(pc.name)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (SelectedVoice) SurfaceDarkLighter else SurfaceDark)
                        .border(
                            1.dp,
                            if (SelectedVoice) AccentGreen else CardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.setVoiceProfile(pc) }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(text = pc.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(text = pc.emotionalCategory, color = AccentGreen, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            voices.takeLast(2).forEach { pc ->
                val SelectedVoice = audioProfile.name.contains(pc.name)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (SelectedVoice) SurfaceDarkLighter else SurfaceDark)
                        .border(
                            1.dp,
                            if (SelectedVoice) AccentGreen else CardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.setVoiceProfile(pc) }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(text = pc.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        Text(text = pc.emotionalCategory, color = AccentGreen, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sliders block
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ACTIVE VOCAL DENSITY PARAMETERS",
                    color = AccentGreen,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Slider 1: Energy / Delivery Speed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "ENERGY / CLIMACTIC RHYTHM", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = String.format(Locale.US, "%.0f%%", audioProfile.energyLevel * 100), color = ElectricCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = audioProfile.energyLevel,
                    onValueChange = { viewModel.updateVoiceSliders(it, audioProfile.prosodyValue) },
                    colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan, inactiveTrackColor = PremiumBg)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Slider 2: Prosody / Emotion pitch variance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "EMOTIONAL PROSODY VARIANCE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = String.format(Locale.US, "%.0f%%", audioProfile.prosodyValue * 100), color = AccentGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
                Slider(
                    value = audioProfile.prosodyValue,
                    onValueChange = { viewModel.updateVoiceSliders(audioProfile.energyLevel, it) },
                    colors = SliderDefaults.colors(thumbColor = AccentGreen, activeTrackColor = AccentGreen, inactiveTrackColor = PremiumBg)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Metrics readout grid
                Text(
                    text = "DETECTION COMPLIANCE METRICS",
                    color = GrayMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(PremiumBg)
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Anti-Robotic Signature", color = GrayMuted, fontSize = 8.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${audioProfile.antiRoboticScore}/100",
                            color = if (audioProfile.antiRoboticScore >= 90) AccentGreen else if (audioProfile.antiRoboticScore >= 80) GoldWarm else ErrorCrimson,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(PremiumBg)
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Platform Demoni-risk", color = GrayMuted, fontSize = 8.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = audioProfile.platformRisk.substringBefore(":"),
                            color = if (audioProfile.platformRisk.startsWith("LOW")) AccentGreen else if (audioProfile.platformRisk.startsWith("MED")) GoldWarm else ErrorCrimson,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

// --- OUTSTAND/AYRSHARE DISTRIBUTION LAYER PANEL ---
@Composable
fun PublishSyncPanel(viewModel: ViralClipViewModel) {
    val publishedCounts by viewModel.publishedCounts.collectAsState()
    val latestCaption by viewModel.latestCaption.collectAsState()
    val context = LocalContext.current

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "DISTRIBUTION LAYER HUB: SHADOW POSTING sandbox",
            color = AccentGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Autonomous dispatch system simulating post triggers on social graphs, validating compliance checks beforehand",
            color = GrayMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Caption optimizer box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "COGNITIVE TAGS & CAPTION COMPILER",
                    color = AccentGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = latestCaption,
                    onValueChange = { viewModel.updateCaption(it) },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentGreen,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = PremiumBg,
                        unfocusedContainerColor = PremiumBg
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sandbox platform dispatches
        Text(
            text = "AVAILABLE DISPATCH CHANNELS",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        val channels = listOf(
            Triple("tiktok", "TikTok Short Graph", Icons.Default.Share),
            Triple("meta", "Meta Reels / Instagram Grid", Icons.Default.Share),
            Triple("youtube", "YouTube short-form index", Icons.Default.Share)
        )

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            channels.forEach { (key, title, icon) ->
                val postedCount = publishedCounts[key] ?: 0
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Shadow-posted sandboxes dispatched: $postedCount runs",
                                color = GrayMuted,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.triggerSocialPublish(key)
                                Toast.makeText(context, "Published simulated payload to $title", Toast.LENGTH_SHORT).show()
                            },
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan, contentColor = PremiumBg)
                        ) {
                            Icon(imageVector = icon, contentDescription = "Publish Icon", Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SHADOW DISPATCH", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- SYSTEM SECURITY AUDIT LEDGER PANEL ---
@Composable
fun SecurityLedgerPanel(viewModel: ViralClipViewModel) {
    val ledgerLogs by viewModel.ledgerLogs.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "DEEPFAKE-GUARD AUDIT SYSTEM",
            color = AccentGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "Immutable event log anchored in cryptographic key chains, documenting Q2 SynthID & C2PA compliance logs",
            color = GrayMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CRYPTOGRAPHIC COMPUTE SIGNATURE RECORD",
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ledgerLogs.forEach { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = entry.id,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(SurfaceDarkLighter)
                                    .border(1.dp, CardBorder, CircleShape)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = entry.provenanceBadge,
                                    color = AccentGreen,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = entry.actionName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Secondary row for timestamp, cost logic
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Time: ${entry.timestamp}",
                                color = GrayMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "${if (entry.creditsExpended < 0) "+" else ""}${String.format(Locale.US, "%.1f", -entry.creditsExpended)} GPU C-Units",
                                color = if (entry.creditsExpended < 0) AccentGreen else GoldWarm,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }

                        // Crypt signature line
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(PremiumBg)
                                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = entry.blockSignature,
                                color = GrayMuted,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- UTILITY ---
private fun getTabIndex(tab: String): Int {
    return when (tab) {
        "Workspace" -> 0
        "Hook Engine" -> 1
        "Face-Vault" -> 2
        "Audio Lab" -> 3
        "Publish Sync" -> 4
        "Security Ledger" -> 5
        else -> 0
    }
}
