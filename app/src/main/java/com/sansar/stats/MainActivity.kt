package com.sansar.stats

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout

class MainActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var store: Store

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        store = Store(this)
        if (Build.VERSION.SDK_INT >= 33) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
        startForegroundService(Intent(this, KeepAliveService::class.java))

        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.mediaPlaybackRequiresUserGesture = false
        web.webChromeClient = WebChromeClient()
        web.addJavascriptInterface(object {
            @JavascriptInterface fun tick(title: String, secs: Int) = store.addSeconds(title, secs)
            @JavascriptInterface fun play(title: String) = store.addPlay(title)
        }, "Android")
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(v: WebView, url: String) {
                v.evaluateJavascript(assets.open("track.js").bufferedReader().readText(), null)
            }
        }
        web.loadUrl("https://sansarsalvoproject.com/")

        val btn = Button(this).apply {
            text = "İstatistik"
            setOnClickListener { startActivity(Intent(this@MainActivity, StatsActivity::class.java)) }
        }
        val root = FrameLayout(this)
        root.addView(web)
        root.addView(btn, FrameLayout.LayoutParams(-2, -2, Gravity.TOP or Gravity.END).apply { topMargin = 120; rightMargin = 16 })
        setContentView(root)
    }

    override fun onBackPressed() { if (web.canGoBack()) web.goBack() else super.onBackPressed() }
}
