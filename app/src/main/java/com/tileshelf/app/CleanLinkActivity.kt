package com.tileshelf.app

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.widget.Toast
import com.tileshelf.app.links.LinkCleaner
import com.tileshelf.app.links.LinkRules

sealed interface ClipCleanOutcome {
    data object NothingCopied : ClipCleanOutcome
    data object NoLink : ClipCleanOutcome
    data object AlreadyClean : ClipCleanOutcome
    data class Cleaned(val text: String) : ClipCleanOutcome
}

fun cleanClipText(text: CharSequence?, cleaner: LinkCleaner): ClipCleanOutcome {
    if (text.isNullOrBlank()) return ClipCleanOutcome.NothingCopied
    val value = text.toString()
    if (!cleaner.containsLink(value)) return ClipCleanOutcome.NoLink
    val result = cleaner.clean(value)
    return if (result.changed) ClipCleanOutcome.Cleaned(result.text) else ClipCleanOutcome.AlreadyClean
}

/**
 * Android lets only the focused app read the clipboard, so the Clean link tile opens
 * this invisible activity, which reads once it has window focus and then closes.
 */
class CleanLinkActivity : Activity() {
    private val handler = Handler(Looper.getMainLooper())
    private var handled = false
    private val giveUp = Runnable {
        if (!handled) {
            handled = true
            toast(R.string.clipboard_unreadable)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler.postDelayed(giveUp, FOCUS_TIMEOUT_MS)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus || handled) return
        handled = true
        handler.removeCallbacks(giveUp)
        handler.post {
            cleanClipboard()
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacks(giveUp)
        super.onDestroy()
    }

    private fun cleanClipboard() {
        val manager = getSystemService(ClipboardManager::class.java)
        val clip = try {
            manager?.primaryClip
        } catch (_: SecurityException) {
            null
        }
        val text = clip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(this)
        when (val outcome = cleanClipText(text, LinkCleaner(LinkRules.get(this)))) {
            ClipCleanOutcome.NothingCopied -> toast(R.string.clipboard_has_nothing)
            ClipCleanOutcome.NoLink -> toast(R.string.clipboard_has_no_link)
            ClipCleanOutcome.AlreadyClean -> toast(R.string.link_already_clean)
            is ClipCleanOutcome.Cleaned -> {
                manager?.setPrimaryClip(replacement(clip, outcome.text))
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) toast(R.string.link_cleaned)
            }
        }
    }

    private fun replacement(original: ClipData?, text: String): ClipData {
        val clip = ClipData.newPlainText(original?.description?.label ?: "", text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            original?.description?.extras?.getBoolean(ClipDescription.EXTRA_IS_SENSITIVE) == true
        ) {
            clip.description.extras = PersistableBundle().apply {
                putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
            }
        }
        return clip
    }

    private fun toast(message: Int) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val FOCUS_TIMEOUT_MS = 3000L
    }
}
