package ua.alegator1209.voltpolska.ui.common

import android.view.MotionEvent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.alegator1209.voltpolska.ui.theme.Blue
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GradientButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var touched by remember { mutableStateOf(false) }
    val animationSpec = tween<Float>(700, easing = LinearOutSlowInEasing)
    val blueLeftPoint by animateFloatAsState(if (touched) 0.3f else 0.5f, animationSpec)
    val blueRightPoint by animateFloatAsState(if (touched) 0.7f else 0.5f, animationSpec)

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.primary,
            disabledContentColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        modifier = modifier
            .background(
                brush = Brush.horizontalGradient(
                    0.0f to MaterialTheme.colors.secondary,
                    blueLeftPoint to Blue,
                    0.5f to Blue,
                    blueRightPoint to Blue,
                    1.0f to MaterialTheme.colors.secondary,
                ),
                shape = MaterialTheme.shapes.large,
                alpha = if (enabled) 1f else 0.5f
            )
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> touched = true
                    MotionEvent.ACTION_UP -> touched = false
                }
                false
            },
        enabled = enabled,
    ) {
        Text(text = text)
    }
}

@Preview(widthDp = 320)
@Composable
private fun GradientButtonPreview() {
    VoltPolskaTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            GradientButton(onClick = {}, text = "Enabled", modifier = Modifier.fillMaxWidth())
            GradientButton(onClick = {}, text = "Disabled", enabled = false, modifier = Modifier.fillMaxWidth())
        }
    }
}
