package com.sansar.stats

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView

class StatsActivity : Activity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val store = Store(this)
        val w = WebView(this)
        w.settings.javaScriptEnabled = true
        w.setBackgroundColor(0xFF0F1110.toInt())
        w.addJavascriptInterface(object {
            @JavascriptInterface fun getStats(): String = store.json()
            @JavascriptInterface fun close() { runOnUiThread { finish() } }
        }, "Native")
        w.loadUrl("file:///android_asset/stats.html")
        setContentView(w)
    }
}
