package ua.alegator1209.voltpolska.ui.screens.start

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import ua.alegator1209.voltpolska.R
import ua.alegator1209.voltpolska.ui.permissions.Permissions
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme

@ExperimentalPermissionsApi
@Composable
fun StartScreen(viewModel: StartViewModel) {
    val permissionsState = rememberMultiplePermissionsState(Permissions.Required)

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
                        text = stringResource(id = R.string.scan_devices),
                        style = MaterialTheme.typography.h1,
                    )
                }
            }
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (!permissionsState.allPermissionsGranted) {
                PermissionsRequest(
                    onRequestPermission = { permissionsState.launchMultiplePermissionRequest() },
                )
            }

            Scanner(
                state = viewModel.scanState,
                devices = viewModel.devices,
                onStartScan = viewModel::startScan,
                onStopScan = viewModel::stopScan,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp))
        }
    }
}

@Composable
private fun PermissionsRequest(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        elevation = 8.dp,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colors.primary,
        modifier = modifier.padding(32.dp),
    ) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.permission_request_desc),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    contentColor = MaterialTheme.colors.primary,
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(id = R.string.permission_request_btn))
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun Scanner(
    state: ScanState,
    devices: List<BluetoothDevice>,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = when (state) {
                ScanState.NOT_STARTED -> R.string.scanner_scan_not_started
                ScanState.IN_PROGRESS -> R.string.scanner_scan_in_progress
                ScanState.FINISHED -> R.string.scanner_scan_finished
            }),
            style = MaterialTheme.typography.h2,
        )

        if (state != ScanState.IN_PROGRESS) {
            Button(onClick = onStartScan) {
                Text(text = stringResource(id = R.string.scanner_start_scan))
            }
        } else {
            Button(onClick = onStopScan) {
                Text(text = stringResource(id = R.string.scanner_stop_scan))
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (device in devices) {
                item(key = device.address) {
                    Column {
                        Text(
                            text = device?.name
                                .takeUnless { it.isNullOrBlank() }
                                ?: stringResource(id = R.string.scanner_device_no_name),
                            style = MaterialTheme.typography.body1,
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = device.address,
                            style = MaterialTheme.typography.caption
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Divider()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PermissionRequestPreview() {
    VoltPolskaTheme {
        PermissionsRequest(onRequestPermission = {})
    }
}
