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
        pairedDevicesRecyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this)
        pairedDevicesRecyclerView.layoutManager = layoutManager

        val mAdapter = PairedDevicesRecycleViewAdapter(PairedDevicesController.getPairedDevicesController(this).getPairedDevices())
        pairedDevicesRecyclerView.adapter = mAdapter
    }


}
