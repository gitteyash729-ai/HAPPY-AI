package com.example.happy.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.happy.data.entity.Sender
import com.example.happy.engine.EmotionType
import com.example.happy.ui.components.AvatarCompanionCard
import com.example.happy.ui.components.ChatMessageItem
import com.example.happy.ui.components.EmotionMoodSelector
import com.example.happy.ui.components.LanguageSelectorDialog
import com.example.happy.ui.components.MemoryVaultSheet
import com.example.happy.ui.components.PrivacyBadge
import com.example.happy.ui.viewmodel.HappyViewModel
import com.example.ui.theme.WarmOrange
import com.example.ui.theme.WarmOrangeDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HappyMainScreen(
    viewModel: HappyViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val memories by viewModel.memories.collectAsStateWithLifecycle()
    val messageCount by viewModel.messageCount.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showMemoryVaultSheet by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    // Autoscroll to bottom when messages update
    LaunchedEffect(messages.size, uiState.isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Friendship level calculation
    val friendshipLevel = when {
        messageCount >= 60 -> 5
        messageCount >= 30 -> 4
        messageCount >= 15 -> 3
        messageCount >= 5 -> 2
        else -> 1
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isWideScreen = maxWidth >= 720.dp

        if (isWideScreen) {
            // Adaptive PC / Tablet Layout (Two Panes)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // Left Companion & Stats Pane
                Column(
                    modifier = Modifier
                        .width(360.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    AvatarCompanionCard(
                        currentEmotion = uiState.currentEmotion,
                        isTyping = uiState.isTyping,
                        isAutoSpeakEnabled = uiState.isAutoSpeakEnabled,
                        onToggleAutoSpeak = { viewModel.toggleAutoSpeak() },
                        friendshipLevel = friendshipLevel,
                        totalMessages = messageCount,
                        isCompact = false
                    )

                    PrivacyBadge(modifier = Modifier.fillMaxWidth())

                    // Language Selector Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLanguageDialog = true }
                            .testTag("pc_language_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = null,
                                    tint = WarmOrangeDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Language Mode",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                    Text(
                                        text = "${uiState.selectedLanguage.label} (${uiState.selectedLanguage.nativeName})",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                            Text(
                                text = "Change",
                                style = MaterialTheme.typography.labelSmall.copy(color = WarmOrangeDark, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Memory Vault Shortcut Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMemoryVaultSheet = true }
                            .testTag("pc_memory_vault_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = WarmOrangeDark,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Memory Vault",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "${memories.size} facts remembered locally",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                            }
                            Text(
                                text = "View",
                                style = MaterialTheme.typography.labelSmall.copy(color = WarmOrangeDark, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Quick Emotion Trigger buttons
                    Text(
                        text = "QUICK EMOTIONS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    EmotionMoodSelector(
                        onSelectPrompt = { prompt, emotion ->
                            viewModel.setEmotionMood(emotion)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Vertical Divider
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )

                // Right Chat Pane
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    // Chat Top Bar
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "HAPPY - Your AI Friend",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                                )
                                Text(
                                    text = "Real brotherly support • Marathi, Hindi & English",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.clearAllChat() },
                                    modifier = Modifier.testTag("top_bar_clear_chat")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteOutline,
                                        contentDescription = "Clear Chat",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Message list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            ChatMessageItem(
                                message = msg,
                                onSpeakClick = { viewModel.speakMessage(msg) }
                            )
                        }

                        if (uiState.isTyping) {
                            item {
                                TypingIndicatorRow()
                            }
                        }
                    }

                    // Input Bar
                    ChatInputBar(
                        inputText = inputText,
                        onInputChanged = { inputText = it },
                        onSendMessage = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        onQuickPrompt = { prompt, emotion ->
                            viewModel.setEmotionMood(emotion)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .imePadding()
                    )
                }
            }
        } else {
            // Mobile Layout (Compact)
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "HAPPY",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            letterSpacing = 1.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = uiState.currentEmotion.emoji,
                                        fontSize = 16.sp
                                    )
                                }
                                Text(
                                    text = "भाऊ & मित्र • 100% Offline",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        color = WarmOrangeDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        },
                        navigationIcon = {
                            IconButton(
                                onClick = { showLanguageDialog = true },
                                modifier = Modifier.testTag("mobile_language_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Language",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = { viewModel.toggleAutoSpeak() },
                                modifier = Modifier.testTag("mobile_speak_toggle")
                            ) {
                                Icon(
                                    imageVector = if (uiState.isAutoSpeakEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                    contentDescription = "Voice toggle",
                                    tint = if (uiState.isAutoSpeakEnabled) WarmOrangeDark else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(
                                onClick = { showMemoryVaultSheet = true },
                                modifier = Modifier.testTag("mobile_memory_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Memory Vault",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Compact Avatar & Privacy Banner
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PrivacyBadge()

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { showMemoryVaultSheet = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Level $friendshipLevel Bro",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = WarmOrangeDark)
                                )
                            }
                        }
                    }

                    // Chat messages
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            ChatMessageItem(
                                message = msg,
                                onSpeakClick = { viewModel.speakMessage(msg) }
                            )
                        }

                        if (uiState.isTyping) {
                            item {
                                TypingIndicatorRow()
                            }
                        }
                    }

                    // Bottom Emotion Prompt Carousel
                    EmotionMoodSelector(
                        onSelectPrompt = { prompt, emotion ->
                            viewModel.setEmotionMood(emotion)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Input Field
                    ChatInputBar(
                        inputText = inputText,
                        onInputChanged = { inputText = it },
                        onSendMessage = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        onQuickPrompt = { prompt, emotion ->
                            viewModel.setEmotionMood(emotion)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .imePadding()
                    )
                }
            }
        }
    }

    // Language Selector Dialog
    if (showLanguageDialog) {
        LanguageSelectorDialog(
            currentLanguage = uiState.selectedLanguage,
            onSelectLanguage = { viewModel.setLanguage(it) },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Memory Vault Bottom Sheet
    if (showMemoryVaultSheet) {
        MemoryVaultSheet(
            memories = memories,
            totalMessages = messageCount,
            friendshipLevel = friendshipLevel,
            onAddMemory = { title, content, cat ->
                viewModel.addManualMemory(title, content, cat)
            },
            onDeleteMemory = { viewModel.deleteMemory(it) },
            onClearChat = { viewModel.clearAllChat() },
            onDismiss = { showMemoryVaultSheet = false }
        )
    }
}

@Composable
private fun ChatInputBar(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSendMessage: () -> Unit,
    onQuickPrompt: (String, EmotionType) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChanged,
                placeholder = {
                    Text(
                        text = "बोल भावा... Talk to Happy in मराठी, हिंदी or English",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_text_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent
                ),
                maxLines = 4
            )

            if (inputText.isNotBlank()) {
                IconButton(
                    onClick = { onInputChanged("") },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            FloatingActionButton(
                onClick = onSendMessage,
                containerColor = WarmOrangeDark,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .size(44.dp)
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Message",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun TypingIndicatorRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(start = 42.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Happy is typing...",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = WarmOrangeDark
                    )
                )
            }
        }
    }
}
