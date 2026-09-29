package com.nutricore.pro

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        webView = WebView(this).apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.allowFileAccess = true
            settings.allowContentAccess = true
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            settings.allowUniversalAccessFromFileURLs = true

            // Adiciona a bridge para buscar API pelo Kotlin
            addJavascriptInterface(ApiInterface(this@MainActivity, this), "AndroidAPI")

            loadUrl("file:///android_asset/index.html")
        }

        setContentView(webView)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}

class ApiInterface(
    private val activity: ComponentActivity,
    private val webView: WebView
) {
    @JavascriptInterface
    fun buscarAlimentos(termo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=${
                    java.net.URLEncoder.encode(termo, "UTF-8")
                }&search_simple=1&action=process&json=1&lc=pt&cc=br&page_size=8&fields=product_name,nutriments,brands"

                val resultado = URL(url).readText()

                withContext(Dispatchers.Main) {
                    // Escapa o JSON para passar para o JavaScript
                    val escapado = resultado
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "")
                        .replace("\r", "")
                    webView.evaluateJavascript(
                        "receberResultadoAPI('$escapado')", null
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    webView.evaluateJavascript(
                        "receberResultadoAPI(null)", null
                    )
                }
            }
        }
    }
}