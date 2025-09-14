package com.focusbubble.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusbubble.R
import kotlin.random.Random

data class Bubble(
    var x: Float,
    var y: Float,
    val radius: Float,
    val speed: Float,
    val alpha: Float,
    val color: Color,
    val scaleAnim: Animatable<Float, AnimationVector1D>
)

@Composable
fun BubblyIntroScreen(onContinue: () -> Unit, modifier: Modifier = Modifier) {
    val screenWidth = 1080f
    val screenHeight = 1920f

    val bubbles = remember {
        List(30) {
            Bubble(
                x = Random.nextFloat() * screenWidth,
                y = Random.nextFloat() * screenHeight,
                radius = Random.nextFloat() * 20f + 10f,
                speed = Random.nextFloat() * 2f + 1f,
                alpha = Random.nextFloat() * 0.5f + 0.3f,
                color = Color(
                    red = Random.nextFloat(),
                    green = Random.nextFloat(),
                    blue = Random.nextFloat(),
                    alpha = Random.nextFloat() * 0.5f + 0.3f
                ),
                scaleAnim = Animatable(1f)
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Black background
        Box(modifier = Modifier.fillMaxSize().background(Color.Black))

        // Animated bubbles
        Canvas(modifier = Modifier.fillMaxSize()) {
            bubbles.forEach { bubble ->
                bubble.y -= bubble.speed
                if (bubble.y + bubble.radius < 0f) {
                    bubble.y = size.height + bubble.radius
                    bubble.x = Random.nextFloat() * size.width
                }
                drawCircle(
                    color = bubble.color,
                    radius = bubble.radius * bubble.scaleAnim.value,
                    center = Offset(bubble.x, bubble.y),
                    alpha = bubble.alpha
                )
            }
        }

        // Animate bubbles scaling
        bubbles.forEach { bubble ->
            LaunchedEffect(bubble) {
                bubble.scaleAnim.animateTo(
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = Random.nextInt(2000, 4000), easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }

        // Mascot image overlay and text
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.bubbly_icon),
                contentDescription = "Bubbly Mascot",
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hi, I’m Bubbly",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Your productivity manager —",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Here to help you stay organized and focused",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }

        // Continue button at bottom
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Recompose every 16ms (~60fps) for smooth bubble animation
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(16)
            bubbles.forEach { it.y -= it.speed }
        }
    }
}
