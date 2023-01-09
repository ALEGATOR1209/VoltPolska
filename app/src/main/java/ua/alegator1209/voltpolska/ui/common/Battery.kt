package ua.alegator1209.voltpolska.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.alegator1209.voltpolska.R
import ua.alegator1209.voltpolska.ui.theme.Malachite
import ua.alegator1209.voltpolska.ui.theme.Red
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme

@Composable
fun Battery(
  capacity: Float,
  status: BatteryStatus,
  modifier: Modifier = Modifier
) {
  Row(modifier = modifier) {
    val strokeColor = MaterialTheme.colors.secondary
    val fillColor = Malachite
    val capacityState by animateFloatAsState(targetValue = capacity)
    val chargeIcon = ImageVector.vectorResource(id = R.drawable.ic_charge)
    val chargeIconPainter = rememberVectorPainter(image = chargeIcon)

    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(120.dp),
      onDraw = {
        val dp2 = 2.dp.toPx()
        val dp8 = 8.dp.toPx()
        val bodyPercent = 0.9f
        val bodyWidth = size.width * bodyPercent - dp2 * 2
        val contactSize = Size(size.width - bodyWidth, size.height / 2)
        val contactOffset = Offset(bodyWidth, (size.height - contactSize.height) / 2)
        val cornerRadius = CornerRadius(dp8)

        drawRoundRect(
          fillColor,
          topLeft = Offset(dp2, dp2),
          size = size.copy(
            width = if (capacityState >= bodyPercent) {
              bodyWidth
            } else {
              bodyWidth * (capacityState + 1.0f - bodyPercent)
            },
            height = size.height - dp2 * 2,
          ),
          cornerRadius = cornerRadius,
        )

        with(chargeIconPainter) {
          val scale = 0.6f * size.height / intrinsicSize.height
          val iconSize = intrinsicSize * scale
          val verticalInset = (size.height - iconSize.height) / 2

          val colorFilter = when (status) {
            BatteryStatus.CHARGING -> ColorFilter.tint(strokeColor)
            BatteryStatus.STAND_BY -> ColorFilter.tint(Color.Transparent)
            BatteryStatus.DISCHARGING -> ColorFilter.tint(Red)
          }

          inset(
            top = verticalInset,
            bottom = verticalInset,
            left = (bodyWidth - iconSize.width) / 2,
            right = (bodyWidth - iconSize.width) / 2 + contactSize.width,
          ) {
            draw(
              size,
              colorFilter = colorFilter
            )
          }
        }

        drawRoundRect(
          strokeColor,
          topLeft = Offset(dp2, dp2),
          size = size.copy(width = bodyWidth, height = size.height - 2 * dp2),
          cornerRadius = cornerRadius,
          Stroke(width = dp2)
        )

        inset(
          left = contactOffset.x + dp2,
          top = contactOffset.y + dp2,
          right = dp2,
          bottom = contactOffset.y + dp2
        ) {
          if (capacityState > bodyPercent) {
            val path = Path().apply {
              addRoundRect(
                RoundRect(
                  rect = Rect(
                    Offset(0f, dp2),
                    size.copy(
                      width = size.width * (capacityState - bodyPercent) / (1.0f - bodyPercent)
                    )
                  ),
                  topRight = cornerRadius,
                  bottomRight = cornerRadius,
                )
              )
            }

            drawPath(path, fillColor)
          }

          val path = Path().apply {
            addRoundRect(
              RoundRect(
                rect = Rect(Offset(0f, dp2), size),
                topRight = cornerRadius,
                bottomRight = cornerRadius,
              )
            )
          }

          drawPath(
            path,
            strokeColor,
            style = Stroke(width = dp2)
          )
        }
      }
    )
  }
}

enum class BatteryStatus {
  CHARGING,
  STAND_BY,
  DISCHARGING,
}

@Preview(widthDp = 320)
@Composable
private fun BatteryPreview() {
  VoltPolskaTheme {
    Surface {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Battery(
          modifier = Modifier.fillMaxWidth(),
          capacity = 1.0f,
          status = BatteryStatus.STAND_BY,
        )

        var clicked by remember { mutableStateOf(false) }

        Battery(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { clicked = !clicked },
          capacity = if (clicked) 1.0f else 0.5f,
          status = if (clicked) BatteryStatus.CHARGING else BatteryStatus.DISCHARGING,
        )
      }
    }
  }
}
