package com.example.blescanner.permission

import android.app.Activity
import android.content.Context

object PermissionsUtilities {


    fun checkPermissions(activity: Activity, permissions: Array<out String>, requestCode: Int) {}

    fun checkPermissionsGranted(context: Context, permissions: Array<out String>): Boolean{
        return true
    }
    fun dispatchOnRequestPermissionsResult(
        requestCode: Int, grantResults: IntArray,
        onGrantedMap: Map<Int, () -> Unit>, onDeniedMap: Map<Int, () -> Unit>
    ) {
    }

    private fun checkPermissionGranted(context: Context, permission: String): Boolean {
        return true
    }

    private fun checkGrantResults(grantResults: IntArray): Boolean{
        return true
    }
}