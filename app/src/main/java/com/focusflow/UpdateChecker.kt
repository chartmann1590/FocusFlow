package com.focusflow

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

data class ReleaseInfo(
    val tagName: String,
    val version: String,
    val downloadUrl: String,
    val publishedAt: String
)

class UpdateChecker(private val context: Context) {
    
    companion object {
        private const val GITHUB_API_URL = "https://api.github.com/repos/chartmann1590/FocusFlow/releases/latest"
        private const val GITHUB_RELEASES_URL = "https://github.com/chartmann1590/FocusFlow/releases"
        private const val WEBSITE_URL = "https://chartmann1590.github.io/FocusFlow/"
        private const val PREFS_NAME = "update_prefs"
        private const val KEY_LAST_SEEN_VERSION = "last_seen_version"
    }
    
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    interface UpdateCallback {
        fun onUpdateAvailable(latestRelease: ReleaseInfo, currentVersion: String)
        fun onNoUpdateAvailable(currentVersion: String)
        fun onError(error: String)
    }
    
    fun checkForUpdate(currentVersion: String, callback: UpdateCallback) {
        val queue = Volley.newRequestQueue(context)
        
        val request = JsonObjectRequest(
            Request.Method.GET,
            GITHUB_API_URL,
            null,
            { response ->
                try {
                    val releaseInfo = parseReleaseResponse(response)
                    val latestVersion = releaseInfo.version
                    
                    if (isNewerVersion(latestVersion, currentVersion)) {
                        saveLastSeenVersion(latestVersion)
                        callback.onUpdateAvailable(releaseInfo, currentVersion)
                    } else {
                        callback.onNoUpdateAvailable(currentVersion)
                    }
                } catch (e: Exception) {
                    callback.onError("Failed to parse release info")
                }
            },
            { error ->
                callback.onError("Network error: ${error.message}")
            }
        )
        
        queue.add(request)
    }
    
    private fun parseReleaseResponse(response: JSONObject): ReleaseInfo {
        val tagName = response.getString("tag_name")
        val publishedAt = response.getString("published_at")
        var downloadUrl = GITHUB_RELEASES_URL
        
        val assets = response.optJSONArray("assets")
        if (assets != null) {
            for (i in 0 until assets.length()) {
                val asset = assets.getJSONObject(i)
                val name = asset.getString("name")
                if (name.endsWith(".apk")) {
                    downloadUrl = asset.getString("browser_download_url")
                    break
                }
            }
        }
        
        return ReleaseInfo(
            tagName = tagName,
            version = tagName,
            downloadUrl = downloadUrl,
            publishedAt = publishedAt
        )
    }
    
    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestClean = latest.removePrefix("v").removePrefix("build-")
        val currentClean = current.removePrefix("v").removePrefix("build-")
        
        if (latestClean.startsWith("build-") && currentClean.startsWith("build-")) {
            val latestCommit = latestClean.removePrefix("build-")
            val currentCommit = currentClean.removePrefix("build-")
            return latestCommit != currentCommit
        }
        
        return latestClean != currentClean
    }
    
    private fun saveLastSeenVersion(version: String) {
        prefs.edit().putString(KEY_LAST_SEEN_VERSION, version).apply()
    }
    
    fun getLastSeenVersion(): String? {
        return prefs.getString(KEY_LAST_SEEN_VERSION, null)
    }
    
    fun hasNewUpdate(): Boolean {
        val lastSeen = getLastSeenVersion()
        return lastSeen != null && !lastSeen.isNullOrEmpty()
    }
    
    fun openWebsite() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(WEBSITE_URL))
        context.startActivity(intent)
    }
    
    fun openGitHub() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/chartmann1590/FocusFlow"))
        context.startActivity(intent)
    }
    
    fun openReleases() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GITHUB_RELEASES_URL))
        context.startActivity(intent)
    }
    
    fun downloadLatestRelease(releaseInfo: ReleaseInfo) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(releaseInfo.downloadUrl))
        context.startActivity(intent)
    }
}
