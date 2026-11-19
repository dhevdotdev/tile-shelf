package com.tileshelf.app

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tileshelf.app.links.LinkCleaner
import com.tileshelf.app.links.LinkRules
import com.tileshelf.app.ui.CleanLinkCard
import com.tileshelf.app.ui.link
import com.tileshelf.app.ui.theme.AppTheme

sealed interface ClipCleanOutcome {
    data object NothingCopied : ClipCleanOutcome
    data object NoLink : ClipCleanOutcome
    data object Unreadable : ClipCleanOutcome
    data class AlreadyClean(val text: String) : ClipCleanOutcome
    data class Cleaned(val text: String, val removed: Int, val unwrapped: Int) : ClipCleanOutcome
}

fun cleanClipText(text: CharSequence?, cleaner: LinkCleaner): ClipCleanOutcome {
    if (text.isNullOrBlank()) return ClipCleanOutcome.NothingCopied
    val value = text.toString()
    if (!cleaner.containsLink(value)) return ClipCleanOutcome.NoLink
    val result = cleaner.clean(value)
    return if (result.changed) {
        ClipCleanOutcome.Cleaned(result.text, result.removed, result.unwrapped)
    } else {
        ClipCleanOutcome.AlreadyClean(value)
    }
}

/**
 * Android lets only the focused app read the clipboard, so the Clean link tile opens
 * this activity, which reads once it has window focus and shows the result as a card.
 */
class CleanLinkActivity : ComponentActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var handled = false
    internal var outcome by mutableStateOf<ClipCleanOutcome?>(null)
        private set
    internal var showing by mutableStateOf(true)
        private set
    private val hide = Runnable { showing = false }
    private val giveUp = Runnable {
        if (!handled) {
            handled = true
            show(ClipCleanOutcome.Unreadable)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        skipTransition(OVERRIDE_TRANSITION_OPEN)
        setContent {
            AppTheme(dynamicColor = true) {
                CleanLinkCard(
                    outcome = outcome,
                    showing = showing,
                    onHide = { showing = false },
                    onDismiss = ::finish,
                )
            }
        }
        handler.postDelayed(giveUp, FOCUS_TIMEOUT_MS)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus || handled) return
        handled = true
        handler.removeCallbacks(giveUp)
        show(cleanClipboard())
    }

    private fun show(result: ClipCleanOutcome) {
        outcome = result
        handler.postDelayed(hide, if (result.link != null) READ_LINK_MS else READ_SHORT_MS)
    }

    override fun finish() {
        super.finish()
        skipTransition(OVERRIDE_TRANSITION_CLOSE)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    private fun skipTransition(type: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(type, 0, 0)
        } else {
            overridePendingTransition(0, 0)
        }
    }

    private fun cleanClipboard(): ClipCleanOutcome {
        val manager = getSystemService(ClipboardManager::class.java) ?: return ClipCleanOutcome.Unreadable
        val clip = try {
            manager.primaryClip
        } catch (_: SecurityException) {
            return ClipCleanOutcome.Unreadable
        }
        val text = clip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(this)
        val result = cleanClipText(text, LinkCleaner(LinkRules.get(this)))
        if (result is ClipCleanOutcome.Cleaned) manager.setPrimaryClip(replacement(clip, result.text))
        return result
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

    private companion object {
        const val FOCUS_TIMEOUT_MS = 3000L
        const val READ_SHORT_MS = 1800L
        const val READ_LINK_MS = 2600L
    }
}
