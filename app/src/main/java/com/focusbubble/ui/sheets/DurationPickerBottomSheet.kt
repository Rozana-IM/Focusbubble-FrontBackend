package com.focusbubble.ui.sheets
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DurationPickerBottomSheet(
    initialHours: Int,
    initialMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val hourRange = (0..11).toList()
    val minuteRange = (0..59).toList()
    val visibleCount = 5 // Odd number for center alignment

    val hoursState = rememberLazyListState(initialHours)
    val minutesState = rememberLazyListState(initialMinutes)

    var selectedHour by remember { mutableStateOf(initialHours) }
    var selectedMinute by remember { mutableStateOf(initialMinutes) }

    LaunchedEffect(hoursState.firstVisibleItemIndex) {
        selectedHour = hoursState.firstVisibleItemIndex
    }

    LaunchedEffect(minutesState.firstVisibleItemIndex) {
        selectedMinute = minutesState.firstVisibleItemIndex
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1C1C1C),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Set duration for your focus session.",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                PickerLazyColumn(
                    items = hourRange,
                    state = hoursState,
                    selectedIndex = selectedHour,
                    visibleCount = visibleCount,
                    label = { "$it" }
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "hours",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Spacer(Modifier.width(24.dp))

                PickerLazyColumn(
                    items = minuteRange,
                    state = minutesState,
                    selectedIndex = selectedMinute,
                    visibleCount = visibleCount,
                    label = { "$it" }
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "mins",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { onConfirm(selectedHour, selectedMinute) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(32.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                Text("Confirm", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PickerLazyColumn(
    items: List<Int>,
    state: LazyListState,
    selectedIndex: Int,
    visibleCount: Int,
    label: (Int) -> String
) {
    val padding = (visibleCount / 2) * 50

    LazyColumn(
        state = state,
        modifier = Modifier
            .height((visibleCount * 50).dp)
            .width(80.dp)
            .background(Color.Transparent),
        contentPadding = PaddingValues(vertical = padding.dp),
        flingBehavior = rememberSnapFlingBehavior(state)
    ) {
        itemsIndexed(items) { idx, item ->
            val isSelected = idx == selectedIndex

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(
                        if (isSelected) Color(0xFF4CAF50) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    label(item),
                    color = if (isSelected) Color.Black else Color.White,
                    fontSize = if (isSelected) 24.sp else 18.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}
