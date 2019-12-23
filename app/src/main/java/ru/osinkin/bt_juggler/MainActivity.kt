package ru.osinkin.bt_juggler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.osinkin.bt_juggler.adapters.PairedDevicesRecycleViewAdapter
import ru.osinkin.bt_juggler.data.PairedDevicesController


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        pairedDevicesRecyclerView.setHasFixedSize(true)

        // use a linear layout manager
        val layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.layoutManager = layoutManager

        // specify an adapter (see also next example)
        val mAdapter = PairedDevicesRecycleViewAdapter(PairedDevicesController.getPairedDevices())
        pairedDevicesRecyclerView.adapter = mAdapter
    }


}
