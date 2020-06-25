package com.quicksterr

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.zxing.Result
import com.quicksterr.utils.PermissionUtil
import com.quicksterr.room.InsertProductsAsyncTask
import kotlinx.android.synthetic.main.product_activity.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ProductActivity : AppCompatActivity(), ZXingScannerView.ResultHandler,
    AppBarLayout.OnOffsetChangedListener {
    private val menuItems = ArrayList<MenuItem>();
    private var mScannerView: ZXingScannerView? = null
    private var scrollRange = -1

    companion object {
        const val TAG = "ScrollingActivity";
        const val ACTIVITY_CHOOSE_FILE = 201
        const val CAMERA_PERMISSION_CODE = 101
        const val FILE_PERMISSION_CODE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.product_activity)
        setSupportActionBar(toolbar)
        toolbar_layout.title = ""
    }

    override fun onStart() {
        super.onStart()
        if (hasPermission(Manifest.permission.CAMERA)) {
            setUpScanner()
        } else {
            requestCameraPermission()
        }
    }

    private fun setUpScanner() {
        app_bar.addOnOffsetChangedListener(this)
        mScannerView = ZXingScannerView(this);   // Programmatically initialize the scanner view
        scannerContainer.addView(mScannerView)
        mScannerView?.setResultHandler(this) // Register ourselves as a handler for scan results.
    }

    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_CODE ->
                if (PermissionUtil.checkPermission(
                        permissions,
                        grantResults,
                        Manifest.permission.CAMERA,
                        this,
                        CAMERA_PERMISSION_CODE
                    )
                ) {
                    setUpScanner()
                }

            FILE_PERMISSION_CODE ->
                if (PermissionUtil.checkPermission(
                        permissions,
                        grantResults,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        this,
                        FILE_PERMISSION_CODE
                    )
                ) {
                    selectCSVFile()
                }
        }
    }

    override fun onResume() {
        super.onResume()
        mScannerView?.startCamera() // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera() // Stop camera on pause
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        this.menuItems.add(menu.findItem(R.id.action_upload))
        this.menuItems.add(menu.findItem(R.id.action_profile))
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> true
            R.id.action_upload -> {
                if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    selectCSVFile()
                } else {
                    requestFilePermission()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun requestFilePermission() {
        PermissionUtil.requestPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            FILE_PERMISSION_CODE
        )
    }

    private fun selectCSVFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/csv"
        startActivityForResult(intent, ACTIVITY_CHOOSE_FILE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            ACTIVITY_CHOOSE_FILE -> {
                if (resultCode == RESULT_OK) {
                    InsertProductsAsyncTask(intent?.data, application).execute();
                }
            }
        }
    }

    override fun handleResult(rawResult: Result?) {
        // Do something with the result here
        Log.v(TAG, rawResult?.text); // Prints scan results
        Log.v(
            TAG,
            rawResult?.barcodeFormat.toString()
        ); // Prints the scan format (qrcode, pdf417 etc.)

        // If you would like to resume scanning, call this method below:
        mScannerView?.resumeCameraPreview(this);
    }

    private fun changeMenuIconColor(color: Int) {
        for (menu in this.menuItems) {
            val icon = menu.icon
            if (icon != null) {
                DrawableCompat.setTint(icon, ContextCompat.getColor(this, color))
            }
        }
    }

    private fun changeAddProductCardOpacity(opacity: Float) {
        if (opacity < 0.1f)
            addProductCard.alpha = opacity / 0.1f
        else
            addProductCard.alpha = 1f
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val opacity = (1 + verticalOffset.toFloat() / scrollRange)
        changeAddProductCardOpacity(opacity)
        if (scrollRange == -1) {
            scrollRange = appBarLayout?.totalScrollRange!!;
        }
        when (scrollRange + verticalOffset) {
            0 -> onToolbarCollapsed()
            scrollRange -> onToolbarExpanded()
            else -> onToolbarCollapsing()
        }
    }

    private fun onToolbarCollapsed() {
        toolbar_layout.title = getString(R.string.app_name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            window?.decorView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        changeMenuIconColor(android.R.color.black)
    }

    private fun onToolbarExpanded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            window?.decorView?.systemUiVisibility = 0
        toolbar_layout.title = getString(R.string.empty_string)
        changeMenuIconColor(R.color.white)
    }

    private fun onToolbarCollapsing() {
        toolbar_layout.title = getString(R.string.empty_string)
    }
}

