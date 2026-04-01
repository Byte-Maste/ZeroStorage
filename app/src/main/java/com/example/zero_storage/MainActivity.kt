package com.example.zero_storage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.*
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = if (data?.clipData != null) {
                Array(data.clipData!!.itemCount) { i -> data.clipData!!.getItemAt(i).uri }
            } else {
                arrayOfNullable(data?.data)
            }
            fileUploadCallback?.onReceiveValue(results)
        } else {
            fileUploadCallback?.onReceiveValue(null)
        }
        fileUploadCallback = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.mediaPlaybackRequiresUserGesture = false
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.setSupportZoom(false)
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    val url = request?.url?.toString() ?: return false
                    
                    // Allow WebView to handle standard web and local file URLs, 
                    // EXCEPT WhatsApp links which should open the native app
                    if ((url.startsWith("http://") || url.startsWith("https://") || url.startsWith("file://")) 
                        && !url.contains("wa.me") && !url.contains("api.whatsapp.com")) {
                        return false
                    }

                    // Handle WhatsApp links explicitly
                    if (url.contains("wa.me") || url.contains("api.whatsapp.com")) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                            return true
                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                            return true // Still return true so WebView doesn't try to load it
                        }
                    }

                    // For custom schemes like intent://, whatsapp://, tel://, mailto://
                    return try {
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        // If the app is not installed, fallback to the browser URL if available
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                            if (fallbackUrl != null) {
                                view?.loadUrl(fallbackUrl)
                            } else {
                                Toast.makeText(this@MainActivity, "App not installed", Toast.LENGTH_SHORT).show()
                            }
                        }
                        true // We handled the intent, don't let WebView load it
                    } catch (e: Exception) {
                        Log.e("WebViewIntent", "Failed to parse or start intent: $url", e)
                        false
                    }
                }

                override fun onReceivedError(
                    view: WebView?, request: WebResourceRequest?, error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    // If loading from assets fails, try loading from network
                    if (request?.isForMainFrame == true) {
                        Toast.makeText(this@MainActivity, "Loading error, retrying...", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    consoleMessage?.let {
                        Log.d("WebViewConsole", "${it.message()} -- From line ${it.lineNumber()} of ${it.sourceId()}")
                    }
                    return super.onConsoleMessage(consoleMessage)
                }

                override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
                ): Boolean {
                    fileUploadCallback?.onReceiveValue(null)
                    fileUploadCallback = filePathCallback

                    val intent = fileChooserParams?.createIntent()
                    if (intent != null) {
                        try {
                            fileChooserLauncher.launch(intent)
                        } catch (e: Exception) {
                            fileUploadCallback = null
                            Toast.makeText(this@MainActivity, "Cannot open file chooser", Toast.LENGTH_SHORT).show()
                            return false
                        }
                    } else {
                        fileUploadCallback?.onReceiveValue(null)
                        fileUploadCallback = null
                        return false
                    }
                    return true
                }
            }
        }

        setContentView(webView)

        // Handle back button using modern AndroidX dispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Prevent backing out into the splash screen
                if (webView.url?.contains("splash.html") == true) return
                
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        // 1. Load the fully static splash screen first
        webView.loadUrl("file:///android_asset/webapp/splash.html")

        // 2. Wait 3.5 seconds so the user sees the logo, then load the React App
        Handler(Looper.getMainLooper()).postDelayed({
            webView.loadUrl("file:///android_asset/dist/index.html")
            
            // Clear history so the user can't press 'back' and return to the splash screen
            webView.clearHistory()
        }, 3500)
    }

    private fun arrayOfNullable(uri: Uri?): Array<Uri>? {
        return if (uri != null) arrayOf(uri) else null
    }
}