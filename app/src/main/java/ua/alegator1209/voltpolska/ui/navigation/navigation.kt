package ua.alegator1209.voltpolska.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import ua.alegator1209.voltpolska.ui.screens.device.DeviceScreen
import ua.alegator1209.voltpolska.ui.screens.start.StartScreen

object Routes {
    const val Start = "start"
    const val Device = "device"
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Navigator(
    isBluetoothEnabled: Boolean,
    onEnableBluetooth: () -> Unit,
    isLocationEnabled: Boolean,
    onEnableLocation: () -> Unit,
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.Start) {
        composable(Routes.Start) {
            StartScreen(
                isBluetoothEnabled = isBluetoothEnabled,
                onDeviceConnected = { navController.navigate(Routes.Device) },
                onEnableBluetooth = onEnableBluetooth,
                isLocationEnabled = isLocationEnabled,
                onEnableLocation = onEnableLocation,
                viewModel = hiltViewModel()
            )
        }

        composable(Routes.Device) {
            DeviceScreen(hiltViewModel())
        }
    }
}
