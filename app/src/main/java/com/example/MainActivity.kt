package com.example

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.ui.CartCraftScreen
import com.example.ui.CartCraftViewModel
import com.example.ui.theme.CartCraftTheme
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

  private val viewModel: CartCraftViewModel by viewModels {
    CartCraftViewModel.Factory(application)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      CartCraftTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          CartCraftScreen(
            viewModel = viewModel,
            onShareApk = ::shareApk
          )
        }
      }
    }
  }

  private fun shareApk() {
    lifecycleScope.launch {
      try {
        val apkUri = withContext(Dispatchers.IO) {
          val shareDirectory = File(cacheDir, "shared-apks")
          if (!shareDirectory.exists() && !shareDirectory.mkdirs()) {
            throw IOException("Unable to create APK share directory")
          }

          val apkFile = File(shareDirectory, "CartCraft.apk")
          File(applicationInfo.sourceDir).copyTo(apkFile, overwrite = true)
          FileProvider.getUriForFile(
            this@MainActivity,
            "$packageName.fileprovider",
            apkFile
          )
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
          type = "application/vnd.android.package-archive"
          putExtra(Intent.EXTRA_STREAM, apkUri)
          clipData = android.content.ClipData.newUri(contentResolver, "CartCraft.apk", apkUri)
          addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share CartCraft APK"))
      } catch (exception: IOException) {
        Log.e(TAG, "Unable to prepare CartCraft APK for sharing", exception)
        Toast.makeText(this@MainActivity, "Couldn't prepare the APK to share.", Toast.LENGTH_LONG)
          .show()
      } catch (exception: ActivityNotFoundException) {
        Log.e(TAG, "No app is available to share the CartCraft APK", exception)
        Toast.makeText(this@MainActivity, "No app is available to share the APK.", Toast.LENGTH_LONG)
          .show()
      }
    }
  }

  private companion object {
    const val TAG = "MainActivity"
  }
}
