package com.example.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GoalTargetSettingsDialog(
    currentGoal: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var goalVal by remember { mutableStateOf(currentGoal.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Set Dailey Targetee 💧🎯",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "A basic recommendatshun is 2,500ml per day. Adjust according to your health planns! 🥗🏃‍♂️",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = goalVal,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            goalVal = input
                        }
                    },
                    label = { Text("Dailey target (in ml) 🥛") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_goal_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = goalVal.toIntOrNull() ?: 2500
                    onConfirm(parsed)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                modifier = Modifier.testTag("dialog_goal_save_button")
            ) {
                Text("Save Target", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_goal_cancel_button")
            ) {
                Text("Cancel", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
