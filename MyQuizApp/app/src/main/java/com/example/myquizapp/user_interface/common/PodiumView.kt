package com.example.myquizapp.user_interface.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myquizapp.data.model.Participant
import com.example.myquizapp.theme.Brand700
import com.example.myquizapp.theme.BrandAccent
import com.example.myquizapp.theme.White50
import com.example.myquizapp.theme.White950

/**
 * PodiumView.kt - Top 3 winners podium component
 */
@Composable
fun PodiumView(
    firstPlace: Participant?,
    secondPlace: Participant?,
    thirdPlace: Participant?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place
        if (secondPlace != null) {
            PodiumColumn(
                participant = secondPlace,
                rank = 2,
                podiumHeight = 100,
                podiumColor = Color(0xFFC0C0C0) // Silver
            )
        } else {
            Spacer(modifier = Modifier.width(80.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 1st Place
        if (firstPlace != null) {
            PodiumColumn(
                participant = firstPlace,
                rank = 1,
                podiumHeight = 130,
                podiumColor = BrandAccent // Gold
            )
        } else {
            Spacer(modifier = Modifier.width(80.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        // 3rd Place
        if (thirdPlace != null) {
            PodiumColumn(
                participant = thirdPlace,
                rank = 3,
                podiumHeight = 70,
                podiumColor = Color(0xFFCD7F32) // Bronze
            )
        } else {
            Spacer(modifier = Modifier.width(80.dp))
        }
    }
}

@Composable
private fun PodiumColumn(
    participant: Participant,
    rank: Int,
    podiumHeight: Int,
    podiumColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        RankedParticipantAvatar(
            name = participant.name,
            rank = rank,
            size = 64.dp
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Podium
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(podiumHeight.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(podiumColor),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text(
                    text = when (rank) {
                        1 -> "I"
                        2 -> "II"
                        3 -> "III"
                        else -> "$rank"
                    },
                    color = White50,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        // Name and score below podium
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$rank.",
            color = White50,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = participant.name,
            color = White50,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "(${participant.score}pts)",
            color = White950,
            fontSize = 12.sp
        )
    }
}
