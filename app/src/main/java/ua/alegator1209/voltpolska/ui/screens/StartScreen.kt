package ua.alegator1209.voltpolska.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import ua.alegator1209.voltpolska.R
import ua.alegator1209.voltpolska.ui.permissions.Permissions
import ua.alegator1209.voltpolska.ui.theme.VoltPolskaTheme

@ExperimentalPermissionsApi
@Composable
fun StartScreen() {
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
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            if (!permissionsState.allPermissionsGranted) {
                PermissionsRequest(
                    onRequestPermission = { permissionsState.launchMultiplePermissionRequest() },
                )
            }
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

@Composable
fun Scanner(
    isInProgress: Boolean,
    progress: Float,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = stringResource(
            id = if (isInProgress) R.string.scanner_scan_in_progress else R.string.scanner_scan_finished
        ))
    }
}
