package ru.osinkin.bt_juggler.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.osinkin.bt_juggler.R
import ru.osinkin.bt_juggler.adapters.LeDeviceListAdapter.ViewHolder

class LeDeviceListAdapter(
    private val mLeDevices: MutableList<BluetoothDevice>,
    private var onItemClickListener: OnItemCLickListener
) :
    RecyclerView.Adapter<ViewHolder>() {

    fun addDevice(device: BluetoothDevice) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device)
        }
    }

    fun getDevice(position: Int): BluetoothDevice {
        return mLeDevices[position]
    }

    fun clear() {
        mLeDevices.clear()
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.paired_device_text_view, parent, false)
        return ViewHolder(textView, onItemClickListener)
    }

    class ViewHolder(pairedDevicesItemView: View, private var mListener: OnItemCLickListener) :
        RecyclerView.ViewHolder(pairedDevicesItemView),
        View.OnClickListener {

        var deviceName: TextView = pairedDevicesItemView.findViewById(R.id.device_name)
        var deviceAddress: TextView = pairedDevicesItemView.findViewById(R.id.device_address)

        init {
            pairedDevicesItemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mListener.onItemClick(adapterPosition)
        }
    }

    interface OnItemCLickListener {
        fun onItemClick(position: Int)
    }

    override fun getItemCount(): Int = mLeDevices.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mLeDevices.isNotEmpty()) {
            val deviceName: String? = mLeDevices[position].name
            if (deviceName != null)
                holder.deviceName.text = deviceName
            else
                holder.deviceName.setText(R.string.unknown_device)
        }
        holder.deviceAddress.text = mLeDevices[position].address
    }

}