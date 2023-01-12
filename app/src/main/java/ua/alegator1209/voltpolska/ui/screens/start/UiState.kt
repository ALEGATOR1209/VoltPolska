package ua.alegator1209.voltpolska.ui.screens.start

data class UiState(
  val deviceName: String = "",
  val isSearchInProgress: Boolean = false,
  val timerValue: Int = 0,
  val devices: List<Device> = listOf(),
  val error: Error = Error.None,
  val isLoading: Boolean = false,
) {
  data class Device(val name: String, val address: String)
  enum class Error {
    None,
    BluetoothPermissionNeeded,
    LocationPermissionNeeded,
    BluetoothAndLocationPermissionNeeded,
    BluetoothDisabled,
    LocationDisabled,
    DeviceConnectionError,
  }
}
