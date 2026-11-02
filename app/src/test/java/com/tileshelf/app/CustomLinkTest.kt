package com.tileshelf.app

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import com.tileshelf.app.catalog.CustomApps
import com.tileshelf.app.catalog.CustomSlot
import com.tileshelf.app.catalog.CustomTarget
import com.tileshelf.app.catalog.Tile
import com.tileshelf.app.catalog.linkLabel
import com.tileshelf.app.catalog.parseLink
import com.tileshelf.app.ui.DeviceActions
import com.tileshelf.app.ui.LinkSave
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class CustomLinkTest {
    private val app get() = RuntimeEnvironment.getApplication()

    @Test
    fun bare_domains_get_https_and_full_links_are_kept() {
        assertEquals("https://example.com", parseLink("example.com"))
        assertEquals("https://example.com/a?b=c", parseLink("  example.com/a?b=c "))
        assertEquals("https://example.com:8080/x", parseLink("example.com:8080/x"))
        assertEquals("https://localhost:8080", parseLink("localhost:8080"))
        assertEquals("http://example.com", parseLink("http://example.com"))
        assertEquals("https://example.com", parseLink("example. com"))
    }

    @Test
    fun app_links_with_their_own_scheme_are_kept() {
        assertEquals("geo:0,0?q=coffee", parseLink("geo:0,0?q=coffee"))
        assertEquals("tel:123", parseLink("tel:123"))
        assertEquals("spotify:track:abc", parseLink("spotify:track:abc"))
    }

    @Test
    fun text_that_is_not_a_link_is_refused() {
        assertNull(parseLink(""))
        assertNull(parseLink("   "))
        assertNull(parseLink("hello"))
        assertNull(parseLink("https://"))
        assertNull(parseLink("mailto:"))
    }

    @Test
    fun default_names_come_from_the_host() {
        assertEquals("example.com", linkLabel("https://www.example.com/path"))
        assertEquals("geo", linkLabel("geo:0,0?q=coffee"))
    }

    @Test
    fun a_slot_holds_an_app_or_a_link_never_both() {
        CustomApps.write(app, CustomSlot.Two, CustomTarget.App("com.example.maps", "Maps"))
        assertEquals(CustomTarget.App("com.example.maps", "Maps"), CustomApps.read(app, CustomSlot.Two))
        CustomApps.write(app, CustomSlot.Two, CustomTarget.Link("https://example.com", "Example"))
        assertEquals(CustomTarget.Link("https://example.com", "Example"), CustomApps.read(app, CustomSlot.Two))
        CustomApps.write(app, CustomSlot.Two, CustomTarget.App("com.example.maps", "Maps"))
        assertEquals(CustomTarget.App("com.example.maps", "Maps"), CustomApps.read(app, CustomSlot.Two))
    }

    @Test
    fun saving_checks_the_link_and_that_an_app_can_open_it() {
        val device = DeviceActions(app)
        assertEquals(LinkSave.Invalid, device.saveLink(Tile.CustomOne, "not a link", ""))
        assertEquals(LinkSave.NoApp, device.saveLink(Tile.CustomOne, "geo:0,0?q=coffee", ""))
        assertNull(CustomApps.read(app, CustomSlot.One))

        val browser = ComponentName("com.example.browser", "com.example.browser.Main")
        shadowOf(app.packageManager).apply {
            addActivityIfNotPresent(browser)
            addIntentFilterForActivity(
                browser,
                IntentFilter(Intent.ACTION_VIEW).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    addDataScheme("https")
                },
            )
        }
        assertEquals(Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com")).resolveActivity(app.packageManager), browser)
        assertEquals(LinkSave.Saved, device.saveLink(Tile.CustomOne, "www.example.com", " "))
        assertEquals(CustomTarget.Link("https://www.example.com", "example.com"), CustomApps.read(app, CustomSlot.One))
        assertEquals(LinkSave.Invalid, device.saveLink(Tile.Counter, "example.com", ""))
    }
}
