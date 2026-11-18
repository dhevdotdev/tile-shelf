package com.tileshelf.app

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Looper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class CleanLinkActivityTest {
    private val clipboard: ClipboardManager
        get() = RuntimeEnvironment.getApplication().getSystemService(ClipboardManager::class.java)

    private fun copy(text: String) = clipboard.setPrimaryClip(ClipData.newPlainText("link", text))

    private fun clipText() = clipboard.primaryClip?.getItemAt(0)?.text?.toString()

    private fun runWithFocus(): CleanLinkActivity {
        val activity = Robolectric.buildActivity(CleanLinkActivity::class.java).setup().get()
        activity.onWindowFocusChanged(true)
        shadowOf(Looper.getMainLooper()).idle()
        return activity
    }

    @Test
    @Config(sdk = [35])
    fun cleans_the_copied_link_once_focused_and_closes() {
        copy("Watch https://youtu.be/abc?si=xyz&t=9 now")
        val activity = runWithFocus()
        assertEquals("Watch https://youtu.be/abc?t=9 now", clipText())
        assertEquals("link", clipboard.primaryClipDescription?.label)
        assertTrue(activity.isFinishing)
    }

    @Test
    @Config(sdk = [32])
    fun says_link_cleaned_where_android_shows_no_copy_preview() {
        copy("https://example.com/?utm_source=a")
        runWithFocus()
        assertEquals("https://example.com/", clipText())
        assertEquals(RuntimeEnvironment.getApplication().getString(R.string.link_cleaned), ShadowToast.getTextOfLatestToast())
    }

    @Test
    @Config(sdk = [35])
    fun leaves_the_clipboard_alone_when_there_is_nothing_to_clean() {
        val app = RuntimeEnvironment.getApplication()
        copy("https://example.com/page?id=1")
        runWithFocus()
        assertEquals("https://example.com/page?id=1", clipText())
        assertEquals(app.getString(R.string.link_already_clean), ShadowToast.getTextOfLatestToast())

        copy("just words")
        runWithFocus()
        assertEquals("just words", clipText())
        assertEquals(app.getString(R.string.clipboard_has_no_link), ShadowToast.getTextOfLatestToast())
    }

    @Test
    @Config(sdk = [35])
    fun gives_up_and_closes_when_focus_never_arrives() {
        copy("https://example.com/?utm_source=a")
        val activity = Robolectric.buildActivity(CleanLinkActivity::class.java).setup().get()
        shadowOf(Looper.getMainLooper()).idleFor(java.time.Duration.ofSeconds(4))
        assertTrue(activity.isFinishing)
        assertEquals("https://example.com/?utm_source=a", clipText())
        assertEquals(
            RuntimeEnvironment.getApplication().getString(R.string.clipboard_unreadable),
            ShadowToast.getTextOfLatestToast(),
        )
    }
}
