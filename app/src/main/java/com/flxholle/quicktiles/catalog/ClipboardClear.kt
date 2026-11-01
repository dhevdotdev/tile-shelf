package com.flxholle.quicktiles.catalog

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Build
import androidx.annotation.RequiresApi

interface ClipTarget {
    fun clearPrimary()
    fun setEmpty()
}

enum class ClearOutcome { Cleared, ReplacedWithEmpty, Failed }

fun clearClip(target: ClipTarget, sdk: Int): ClearOutcome {
    if (sdk >= Build.VERSION_CODES.P) {
        try {
            target.clearPrimary()
            return ClearOutcome.Cleared
        } catch (_: RuntimeException) {
        }
    }
    return try {
        target.setEmpty()
        ClearOutcome.ReplacedWithEmpty
    } catch (_: RuntimeException) {
        ClearOutcome.Failed
    }
}

internal class AndroidClipboard(
    private val manager: ClipboardManager,
) : ClipTarget {
    @RequiresApi(28)
    override fun clearPrimary() {
        manager.clearPrimaryClip()
    }

    override fun setEmpty() {
        manager.setPrimaryClip(ClipData.newPlainText("", ""))
    }
}
