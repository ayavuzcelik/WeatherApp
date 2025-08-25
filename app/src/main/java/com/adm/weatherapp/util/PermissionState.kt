package com.adm.weatherapp.util

sealed interface PermissionState {
    object Granted : PermissionState
    object Denied : PermissionState
    object ShowRationale : PermissionState
    object NeverAskAgain : PermissionState
}