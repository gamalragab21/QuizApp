package com.developer.quizapp.utils

import android.Manifest
import android.content.Context
import android.os.Build
import pub.devrel.easypermissions.EasyPermissions

object PermissionsUtility {
    fun hasLocationPermissions(context: Context) =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
            else -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            }
        }

    fun hasCallPhonePermissions(context: Context) =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.CALL_PHONE,
                )
            }
            else -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.CALL_PHONE
                )
            }
        }


    fun hasReadContactsPhonePermissions(context: Context) =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.READ_CONTACTS,
                )
            }
            else -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.READ_CONTACTS
                )
            }
        }

    fun hasReadExternalStoragePermissions(context: Context) =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }
            else -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    fun hasWriteExternalStoragePermissions(context: Context) =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            }
            else -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }

    fun hasCameraAndVibrationsPermissions(context: Context) =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.CAMERA
                )
            }
            else -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.CAMERA
                )
            }
        }
    fun hasInternetPermission(context: Context) =
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.INTERNET
                )
            }
            else -> {
                EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.INTERNET
                )
            }
        }
}