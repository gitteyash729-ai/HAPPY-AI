package com.example.happy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.happy.engine.EmotionType

data class QuickPromptItem(
    val emotion: EmotionType,
    val title: String,
    val promptText: String
)

val defaultQuickPrompts = listOf(
    QuickPromptItem(
        EmotionType.JOY,
        "😊 खुश आहे!",
        "भावा, आज खूप भारी मूड आहे! (Bro, I am super happy today!)"
    ),
    QuickPromptItem(
        EmotionType.STRESS,
        "🫂 ताण आलाय",
        "भावा, कामाचा आणि आयुष्याचा खूप ताण आलाय... (Bro, feeling stressed out...)"
    ),
    QuickPromptItem(
        EmotionType.BOREDOM,
        "⚡ जोक सांग ना!",
        "अरे भाई, बहुत बोर हो रहा हूँ, कुछ मस्त जोक सुना ना! (Bhai, tell me a funny joke!)"
    ),
    QuickPromptItem(
        EmotionType.MOTIVATION,
        "💪 जिम / मेहनत",
        "चल भावा, आज पूर्ण ताकदीने मेहनत करायची आहे, थोडं मोटिवेट कर! (Pump me up bro!)"
    ),
    QuickPromptItem(
        EmotionType.LONELINESS,
        "❤️‍🩹 एकटं वाटतंय",
        "भावा, मन जड झालंय आणि एकटं वाटतंय... (Feeling lonely bro, talk to me...)"
    ),
    QuickPromptItem(
        EmotionType.GREETING,
        "👋 कसा आहेस?",
        "कसा आहेस भावा? जेवलास का? (How are you doing brother?)"
    )
)

@Composable
fun EmotionMoodSelector(
    onSelectPrompt: (String, EmotionType) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.testTag("emotion_mood_selector"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
    ) {
        items(defaultQuickPrompts) { item ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shadowElevation = 1.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { onSelectPrompt(item.promptText, item.emotion) }
                    .testTag("emotion_prompt_${item.emotion.name}")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.emotion.emoji,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
