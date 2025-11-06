package com.ucb.morfeo.features.core.maintenance.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.R

@Composable
fun AnimatedGearIcon() {
    val infiniteRotation = rememberInfiniteTransition(label = "gearRotation")
    val angle by infiniteRotation.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing)
        ),
        label = "gearRotationAnim"
    )

    Box(
        modifier = Modifier
            .size(120.dp)
            .background(Color(0xFF1A1F35), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.icon_gear),
            contentDescription = null,
            tint = Color(0xFFFF7A00),
            modifier = Modifier
                .size(64.dp)
                .rotate(angle)
        )
    }
}
