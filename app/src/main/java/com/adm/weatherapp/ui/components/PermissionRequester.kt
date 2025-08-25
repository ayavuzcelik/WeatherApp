package com.adm.weatherapp.ui.components

import androidx.compose.runtime.Composable

@Composable
fun PermissionRequester(
    permission: String,
    rationaleMessage: String,
    onPermissionGranted: @Composable () -> Unit = {},
    onPermissionDenied: @Composable () -> Unit = {},
    onPermissionError: @Composable () -> Unit = {}
) {
    // Implementation
}