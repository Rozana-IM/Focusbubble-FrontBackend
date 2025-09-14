package com.focusbubble.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusbubble.R
import kotlinx.coroutines.delay

@Composable
fun FocusSessionScreen(
    durationMinutes: Int,
    onStop: () -> Unit,
    onFinish: () -> Unit,
    onFocusSessionsClick: () -> Unit

) {
    var timeLeft by remember { mutableStateOf(durationMinutes * 60) }
    var isPaused by remember { mutableStateOf(false) }

    // Countdown logic
    LaunchedEffect(timeLeft, isPaused) {
        if (!isPaused && timeLeft > 0) {
            delay(1000L)
            timeLeft--
            if (timeLeft == 0) {
                onFinish()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background same as Dashboard
        Image(
            painter = painterResource(id = R.drawable.dashboard_bg),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Spacer to push content slightly up
            Spacer(modifier = Modifier.height(40.dp))

            // Circle timer with white border
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .drawBehind {
                        val sweep = 360f * (timeLeft.toFloat() / (durationMinutes * 60))
                        drawArc(
                            color = Color.White,
                            startAngle = -90f,
                            sweepAngle = sweep,
                            useCenter = false,
                            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
            ) {
                Text(
                    text = String.format("%02d:%02d", timeLeft / 60, timeLeft % 60),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mascot image
            Image(
                painter = painterResource(id = R.drawable.bubbly_icon),
                contentDescription = "Mascot",
                modifier = Modifier
                    .size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Buttons row (Stop / Pause)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onStop,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Stop focusing")
                }

                Button(
                    onClick = { isPaused = !isPaused },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(if (isPaused) "Resume" else "Pause")
                }
            }
        }
    }
}
