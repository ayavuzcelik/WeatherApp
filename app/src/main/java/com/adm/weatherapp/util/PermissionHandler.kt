package com.adm.weatherapp.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PermissionHandler(private val context: Context) {
    private val _state = MutableStateFlow<Map<String, PermissionState>>(emptyMap())
    val state = _state.asStateFlow()

    private fun checkPermission(permission: String): PermissionState {
        return when {
            ContextCompat.checkSelfPermission(context, permission) ==
                    PackageManager.PERMISSION_GRANTED -> PermissionState.Granted

            ActivityCompat.shouldShowRequestPermissionRationale(
                context as Activity,
                permission
            ) -> PermissionState.ShowRationale

            _state.value[permission] == PermissionState.Denied ->
                PermissionState.NeverAskAgain

            else -> PermissionState.Denied
        }
    }
}