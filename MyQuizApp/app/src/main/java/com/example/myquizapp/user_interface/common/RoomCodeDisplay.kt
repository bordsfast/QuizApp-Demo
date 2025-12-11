package com.example.myquizapp.user_interface.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myquizapp.theme.Brand600
import com.example.myquizapp.theme.Brand700
import com.example.myquizapp.theme.White50
import com.example.myquizapp.theme.White950

/**
 * RoomCodeDisplay.kt - Displays room code with share option
 */
@Composable
fun RoomCodeDisplay(
    roomCode: String,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brand700)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Room $roomCode",
            color = White50,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        IconButton(
            onClick = onShareClick,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Brand600)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share room code",
                tint = White50,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Share Link",
            color = White950,
            fontSize = 14.sp
        )
    }
}
