package ua.alegator1209.voltpolska.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ua.alegator1209.voltpolska.R
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme
import ua.alegator1209.voltpolska.ui.theme.Yellow

@Composable
fun Error(
    message: String,
    modifier: Modifier = Modifier,
    buttonText: String = "",
    onButtonClick: () -> Unit = {},
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(16.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_error),
            contentDescription = stringResource(id = R.string.error)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.body1,
            textAlign = TextAlign.Center,
        )

        if (buttonText.isNotBlank()) {
            TextButton(
                onClick = onButtonClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Yellow,
                    disabledContentColor = Yellow.copy(alpha = 0.5f)
                )
            ) {
                Text(text = buttonText, style = MaterialTheme.typography.body1)
            }
        }
    }
}

@Preview
@Composable
private fun ErrorPreview() {
    VoltPolskaTheme {
        Surface {
            Error(
                message = "The app needs Bluetooth to function",
                buttonText = "Enable Bluetooth",
                onButtonClick = {}
            )
        }
    }
}


@Preview
@Composable
private fun ErrorPreviewWithoutButton() {
    VoltPolskaTheme {
        Surface {
            Error(message = "The app needs Bluetooth to function")
        }
    }
}
