package ru.osinkin.bt_juggler.data

import android.bluetooth.BluetoothDevice


class PairedDeviceItem(bluetoothDevice: BluetoothDevice) {
    var name: String = bluetoothDevice.name
}