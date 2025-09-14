// QuotesMultiSelectSheet.kt
package com.focusbubble.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesMultiSelectSheet(
    selectedQuotes: Set<String>,  // Immutable set here
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    val allQuotes = listOf(
        "Motivational", "Focus", "Productivity", "Mindfulness",
        "Inspirational", "Breakup Motivational", "Mother Love"
    )

    val selectedState = remember(selectedQuotes) { mutableStateOf(selectedQuotes.toMutableSet()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1C),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Select Quote Categories", fontSize = 20.sp, color = Color.White)
            Spacer(Modifier.height(12.dp))

            Column {
                allQuotes.forEach { quote ->
                    val isSelected = selectedState.value.contains(quote)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .toggleable(
                                value = isSelected,
                                onValueChange = {
                                    if (it) selectedState.value.add(quote)
                                    else selectedState.value.remove(quote)
                                    selectedState.value = selectedState.value.toMutableSet()
                                }
                            )
                            .background(
                                if (isSelected) Color(0xFF0D47A1) else Color(0xFF2C2C2C),
                                RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = quote,
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = isSelected,
                            onCheckedChange = null,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF2196F3),
                                checkedTrackColor = Color(0xFF0D47A1)
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { onConfirm(selectedState.value) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Confirm")
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}
