package com.example.myquizapp.user_interface.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myquizapp.theme.Black500
import com.example.myquizapp.theme.Green500
import com.example.myquizapp.theme.Red500
import com.example.myquizapp.theme.White50

/**
 * AnswerOptionCard.kt - Answer option button with styled border
 */
@Composable
fun AnswerOptionCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isCorrect: Boolean? = null,
    isEnabled: Boolean = true
) {
    val borderColor = when {
        isCorrect == true -> Green500
        isCorrect == false && isSelected -> Red500
        isSelected -> Green500
        else -> Green500.copy(alpha = 0.6f)
    }

    val backgroundColor = when {
        isCorrect == true -> Green500.copy(alpha = 0.2f)
        isCorrect == false && isSelected -> Red500.copy(alpha = 0.2f)
        isSelected -> Green500.copy(alpha = 0.1f)
        else -> Black500
    }

    Card(
        onClick = { if (isEnabled) onClick() },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        enabled = isEnabled,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
        ),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Text(
            text = text,
            color = White50,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp)
        )
    }
}
