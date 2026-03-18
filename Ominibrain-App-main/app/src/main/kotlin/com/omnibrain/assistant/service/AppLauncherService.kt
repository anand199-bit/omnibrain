package com.omnibrain.assistant.service

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.omnibrain.assistant.util.Constants

class AppLauncherService(private val context: Context) {
    
    fun parseAndLaunchApp(command: String): String {
        val lowerCommand = command.lowercase()
        
        // Check if it's an app launch command
        val isLaunchCommand = lowerCommand.contains(Constants.COMMAND_OPEN) ||
                lowerCommand.contains(Constants.COMMAND_LAUNCH) ||
                lowerCommand.contains(Constants.COMMAND_START)
        
        if (!isLaunchCommand) {
            return "" // Not a launch command
        }
        
        // Extract app name
        val appName = extractAppName(lowerCommand)
        if (appName.isEmpty()) {
            return "I couldn't identify which app to open."
        }
        
        // Try to launch the app
        return launchApp(appName)
    }
    
    private fun extractAppName(command: String): String {
        val keywords = listOf(Constants.COMMAND_OPEN, Constants.COMMAND_LAUNCH, Constants.COMMAND_START)
        var appName = command
        
        keywords.forEach { keyword ->
            if (appName.contains(keyword)) {
                appName = appName.substringAfter(keyword).trim()
            }
        }
        
        return appName
    }
    
    private fun launchApp(appName: String): String {
        val packageManager = context.packageManager
        
        // Get installed apps based on Android version
        val installedApps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getInstalledApplications(
                PackageManager.ApplicationInfoFlags.of(PackageManager.GET_META_DATA.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        }
        
        // Filter only launchable apps
        val launchableApps = installedApps.filter { appInfo ->
            packageManager.getLaunchIntentForPackage(appInfo.packageName) != null
        }
        
        // Find matching app with fuzzy matching
        val matchedApp = findBestMatch(launchableApps, appName, packageManager)
        
        return if (matchedApp != null) {
            try {
                val launchIntent = packageManager.getLaunchIntentForPackage(matchedApp.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    val appLabel = packageManager.getApplicationLabel(matchedApp)
                    "Opening $appLabel"
                } else {
                    "Cannot launch this app"
                }
            } catch (e: Exception) {
                "Error launching app: ${e.message}"
            }
        } else {
            // Try common app mappings
            val commonAppResult = tryCommonApps(appName)
            if (commonAppResult.isNotEmpty()) {
                commonAppResult
            } else {
                "App '$appName' not found. Try saying the full app name."
            }
        }
    }
    
    private fun findBestMatch(
        apps: List<ApplicationInfo>,
        searchName: String,
        packageManager: PackageManager
    ): ApplicationInfo? {
        val searchLower = searchName.lowercase()
        
        // First try exact match
        var bestMatch = apps.find { appInfo ->
            val label = packageManager.getApplicationLabel(appInfo).toString().lowercase()
            label == searchLower
        }
        
        if (bestMatch != null) return bestMatch
        
        // Try starts with
        bestMatch = apps.find { appInfo ->
            val label = packageManager.getApplicationLabel(appInfo).toString().lowercase()
            label.startsWith(searchLower) || searchLower.startsWith(label)
        }
        
        if (bestMatch != null) return bestMatch
        
        // Try contains
        bestMatch = apps.find { appInfo ->
            val label = packageManager.getApplicationLabel(appInfo).toString().lowercase()
            label.contains(searchLower) || searchLower.contains(label)
        }
        
        if (bestMatch != null) return bestMatch
        
        // Try package name match
        return apps.find { appInfo ->
            appInfo.packageName.lowercase().contains(searchLower)
        }
    }
    
    private fun tryCommonApps(appName: String): String {
        val commonApps = mapOf(
            "chrome" to "com.android.chrome",
            "browser" to "com.android.chrome",
            "whatsapp" to "com.whatsapp",
            "youtube" to "com.google.android.youtube",
            "gmail" to "com.google.android.gm",
            "maps" to "com.google.android.apps.maps",
            "camera" to "com.android.camera2",
            "gallery" to "com.google.android.apps.photos",
            "photos" to "com.google.android.apps.photos",
            "settings" to "com.android.settings",
            "phone" to "com.android.dialer",
            "dialer" to "com.android.dialer",
            "messages" to "com.google.android.apps.messaging",
            "sms" to "com.google.android.apps.messaging",
            "contacts" to "com.android.contacts",
            "calculator" to "com.google.android.calculator",
            "calendar" to "com.google.android.calendar",
            "clock" to "com.google.android.deskclock",
            "play store" to "com.android.vending",
            "store" to "com.android.vending"
        )
        
        val packageName = commonApps[appName.lowercase()]
        if (packageName != null) {
            return try {
                val intent = context.packageManager.getLaunchIntentForPackage(packageName)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    "Opening $appName"
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        }
        return ""
    }
}
