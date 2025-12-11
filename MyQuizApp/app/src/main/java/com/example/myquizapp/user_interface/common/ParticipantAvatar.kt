package com.example.myquizapp.user_interface.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myquizapp.theme.Brand400
import com.example.myquizapp.theme.Brand500
import com.example.myquizapp.theme.Brand600
import com.example.myquizapp.theme.Green400
import com.example.myquizapp.theme.Green500
import com.example.myquizapp.theme.White50
import com.example.myquizapp.theme.Yellow500
import com.example.myquizapp.theme.BrandAccent

/**
 * ParticipantAvatar.kt - Circular avatar component for participants
 */
@Composable
fun ParticipantAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    colorIndex: Int = 0
) {
    val colors = listOf(
        Brand500,
        Green500,
        BrandAccent,
        Brand400,
        Green400,
        Yellow500
    )
    val backgroundColor = colors[colorIndex % colors.size]
    val initial = name.firstOrNull()?.uppercaseChar() ?: '?'

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial.toString(),
            color = White50,
            fontSize = (size.value * 0.4f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RankedParticipantAvatar(
    name: String,
    rank: Int,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp
) {
    val backgroundColor = when (rank) {
        1 -> BrandAccent // Gold for 1st
        2 -> Color(0xFFC0C0C0) // Silver for 2nd
        3 -> Color(0xFFCD7F32) // Bronze for 3rd
        else -> Brand500
    }
    val initial = name.firstOrNull()?.uppercaseChar() ?: '?'

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial.toString(),
            color = White50,
            fontSize = (size.value * 0.4f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
