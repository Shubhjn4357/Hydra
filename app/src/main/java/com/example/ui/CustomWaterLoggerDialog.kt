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
fun CustomWaterLoggerDialog(
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var mlVal by remember { mutableStateOf("250") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Custom Amount 🚰",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Specify a custom amount of water details consumed to add to your daily log:",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = mlVal,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            mlVal = input
                        }
                    },
                    label = { Text("Water logged (in ml)") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_custom_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = mlVal.toIntOrNull() ?: 250
                    onConfirm(parsed)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                modifier = Modifier.testTag("dialog_custom_add_button")
            ) {
                Text("Log Intake", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_custom_cancel_button")
            ) {
                Text("Cancel", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}
