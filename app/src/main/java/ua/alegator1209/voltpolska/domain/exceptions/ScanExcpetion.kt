package ua.alegator1209.voltpolska.domain.exceptions

sealed class ScanException(message: String) : Exception(message) {
  class BluetoothException : ScanException("Bluetooth disabled")
  class LocationException : ScanException("Geolocation disabled")
  class ScanFailureException(val code: Int) : ScanException("Scan failure, code: $code")
}
