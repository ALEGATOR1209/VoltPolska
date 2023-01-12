package ua.alegator1209.voltpolska.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import ua.alegator1209.voltpolska.R
import ua.alegator1209.voltpolska.ui.theme.Blue
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme

@Composable
fun ScanResult(
  name: String,
  address: String,
  onConnect: () -> Unit,
  modifier: Modifier = Modifier,
) {
  ConstraintLayout(
    modifier = modifier
      .fillMaxWidth()
      .background(Blue.copy(alpha = 0.4f), shape = RoundedCornerShape(16.dp))
  ) {
    val (nameLabel, addressLabel, button) = createRefs()

    Text(
      text = name,
      style = MaterialTheme.typography.body1,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.constrainAs(nameLabel) {
        top.linkTo(parent.top, 16.dp)
        start.linkTo(parent.start, 8.dp)
        end.linkTo(button.start, 8.dp)
        bottom.linkTo(addressLabel.top, 4.dp)

        width = Dimension.fillToConstraints
      }
    )

    Text(
      text = address,
      style = MaterialTheme.typography.body2,
      color = MaterialTheme.colors.primary.copy(alpha = 0.5f),
      fontWeight = FontWeight.Bold,
      modifier = Modifier.constrainAs(addressLabel) {
        top.linkTo(nameLabel.bottom)
        start.linkTo(nameLabel.start)
        end.linkTo(nameLabel.end)
        bottom.linkTo(parent.bottom, 16.dp)

        width = Dimension.fillToConstraints
      }
    )

    Button(
      onClick = onConnect,
      shape = RoundedCornerShape(
        topStart = 0.dp,
        bottomStart = 0.dp,
        topEnd = 8.dp,
        bottomEnd = 8.dp,
      ),
      colors = ButtonDefaults.buttonColors(
        backgroundColor = MaterialTheme.colors.secondary,
      ),
      modifier = Modifier.constrainAs(button) {
        height = Dimension.fillToConstraints
        top.linkTo(parent.top)
        bottom.linkTo(parent.bottom)
        end.linkTo(parent.end)
      }
    ) {
      Text(
        text = stringResource(id = R.string.device_connect_btn),
        color = MaterialTheme.colors.primary,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Preview
@Composable
private fun ScanResultPreview() {
  VoltPolskaTheme {
    Surface {
      ScanResult(
        name = "12V100A1234",
        address = "11:22:33:ee:ff",
        onConnect = {},
      )
    }
  }
}
