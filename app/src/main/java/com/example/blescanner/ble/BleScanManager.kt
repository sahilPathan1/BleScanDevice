package com.example.blescanner.ble

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.os.Handler
import android.os.Looper

class BleScanManager(
    btManager: BluetoothManager,
    private val scanPeriod: Long = DEFAULT_SCAN_PERIOD,
    private val scanCallback: BleScanCallback = BleScanCallback()
) {

    companion object {
        const val DEFAULT_SCAN_PERIOD: Long = 1000

        private fun executeListOfFunctions(toExecute: List<() -> Unit>) {
            toExecute.forEach {
                it()
            }
        }
    }

    private val btAdapter = btManager.adapter
    private val bleScanner = btAdapter.bluetoothLeScanner

    var beforeScanActions: MutableList<() -> Unit> = mutableListOf()
    var afterScanActions: MutableList<() -> Unit> = mutableListOf()

    /** True when the manager is performing the scan */
    private var scanning = false

    private val handler = Handler(Looper.getMainLooper())

    /**
     * Scans for Bluetooth LE devices and stops the scan after [scanPeriod] seconds.
     * Does not checks the required permissions are granted, check must be done beforehand.
     */
    @SuppressLint("MissingPermission")
    fun scanBleDevices() {
        fun stopScan() {
            scanning = false
            bleScanner.stopScan(scanCallback)

            // execute all the functions to execute after scanning
            executeAfterScanActions()
        }

        // scans for bluetooth LE devices
        if (scanning) {
            stopScan()
        } else {
            // stops scanning after scanPeriod millis
            handler.postDelayed({ stopScan() }, scanPeriod)
            // execute all the functions to execute before scanning
            executeBeforeScanActions()

            // starts scanning
            scanning = true
            bleScanner.startScan(scanCallback)
        }
    }

    private fun executeBeforeScanActions() {
        executeListOfFunctions(beforeScanActions)
    }

    private fun executeAfterScanActions() {
        executeListOfFunctions(afterScanActions)
    }
}