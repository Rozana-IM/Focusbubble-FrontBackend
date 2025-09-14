package com.focusbubble.ui.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesSheet(
    initialSelected: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (selectedQuotes: List<String>) -> Unit
) {
    val quotes = listOf(
        "Motivational",
        "Focus",
        "Productivity",
        "Mindfulness",
        "Inspirational",
        "Breakup Motivational",
        "Mother Love"
    )

    var selectedQuotes by remember { mutableStateOf(initialSelected.toMutableSet()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1C),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Select Quote Categories", fontSize = 20.sp, color = Color.White)
            Spacer(Modifier.height(12.dp))

            LazyColumn {
                items(quotes) { quote ->
                    val isSelected = selectedQuotes.contains(quote)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2C2C2C), androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(quote, color = Color.White, modifier = Modifier.weight(1f))
                        Switch(
                            checked = isSelected,
                            onCheckedChange = { checked ->
                                if (checked) selectedQuotes.add(quote) else selectedQuotes.remove(quote)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF3D8DFF)
                            )
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    onConfirm(selectedQuotes.toList())
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
            ) {
                Text("Confirm", fontSize = 16.sp)
            }
        }
    }
}
