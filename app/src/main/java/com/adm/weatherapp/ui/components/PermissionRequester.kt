package com.adm.weatherapp.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun PermissionRequester(
    permission: String,
    rationaleMessage: String,
    onPermissionGranted: @Composable () -> Unit = {},
    onPermissionDenied: @Composable (openSettings: () -> Unit) -> Unit = {},
    onPermissionError: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasPermission by remember { mutableStateOf<Boolean?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasPermission = granted
        }
    )

    fun requestPermission() {
        launcher.launch(permission)
    }

    // Open app settings to grant permission manually
    fun openAppSettings() {
        val activity = context.findActivity()
        val intent = Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", activity.packageName, null)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        activity.startActivity(intent)
    }

    // When the app comes back to foreground, check the permission status
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val check = ContextCompat.checkSelfPermission(context, permission)
                hasPermission = check == PackageManager.PERMISSION_GRANTED
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Initial permission check
    LaunchedEffect(Unit) {
        val check = ContextCompat.checkSelfPermission(context, permission)
        if (check == PackageManager.PERMISSION_GRANTED) {
            hasPermission = true
        } else {
            requestPermission()
        }
    }

    when (hasPermission) {
        true -> onPermissionGranted()
        false -> onPermissionDenied { openAppSettings() }
        null -> {}
    }
}

// Context -> Activity extension (modern)
fun Context.findActivity(): Activity {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    throw IllegalStateException("Activity not found in context chain")
}
