package com.tileshelf.app

import com.tileshelf.app.catalog.ClearOutcome
import com.tileshelf.app.catalog.ClipTarget
import com.tileshelf.app.catalog.clearClip
import org.junit.Assert.assertEquals
import org.junit.Test

class ClearClipTest {
    @Test
    fun api_28_calls_clear_primary_and_stops() {
        val target = FakeClip()
        assertEquals(ClearOutcome.Cleared, clearClip(target, 28))
        assertEquals(listOf("clear"), target.calls)
    }

    @Test
    fun thrown_clear_writes_the_empty_clip() {
        val target = FakeClip(clearThrows = true)
        assertEquals(ClearOutcome.ReplacedWithEmpty, clearClip(target, 28))
        assertEquals(listOf("clear", "empty"), target.calls)
    }

    @Test
    fun api_24_writes_the_empty_clip_without_clear_primary() {
        val target = FakeClip()
        assertEquals(ClearOutcome.ReplacedWithEmpty, clearClip(target, 24))
        assertEquals(listOf("empty"), target.calls)
    }

    @Test
    fun both_throws_are_failed() {
        val target = FakeClip(clearThrows = true, emptyThrows = true)
        assertEquals(ClearOutcome.Failed, clearClip(target, 28))
        assertEquals(listOf("clear", "empty"), target.calls)
    }

    private class FakeClip(
        private val clearThrows: Boolean = false,
        private val emptyThrows: Boolean = false,
    ) : ClipTarget {
        val calls = mutableListOf<String>()

        override fun clearPrimary() {
            calls += "clear"
            if (clearThrows) throw IllegalStateException("clear")
        }

        override fun setEmpty() {
            calls += "empty"
            if (emptyThrows) throw IllegalStateException("empty")
        }
    }
}
