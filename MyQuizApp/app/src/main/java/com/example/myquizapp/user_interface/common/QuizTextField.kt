package com.example.myquizapp.user_interface.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myquizapp.theme.Black300
import com.example.myquizapp.theme.Brand600
import com.example.myquizapp.theme.White200
import com.example.myquizapp.theme.White50
import com.example.myquizapp.theme.White700
import com.example.myquizapp.theme.White950

/**
 * QuizTextField.kt - Text field component styled for the quiz app
 */
@Composable
fun QuizTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = singleLine,
        placeholder = {
            Text(
                text = placeholder,
                color = White950
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = White950
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            capitalization = capitalization
        ),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = White50,
            unfocusedTextColor = White50,
            focusedContainerColor = Black300.copy(alpha = 0.5f),
            unfocusedContainerColor = Black300.copy(alpha = 0.5f),
            focusedBorderColor = Brand600,
            unfocusedBorderColor = White700,
            cursorColor = Brand600
        )
    )
}
