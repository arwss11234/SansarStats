package com.sansar.stats

import android.content.Context
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Store(ctx: Context) {
    private val sp = ctx.getSharedPreferences("stats", Context.MODE_PRIVATE)

    @Synchronized
    fun addSeconds(title: String, secs: Int) {
        val j = load()
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val days = j.getJSONObject("days")
        days.put(day, days.optInt(day, 0) + secs)
        val s = song(j, title)
        s.put("secs", s.optInt("secs", 0) + secs)
        sp.edit().putString("data", j.toString()).apply()
    }

    @Synchronized
    fun addPlay(title: String) {
        val j = load()
        val s = song(j, title)
        s.put("plays", s.optInt("plays", 0) + 1)
        sp.edit().putString("data", j.toString()).apply()
    }

    fun json(): String = load().toString()

    private fun song(j: JSONObject, t: String): JSONObject {
        val songs = j.getJSONObject("songs")
        if (!songs.has(t)) songs.put(t, JSONObject())
        return songs.getJSONObject(t)
    }

    private fun load(): JSONObject {
        val j = JSONObject(sp.getString("data", "{}")!!)
        if (!j.has("days")) j.put("days", JSONObject())
        if (!j.has("songs")) j.put("songs", JSONObject())
        return j
    }
}
