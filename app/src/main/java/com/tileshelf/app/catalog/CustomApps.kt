package com.tileshelf.app.catalog

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import java.net.URI
import java.util.Locale

sealed interface CustomTarget {
    val label: String

    data class App(val packageName: String, override val label: String) : CustomTarget

    data class Link(val url: String, override val label: String) : CustomTarget
}

val CustomSlot.linkKey: String get() = "${packageKey}_link"

object CustomApps {
    fun read(context: Context, slot: CustomSlot): CustomTarget? {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val label = prefs.getString(slot.labelKey, null)?.trim().orEmpty()
        val link = prefs.getString(slot.linkKey, null)?.trim().orEmpty()
        if (link.isNotEmpty()) return CustomTarget.Link(link, label.ifEmpty { linkLabel(link) })
        val packageName = prefs.getString(slot.packageKey, null)?.trim().orEmpty()
        if (packageName.isEmpty()) return null
        return CustomTarget.App(packageName, label.ifEmpty { packageName })
    }

    fun write(context: Context, slot: CustomSlot, target: CustomTarget) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(slot.labelKey, target.label)
            when (target) {
                is CustomTarget.App -> putString(slot.packageKey, target.packageName).remove(slot.linkKey)
                is CustomTarget.Link -> putString(slot.linkKey, target.url).remove(slot.packageKey)
            }
        }
    }
}

fun parseLink(raw: String): String? {
    val text = raw.filterNot { it.isWhitespace() }
    if (text.isEmpty()) return null
    val hostWithPort = Regex("^([^:/]*\\.[^:/]*|localhost):\\d+(/|$)").containsMatchIn(text)
    val hasScheme = !hostWithPort && Regex("^[A-Za-z][A-Za-z0-9+.-]*:").containsMatchIn(text)
    val candidate = if (hasScheme) text else "https://$text"
    val uri = try {
        URI(candidate)
    } catch (_: Exception) {
        return null
    }
    val scheme = uri.scheme?.lowercase(Locale.ROOT) ?: return null
    if (scheme == "http" || scheme == "https") {
        val host = uri.host ?: return null
        if (!host.contains('.') && host != "localhost") return null
    } else if (uri.schemeSpecificPart.isNullOrEmpty()) {
        return null
    }
    return candidate
}

fun linkLabel(url: String): String {
    val uri = try {
        URI(url)
    } catch (_: Exception) {
        return url
    }
    val host = uri.host ?: return uri.scheme ?: url
    return host.removePrefix("www.")
}
