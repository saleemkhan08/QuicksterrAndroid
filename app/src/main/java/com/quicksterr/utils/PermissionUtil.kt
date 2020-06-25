package com.quicksterr.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.quicksterr.ProductActivity
import com.quicksterr.R

class PermissionUtil {
    companion object {
        fun checkPermission(
            permissions: Array<out String>,
            grantResults: IntArray,
            permission: String,
            activity: AppCompatActivity,
            code: Int
        ): Boolean {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        showAlertDialog(
                            !activity.shouldShowRequestPermissionRationale(
                                permission
                            ), permission, activity, ({
                                requestPermission(activity, permission, code)
                            })
                        )
                        return false
                    }
                }
            }
            return true
        }

        fun requestPermission(activity: AppCompatActivity, permission: String, code: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(
                    arrayOf(permission),
                    code
                )
            }
        }

        private fun showAlertDialog(
            deniedPermanently: Boolean,
            type: String,
            activity: AppCompatActivity,
            onPositiveClick: () -> Unit
        ) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(R.string.permission_required)
            var message = getAlertMessage(type)
            val shouldCloseActivity = getShouldCloseActivity(type)
            val deniedMessage = getDeniedMessage(type)

            if (deniedPermanently) {
                message = R.string.goto_setting_message
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    closeActivity(activity, deniedMessage, shouldCloseActivity)
                }
            } else {
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    onPositiveClick()
                }

                builder.setNegativeButton(android.R.string.no) { _, _ ->
                    closeActivity(activity, deniedMessage, shouldCloseActivity)
                }
            }
            builder.setMessage(message)
            builder.show()
        }

        private fun getDeniedMessage(type: String): Int {
            return when (type) {
                Manifest.permission.CAMERA ->
                    R.string.camera_permission_denied_message
                Manifest.permission.READ_EXTERNAL_STORAGE ->
                    R.string.file_permission_denied_message
                else -> R.string.empty_string
            }
        }

        private fun getAlertMessage(type: String): Int {
            return when (type) {
                Manifest.permission.CAMERA ->
                    R.string.camera_alert_message
                Manifest.permission.READ_EXTERNAL_STORAGE ->
                    R.string.file_alert_message
                else -> R.string.empty_string
            }
        }

        private fun getShouldCloseActivity(type: String): Boolean {
            return when (type) {
                Manifest.permission.CAMERA -> true
                else -> false
            }
        }

        private fun closeActivity(
            activity: AppCompatActivity,
            deniedMessage: Int,
            shouldCloseActivity: Boolean
        ) {
            Toast.makeText(
                activity,
                deniedMessage,
                Toast.LENGTH_LONG
            ).show()
            if (shouldCloseActivity) {
                activity.finish()
            }
        }
    }
}