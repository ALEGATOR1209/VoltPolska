package ua.alegator1209.voltpolska.ui.screens.start

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ua.alegator1209.voltpolska.R
import ua.alegator1209.voltpolska.ui.common.Error
import ua.alegator1209.voltpolska.ui.common.GradientButton
import ua.alegator1209.voltpolska.ui.common.Timer
import ua.alegator1209.voltpolska.ui.permissions.Permissions
import ua.alegator1209.voltpolska.ui.theme.Cyan
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme

@ExperimentalPermissionsApi
@Composable
fun StartScreen(
    onDeviceConnected: () -> Unit,
    isBluetoothEnabled: Boolean,
    onEnableBluetooth: () -> Unit,
    isLocationEnabled: Boolean,
    onEnableLocation: () -> Unit,
    viewModel: StartViewModel
) {
    val bluetoothPermissions = rememberMultiplePermissionsState(Permissions.Bluetooth)
    val locationPermissions = rememberMultiplePermissionsState(Permissions.Location)
    val combinedPermissions = rememberMultiplePermissionsState(Permissions.Bluetooth + Permissions.Location)

    val bluetoothPermissionError = bluetoothPermissions.allPermissionsGranted.let { allGranted ->
        if (!allGranted) StartViewModel.UiState.Error.BluetoothPermissionNeeded else null
    }

    val locationPermissionError = locationPermissions.allPermissionsGranted.let { allGranted ->
        if (!allGranted) StartViewModel.UiState.Error.LocationPermissionNeeded else null
    }

    val combinedPermissionsError = if (bluetoothPermissionError != null && locationPermissionError != null) {
        StartViewModel.UiState.Error.BluetoothAndLocationPermissionNeeded
    } else null

    val bluetoothDisabledError = if (!isBluetoothEnabled) StartViewModel.UiState.Error.BluetoothDisabled else null

    val locationDisabledError = if (!isLocationEnabled) StartViewModel.UiState.Error.LocationDisabled else null

    val error = combinedPermissionsError
        ?: bluetoothPermissionError
        ?: locationPermissionError
        ?: bluetoothDisabledError
        ?: locationDisabledError
        ?: viewModel.uiState.error

    StartScreenStateless(
        viewModel.uiState.copy(error = error),
        onDeviceNameChange = {},
        onSearchClicked = {},
        onEnableBluetooth = onEnableBluetooth,
        onEnableLocation = onEnableLocation,
        onGiveBluetoothAccess = bluetoothPermissions::launchMultiplePermissionRequest,
        onGiveLocationAccess = locationPermissions::launchMultiplePermissionRequest,
        onGiveAllPermissions = combinedPermissions::launchMultiplePermissionRequest,
    )
}

@Composable
private fun StartScreenStateless(
    uiState: StartViewModel.UiState,
    onDeviceNameChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onGiveBluetoothAccess: () -> Unit,
    onEnableBluetooth: () -> Unit,
    onGiveLocationAccess: () -> Unit,
    onEnableLocation: () -> Unit,
    onGiveAllPermissions: () -> Unit,
) {
    Scaffold(
        topBar = {
            ConstraintLayout(
                modifier = Modifier
            ) {
                val (title, divider) = createRefs()

                Text(
                    text = stringResource(id = R.string.start_screen_title),
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
        },
        content = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(
                        top = 32.dp,
                        bottom = 16.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
            ) {
                DeviceNameField(
                    deviceName = uiState.deviceName,
                    enabled = !uiState.isSearchInProgress,
                    onDeviceNameChange = onDeviceNameChange,
                )

                Spacer(modifier = Modifier.height(32.dp))

                GradientButton(
                    onClick = onSearchClicked,
                    text = stringResource(id = R.string.search_btn),
                    enabled = !uiState.isSearchInProgress && uiState.error == StartViewModel.UiState.Error.None,
                    modifier = Modifier.fillMaxWidth()
                )

                ErrorState(
                    uiState.error,
                    onGiveLocationAccess = onGiveLocationAccess,
                    onGiveBluetoothAccess = onGiveBluetoothAccess,
                    onEnableLocation = onEnableLocation,
                    onEnableBluetooth = onEnableBluetooth,
                    onGiveAllPermissions = onGiveAllPermissions,
                )

                if (uiState.isSearchInProgress) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Timer(value = uiState.timerValue)
                }
            }
        }
    )
}

@Composable
private fun DeviceNameField(
    deviceName: String,
    enabled: Boolean,
    onDeviceNameChange: (String) -> Unit,
) {
    TextField(
        value = deviceName,
        enabled = enabled,
        onValueChange = onDeviceNameChange,
        textStyle = MaterialTheme.typography.body1,
        placeholder = {
            Text(
                text = stringResource(id = R.string.device_name_field),
                color = Cyan.copy(alpha = 0.5f),
                style = MaterialTheme.typography.body1,
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            textColor = Cyan,
            disabledTextColor = Cyan.copy(alpha = 0.5f),
            backgroundColor = Color.Transparent,
            cursorColor = MaterialTheme.colors.secondary,
            errorCursorColor = MaterialTheme.colors.secondary,
            focusedIndicatorColor = MaterialTheme.colors.secondary,
            unfocusedIndicatorColor = Cyan,
            disabledIndicatorColor = Cyan,
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ErrorState(
    error: StartViewModel.UiState.Error,
    onEnableBluetooth: () -> Unit,
    onEnableLocation: () -> Unit,
    onGiveAllPermissions: () -> Unit,
    onGiveBluetoothAccess: () -> Unit,
    onGiveLocationAccess: () -> Unit,
) {
    if (error != StartViewModel.UiState.Error.None) {
        Spacer(modifier = Modifier.height(32.dp))
    }

    when (error) {
        StartViewModel.UiState.Error.None -> {}
        StartViewModel.UiState.Error.BluetoothPermissionNeeded -> Error(
            message = stringResource(id = R.string.error_bluetooth_permission),
            buttonText = stringResource(id = R.string.error_bluetooth_permission_btn),
            onButtonClick = onGiveBluetoothAccess,
        )
        StartViewModel.UiState.Error.BluetoothDisabled -> Error(
            message = stringResource(id = R.string.error_bluetooth_disabled),
            buttonText = stringResource(id = R.string.error_bluetooth_disabled_btn),
            onButtonClick = onEnableBluetooth,
        )
        StartViewModel.UiState.Error.LocationPermissionNeeded -> Error(
            message = stringResource(id = R.string.error_location_permission),
            buttonText = stringResource(id = R.string.error_location_permission_btn),
            onButtonClick = onGiveLocationAccess,
        )
        StartViewModel.UiState.Error.LocationDisabled -> Error(
            message = stringResource(id = R.string.error_location_disabled),
            buttonText = stringResource(id = R.string.error_location_disabled_btn),
            onButtonClick = onEnableLocation,
        )
        StartViewModel.UiState.Error.BluetoothAndLocationPermissionNeeded -> Error(
            message = stringResource(id = R.string.error_all_permissions),
            buttonText = stringResource(id = R.string.error_all_permissions_btn),
            onButtonClick = onGiveAllPermissions,
        )
    }
}

@Preview
@Composable
private fun StartScreenPreview() {
    VoltPolskaTheme {
        StartScreenStateless(
            uiState = StartViewModel.UiState(),
            onDeviceNameChange = {},
            onSearchClicked = {},
            onEnableBluetooth = {},
            onEnableLocation = {},
            onGiveBluetoothAccess = {},
            onGiveLocationAccess = {},
            onGiveAllPermissions = {},
        )
    }
}

@Preview
@Composable
private fun StartScreenPreviewWithDeviceName() {
    VoltPolskaTheme {
        StartScreenStateless(
            uiState = StartViewModel.UiState(deviceName = "MyAccumulator"),
            onDeviceNameChange = {},
            onSearchClicked = {},
            onEnableBluetooth = {},
            onEnableLocation = {},
            onGiveBluetoothAccess = {},
            onGiveLocationAccess = {},
            onGiveAllPermissions = {},
        )
    }
}

@Preview
@Composable
private fun StartScreenPreviewWithSearch() {
    VoltPolskaTheme {
        StartScreenStateless(
            uiState = StartViewModel.UiState(
                deviceName = "MyAccumulator",
                isSearchInProgress = true,
                timerValue = 59,
            ),
            onDeviceNameChange = {},
            onSearchClicked = {},
            onEnableBluetooth = {},
            onEnableLocation = {},
            onGiveBluetoothAccess = {},
            onGiveLocationAccess = {},
            onGiveAllPermissions = {},
        )
    }
}

@Preview
@Composable
private fun StartScreenPreviewWithError() {
    VoltPolskaTheme {
        StartScreenStateless(
            uiState = StartViewModel.UiState(error = StartViewModel.UiState.Error.BluetoothDisabled),
            onDeviceNameChange = {},
            onSearchClicked = {},
            onEnableBluetooth = {},
            onEnableLocation = {},
            onGiveBluetoothAccess = {},
            onGiveLocationAccess = {},
            onGiveAllPermissions = {},
        )
    }
}
