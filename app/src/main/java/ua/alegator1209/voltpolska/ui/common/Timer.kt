package ua.alegator1209.voltpolska.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ua.alegator1209.voltpolska.ui.theme.Blue
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme

@Composable
fun Timer(
    value: Int,
    modifier: Modifier = Modifier,
    size: Dp = 86.dp,
    stroke: Dp = 8.dp,
    gradient: List<Color> = listOf(
        MaterialTheme.colors.secondary,
        Blue,
        MaterialTheme.colors.secondary,
    )
) {
    val transition = rememberInfiniteTransition()
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(1000))
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation),
            onDraw = {
                drawCircle(
                    brush = Brush.horizontalGradient(gradient),
                    center = this.center,
                    radius = (this.size.minDimension - stroke.toPx()) / 2,
                    style = Stroke(width = stroke.toPx())
                )
            }
        )

        Text(
            text = value.toString(),
            fontSize = 20.sp,
        )
    }
}

@Preview
@Composable
private fun TimerPreview() {
    VoltPolskaTheme {
        Surface {
            Timer(value = 99)
        }
    }
}
