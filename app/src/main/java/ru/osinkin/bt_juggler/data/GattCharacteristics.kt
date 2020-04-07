package ru.osinkin.bt_juggler.data

import java.util.*

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
object GattCharacteristics {
    private val attributes = HashMap<String?, String?>()

    var CUSTOM_CHARACTERISTIC = "00000002-0000-1000-8000-00805f9b34fb"
    var CUSTOM_SERVICE = "00001110-0000-1000-8000-00805f9b34fb"

    fun lookup(uuid: String?, defaultName: String?): String? {
        return attributes[uuid] ?: defaultName
    }

    init {
        attributes[CUSTOM_CHARACTERISTIC] = "Test Characteristic"
        attributes[CUSTOM_SERVICE] = "Test Service"
    }
}