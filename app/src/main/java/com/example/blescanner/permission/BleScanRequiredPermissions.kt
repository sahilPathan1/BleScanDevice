package com.example.blescanner.permission

import android.os.Build
import androidx.annotation.RequiresApi

object BleScanRequiredPermissions {

    @RequiresApi(Build.VERSION_CODES.S)
    val permissions = arrayOf(
        android.Manifest.permission.BLUETOOTH,
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.BLUETOOTH_ADMIN,
        android.Manifest.permission.BLUETOOTH_PRIVILEGED
    )
}