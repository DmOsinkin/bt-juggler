package ru.osinkin.bt_juggler.adapters

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.osinkin.bt_juggler.R

class PairedDevicesRecycleViewAdapter(private val myDataset: MutableList<BluetoothDevice>) :
    RecyclerView.Adapter<PairedDevicesRecycleViewAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(pairedDevicesItemView: View) :
        RecyclerView.ViewHolder(pairedDevicesItemView) {
        var textView: TextView? = null

        init {
            textView = pairedDevicesItemView.findViewById(R.id.device_name)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // create a new view
        val textView = LayoutInflater.from(parent.context)
            .inflate(R.layout.paired_device_text_view, parent, false)
        return MyViewHolder(textView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textView?.text = if (myDataset.isNotEmpty()) myDataset[position].name else "none"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = myDataset.size

}