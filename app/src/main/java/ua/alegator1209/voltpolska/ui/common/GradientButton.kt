package ua.alegator1209.voltpolska.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.alegator1209.voltpolska.ui.theme.Blue
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme

@Composable
fun GradientButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.primary,
            disabledContentColor = MaterialTheme.colors.primary.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp),
        modifier = modifier.background(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    MaterialTheme.colors.secondary,
                    Blue,
                    MaterialTheme.colors.secondary,
                )
            ),
            shape = MaterialTheme.shapes.large,
            alpha = if (enabled) 1f else 0.5f
        ),
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
