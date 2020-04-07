package ru.osinkin.bt_juggler

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_device_control.*
import ru.osinkin.bt_juggler.service.BluetoothLeService


class DeviceControlActivity : Activity() {
    private var mDeviceAddress: String? = null
    private var mBluetoothLeService: BluetoothLeService? = null

    // Code to manage Service lifecycle.
    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            componentName: ComponentName,
            binder: IBinder
        ) {
            mBluetoothLeService = (binder as BluetoothLeService.LocalBinder).service
            if (!mBluetoothLeService?.initialize()!!) {
                Log.e(TAG, "Unable to initialize Bluetooth")
                finish()
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService?.connect(mDeviceAddress)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBluetoothLeService = null
        }
    }

    // Handles various events fired by the Service.
    private val mGattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            when (intent.action) {
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    //finish()
                }
                BluetoothLeService.ACTION_DATA_AVAILABLE -> {
                    displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA))
                }
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_control)

        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS)

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter())
        if (mBluetoothLeService != null) {
            val result: Boolean = mBluetoothLeService!!.connect(mDeviceAddress)
            Log.d(TAG, "Connect request result=$result")
        }
//        else {
//            finish()
//        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mGattUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mServiceConnection)
        mBluetoothLeService!!.disconnect()
        mBluetoothLeService = null
    }

    private fun displayData(data: String?) {
        if (data != null) {
            characteristicValueEditText.setText(data)
        }
    }

    fun onClickWrite(v: View?) {
        if (mBluetoothLeService != null) {
            //TODO(): change to value from characteristicValueEditText
            mBluetoothLeService!!.writeCustomCharacteristic(0xAA)
        }
    }

    fun onClickRead(v: View?) {
        if (mBluetoothLeService != null) {
            mBluetoothLeService!!.readCustomCharacteristic()
        }
    }

    companion object {
        private val TAG = DeviceControlActivity::class.java.simpleName
        const val EXTRAS_DEVICE_NAME = "DEVICE_NAME"
        const val EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS"
        private fun makeGattUpdateIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
            return intentFilter
        }
    }
}