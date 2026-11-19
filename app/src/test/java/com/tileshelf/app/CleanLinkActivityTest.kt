package com.tileshelf.app

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Looper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.time.Duration

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CleanLinkActivityTest {
    private val clipboard: ClipboardManager
        get() = RuntimeEnvironment.getApplication().getSystemService(ClipboardManager::class.java)

    private fun copy(text: String) = clipboard.setPrimaryClip(ClipData.newPlainText("link", text))

    private fun clipText() = clipboard.primaryClip?.getItemAt(0)?.text?.toString()

    private fun idle(seconds: Long = 0) {
        val looper = shadowOf(Looper.getMainLooper())
        if (seconds == 0L) looper.idle() else looper.idleFor(Duration.ofSeconds(seconds))
    }

    private fun focused(): CleanLinkActivity {
        val activity = Robolectric.buildActivity(CleanLinkActivity::class.java).setup().get()
        activity.onWindowFocusChanged(true)
        idle()
        return activity
    }

    @Test
    fun cleans_the_copied_link_once_focused_then_closes_after_the_card() {
        copy("Watch https://youtu.be/abc?si=xyz&t=9 now")
        val activity = focused()
        assertEquals("Watch https://youtu.be/abc?t=9 now", clipText())
        assertEquals("link", clipboard.primaryClipDescription?.label)
        assertEquals(ClipCleanOutcome.Cleaned("Watch https://youtu.be/abc?t=9 now", 1, 0), activity.outcome)
        assertFalse(activity.isFinishing)
        idle(seconds = 4)
        assertTrue(activity.isFinishing)
    }

    @Test
    fun counts_an_unwrapped_redirect() {
        copy("https://www.google.com/url?q=https%3A%2F%2Fexample.com%2F%3Futm_source%3Dg&sa=D")
        val outcome = focused().outcome
        assertEquals(ClipCleanOutcome.Cleaned("https://example.com/", 1, 1), outcome)
    }

    @Test
    fun leaves_the_clipboard_alone_when_there_is_nothing_to_clean() {
        copy("https://example.com/page?id=1")
        assertEquals(ClipCleanOutcome.AlreadyClean("https://example.com/page?id=1"), focused().outcome)
        assertEquals("https://example.com/page?id=1", clipText())

        copy("just words")
        assertEquals(ClipCleanOutcome.NoLink, focused().outcome)
        assertEquals("just words", clipText())
    }

    @Test
    fun reports_unreadable_and_closes_when_focus_never_arrives() {
        copy("https://example.com/?utm_source=a")
        val activity = Robolectric.buildActivity(CleanLinkActivity::class.java).setup().get()
        idle(seconds = 4)
        assertEquals(ClipCleanOutcome.Unreadable, activity.outcome)
        assertEquals("https://example.com/?utm_source=a", clipText())
        idle(seconds = 4)
        assertTrue(activity.isFinishing)
    }
}
