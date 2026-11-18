package com.tileshelf.app

import com.tileshelf.app.links.LinkCleaner
import com.tileshelf.app.links.LinkRules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LinkCleanerTest {
    private val rules: LinkRules by lazy {
        fun read(name: String) = File("src/main/assets/link-rules/$name").readText()
        LinkRules.parse(
            listOf(read("firefox-strip-on-share.json"), read("firefox-strip-on-share-lgpl.json")),
            read("clearurls-redirects.json"),
        )
    }
    private val cleaner by lazy { LinkCleaner(rules) }

    private fun cleaned(text: String) = cleaner.clean(text).text

    @Test
    fun bundled_rules_load_and_every_redirect_pattern_compiles() {
        assertTrue(rules.redirects.size > 50)
        assertTrue(rules.strip.global.containsAll(listOf("utm_source", "utm_medium", "fbclid", "gclid")))
    }

    @Test
    fun removes_tracking_params_and_keeps_the_rest_in_order() {
        val result = cleaner.clean("https://example.com/a?id=5&utm_source=x&utm_medium=y&b=2#top")
        assertEquals("https://example.com/a?id=5&b=2#top", result.text)
        assertEquals(2, result.removed)
    }

    @Test
    fun drops_the_question_mark_when_every_param_was_tracking() {
        assertEquals("https://example.com/", cleaned("https://example.com/?fbclid=abc&gclid=def"))
    }

    @Test
    fun leaves_a_clean_link_untouched() {
        val link = "https://example.com/search?q=a+b%20c&page=2#results"
        val result = cleaner.clean(link)
        assertEquals(link, result.text)
        assertFalse(result.changed)
    }

    @Test
    fun keeps_the_encoding_of_the_params_it_keeps() {
        assertEquals(
            "https://example.com/search?q=a+b%20c%2Fd",
            cleaned("https://example.com/search?q=a+b%20c%2Fd&utm_campaign=x"),
        )
    }

    @Test
    fun matches_param_names_without_case() {
        assertEquals("https://example.com/p", cleaned("https://example.com/p?UTM_Source=a&Fbclid=b"))
    }

    @Test
    fun youtube_loses_si_but_keeps_timestamps_playlists_and_video() {
        assertEquals("https://youtu.be/dQw4w9WgXcQ?t=42", cleaned("https://youtu.be/dQw4w9WgXcQ?si=abc&t=42"))
        assertEquals(
            "https://m.youtube.com/watch?v=x&list=PL1&index=3",
            cleaned("https://m.youtube.com/watch?v=x&list=PL1&index=3&si=z&pp=y"),
        )
    }

    @Test
    fun site_rules_stay_on_their_site() {
        val link = "https://example.com/watch?si=1&s=2&t=3"
        assertEquals(link, cleaned(link))
        assertEquals("https://x.com/user/status/1?t=abc", cleaned("https://x.com/user/status/1?s=20&t=abc"))
    }

    @Test
    fun keeps_at_signs_ports_and_user_info() {
        assertEquals("https://www.tiktok.com/@user/video/1", cleaned("https://www.tiktok.com/@user/video/1?utm_source=a"))
        assertEquals("https://me@Example.com:8080/x", cleaned("https://me@Example.com:8080/x?utm_source=a"))
    }

    @Test
    fun unwraps_google_and_facebook_redirects_then_cleans_the_target() {
        val google = cleaner.clean(
            "https://www.google.com/url?sa=t&q=https%3A%2F%2Fexample.com%2Fpage%3Futm_source%3Dg%26id%3D7&usg=AOv",
        )
        assertEquals("https://example.com/page?id=7", google.text)
        assertEquals(1, google.unwrapped)
        assertEquals(
            "https://example.com/",
            cleaned("https://l.facebook.com/l.php?u=https%3A%2F%2Fexample.com%2F%3Ffbclid%3Dabc&h=AT0"),
        )
    }

    @Test
    fun unwraps_google_amp_to_https() {
        assertEquals("https://example.com/article", cleaned("https://www.google.com/amp/s/example.com/article"))
    }

    @Test
    fun ignores_a_redirect_match_that_is_not_a_link() {
        val link = "https://www.instagram.com/p/abc/?menu=1"
        assertEquals(link, cleaned(link))
    }

    @Test
    fun cleans_links_inside_text_and_leaves_punctuation_outside() {
        assertEquals(
            "Look (https://example.com/x). And https://example.com/y, ok",
            cleaned("Look (https://example.com/x?utm_source=a). And https://example.com/y?fbclid=1, ok"),
        )
        assertEquals(
            "https://en.wikipedia.org/wiki/Foo_(bar)",
            cleaned("https://en.wikipedia.org/wiki/Foo_(bar)?utm_source=a"),
        )
    }

    @Test
    fun cleans_a_link_carried_inside_another_links_param() {
        assertEquals(
            "https://example.com/share?url=https%3A%2F%2Fother.com%2F",
            cleaned("https://example.com/share?url=https%3A%2F%2Fother.com%2F%3Futm_source%3Dx"),
        )
    }

    @Test
    fun finds_no_link_in_plain_text() {
        assertFalse(cleaner.containsLink("meet at 5, bring snacks"))
        assertEquals("meet at 5", cleaned("meet at 5"))
    }
}
