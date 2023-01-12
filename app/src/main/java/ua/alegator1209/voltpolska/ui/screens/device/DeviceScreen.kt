package ua.alegator1209.voltpolska.ui.screens.device

import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme

@Composable
fun DeviceScreen(
    viewModel: DeviceViewModel,
) {
    DeviceScreenStateless(UiState())
}

@Composable
private fun DeviceScreenStateless(
    state: UiState,
) {
    Scaffold(
        topBar = {
            ConstraintLayout(
                modifier = Modifier
            ) {
                val (title, divider) = createRefs()

                Text(
                    text = state.deviceName,
                    style = MaterialTheme.typography.h1,
                    modifier = Modifier
                        .constrainAs(title) {
                            top.linkTo(parent.top, margin = 16.dp)
                            start.linkTo(parent.start, margin = 16.dp)
                        }
                )

                Divider(
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .constrainAs(divider) {
                            top.linkTo(title.bottom, margin = 8.dp)
                            end.linkTo(title.end)
                        }
                )
            }
        }
    )
    {

    }
}

@Preview
@Composable
private fun DeviceScreenPreview() {
    VoltPolskaTheme {
        DeviceScreenStateless(
            state = UiState(
                deviceName = "12V100A1235"
            )
        )
    }
}
