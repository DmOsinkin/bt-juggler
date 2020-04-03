package ru.osinkin.bt_juggler.view

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager

interface BluetoothScanView {
    fun addDevice(bluetoothDevice: BluetoothDevice)
    fun initBluetoothEnablingIntent()
    fun getBluetoothManager(): BluetoothManager
    fun showBluetoothIsNotSupported()
}