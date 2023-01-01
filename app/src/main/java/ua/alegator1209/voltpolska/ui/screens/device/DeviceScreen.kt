package ua.alegator1209.voltpolska.ui.screens.device

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ua.alegator1209.voltpolska.R

@Composable
fun DeviceScreen(
    viewModel: DeviceViewModel,
) {
    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colors.primary,
                modifier = Modifier.fillMaxWidth(),
                elevation = 8.dp
            ) {
                Row(
                    Modifier.padding(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.device_screen),
                        style = MaterialTheme.typography.h1,
                    )
                }
            }
        }
    ) {
        val state = viewModel.deviceState

        Column {
            when (state) {
                is DeviceViewModel.DeviceUiState.Data -> {
                    Text(text = state.data.toString())
                }
                DeviceViewModel.DeviceUiState.Error -> {
                    Text(text = "Error")
                }
                DeviceViewModel.DeviceUiState.Loading -> {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
