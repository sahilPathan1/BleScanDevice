package com.example.blescanner.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.blescanner.adapter.BleDeviceAdapter.ViewHolder
import com.example.blescanner.databinding.BleDeviceItemBinding
import com.example.blescanner.model.BleDevice


class BleDeviceAdapter(private val devices: List<BleDevice>) : RecyclerView.Adapter<ViewHolder>() {
    inner class ViewHolder(var binding: BleDeviceItemBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            BleDeviceItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = devices[position]

        holder.binding.deviceName.text = device.name
    }

    override fun getItemCount(): Int {
        return devices.size
    }
}
