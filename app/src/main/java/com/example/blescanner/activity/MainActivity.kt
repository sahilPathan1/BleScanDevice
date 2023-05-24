package com.example.blescanner.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.blescanner.*
import com.example.blescanner.permission.PermissionsUtilities.dispatchOnRequestPermissionsResult
import com.example.blescanner.adapter.BleDeviceAdapter
import com.example.blescanner.ble.BleScanCallback
import com.example.blescanner.ble.BleScanManager
import com.example.blescanner.databinding.ActivityMainBinding
import com.example.blescanner.model.BleDevice
import com.example.blescanner.permission.BleScanRequiredPermissions
import com.example.blescanner.permission.PermissionsUtilities

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var btManager: BluetoothManager
    private lateinit var bleScanManager: BleScanManager
    private lateinit var bleDeviceAdapter: BleDeviceAdapter
    private lateinit var foundDevices: MutableList<BleDevice>

    @SuppressLint("NotifyDataSetChanged", "MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        askPermission()
        binding.apply {
            // RecyclerView handling

            foundDevices = BleDevice.createBleDevicesList()
            bleDeviceAdapter = BleDeviceAdapter(foundDevices)
            rvFoundDevices.adapter = bleDeviceAdapter
            rvFoundDevices.layoutManager = LinearLayoutManager(this@MainActivity)
        }
        // BleManager creation
        btManager = getSystemService(BluetoothManager::class.java)
        bleScanManager = BleScanManager(btManager, 500, scanCallback = BleScanCallback({
            val name = it?.device?.address
            val device = BleDevice(name ?: "noname")
            if (!foundDevices.contains(device)) {
                foundDevices.add(device)
                bleDeviceAdapter.notifyItemInserted(foundDevices.size - 1)
            }
        }))

        // Adding the actions the manager must do before and after scanning
        bleScanManager.beforeScanActions.add {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnStartScan.isEnabled = false
        }
        bleScanManager.beforeScanActions.add {
            foundDevices.clear()
            bleDeviceAdapter.notifyDataSetChanged()
        }
        bleScanManager.afterScanActions.add {
            binding.progressBar.visibility = View.GONE
            binding.btnStartScan.isEnabled = true
        }

        // Adding the onclick listener to the start scan button
        binding.btnStartScan.setOnClickListener {
            // Checks if the required permissions are granted and starts the scan if so, otherwise it requests them
            when (PermissionsUtilities.checkPermissionsGranted(
                this,
                BleScanRequiredPermissions.permissions
            )) {
                true ->
                    bleScanManager.scanBleDevices()
                false -> PermissionsUtilities.checkPermissions(
                    this, BleScanRequiredPermissions.permissions, BLE_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun askPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
               android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        //Prompt the user once explanation has been shown
                        requestLocationPermission()
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                requestLocationPermission()
            }
        } else {
            Toast.makeText(this, "check background location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
               android.Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            Companion.MY_PERMISSIONS_REQUEST_LOCATION
        )
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        dispatchOnRequestPermissionsResult(
            requestCode,
            grantResults,
            onGrantedMap = mapOf(BLE_PERMISSION_REQUEST_CODE to { bleScanManager.scanBleDevices() }),
            onDeniedMap = mapOf(BLE_PERMISSION_REQUEST_CODE to {
                Toast.makeText(
                    this,
                    "Some permissions were not granted, please grant them and try again",
                    Toast.LENGTH_LONG
                ).show()
            })
        )
    }

    companion object {
        private const val BLE_PERMISSION_REQUEST_CODE = 1
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        private const val MY_PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 66
    }
}