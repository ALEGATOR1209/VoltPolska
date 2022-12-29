package ua.alegator1209.voltpolska.di

import android.bluetooth.BluetoothManager
import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ua.alegator1209.voltpolska.data.ScanRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MainModule {
  @Provides
  @Singleton
  fun provideScanRepository(
    bluetoothManager: BluetoothManager,
    locationManager: LocationManager,
  ) = ScanRepository(bluetoothManager, locationManager)

  @Provides
  @Singleton
  fun provideBluetoothManager(
    @ApplicationContext context: Context
  ) = context.getSystemService(BluetoothManager::class.java)

  @Provides
  @Singleton
  fun provideLocationManager(
    @ApplicationContext context: Context
  ) = context.getSystemService(LocationManager::class.java)
}