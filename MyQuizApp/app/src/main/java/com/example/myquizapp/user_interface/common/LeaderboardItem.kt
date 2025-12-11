package com.example.myquizapp.user_interface.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.myquizapp.theme.Black500
import com.example.myquizapp.theme.BrandAccent
import com.example.myquizapp.theme.White50
import com.example.myquizapp.theme.White950

/**
 * LeaderboardItem.kt - Ranking row component
 */
@Composable
fun LeaderboardItem(
    participant: Participant,
    rank: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Black500.copy(alpha = 0.6f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (rank) {
                            1 -> BrandAccent
                            2 -> Color(0xFFC0C0C0)
                            3 -> Color(0xFFCD7F32)
                            else -> White950
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$rank",
                    color = White50,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Avatar
            RankedParticipantAvatar(
                name = participant.name,
                rank = rank,
                size = 40.dp
            )
            Spacer(modifier = Modifier.width(12.dp))
            // Name and score
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = participant.name,
                    color = White50,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${participant.score} pts",
                    color = White950,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TopThreeStandings(
    participants: List<Participant>,
    modifier: Modifier = Modifier
) {
    if (participants.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd place (left)
        if (participants.size > 1) {
            StandingItem(
                participant = participants[1],
                rank = 2,
                height = 80.dp.value.toInt()
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        // 1st place (center, tallest)
        if (participants.isNotEmpty()) {
            StandingItem(
                participant = participants[0],
                rank = 1,
                height = 100.dp.value.toInt()
            )
        }
        // 3rd place (right)
        if (participants.size > 2) {
            StandingItem(
                participant = participants[2],
                rank = 3,
                height = 60.dp.value.toInt()
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StandingItem(
    participant: Participant,
    rank: Int,
    height: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        RankedParticipantAvatar(
            name = participant.name,
            rank = rank,
            size = 56.dp
        )
        Text(
            text = "$rank.",
            color = White50,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = participant.name,
            color = White50,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "(${participant.score} pts)",
            color = White950,
            fontSize = 12.sp
        )
    }
}
