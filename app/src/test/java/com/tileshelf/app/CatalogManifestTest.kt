package com.tileshelf.app

import com.tileshelf.app.catalog.CustomSlot
import com.tileshelf.app.catalog.Tile
import com.tileshelf.app.utils.SharedPreferencesUtil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class CatalogManifestTest {
    @Test
    fun services_match_tile_entries_on_class_and_active_tile() {
        val expected = Tile.entries.associate { it.service.java.name to it.activeTile }
        assertEquals(expected, qsServices())
    }

    @Test
    fun wireless_debugging_service_is_absent() {
        val text = manifestFile().readText()
        assertFalse(text.contains("WirelessUsbDebugging"))
        assertFalse(text.contains("usb_wifi_debugging"))
        assertTrue(Tile.entries.none { it.key == "usb_wifi_debugging" })
        assertTrue(Tile.entries.any { it.key == "clear_clipboard" })
        assertEquals(55, Tile.entries.size)
    }

    @Test
    fun manifest_has_no_ad_id_and_no_widget_receiver() {
        val text = manifestFile().readText()
        assertFalse(text.contains("AD_ID"))
        assertFalse(text.contains("appwidget"))
        assertFalse(text.contains("AppWidget"))
        assertEquals(listOf(".BootReceiver"), receiverNames())
    }

    @Test
    fun custom_slot_keys_match_the_service_getters() {
        val cases = listOf(
            "OpenAppOneTileService.java" to CustomSlot.One,
            "OpenAppTwoTileService.java" to CustomSlot.Two,
            "OpenAppThreeTileService.java" to CustomSlot.Three,
            "OpenAppFourTileService.java" to CustomSlot.Four,
            "OpenAppFiveTileService.java" to CustomSlot.Five,
        )
        val prefs = SharedPreferencesUtil::class.java
        for ((file, slot) in cases) {
            val source = File(moduleRoot(), "src/main/java/com/tileshelf/app/intent_tiles/$file").readText()
            val packageField = returnedConstant(source, "getPreferencesKey")
            val labelField = returnedConstant(source, "getLabelPreferenceKey")
            assertEquals(slot.packageKey, prefs.getField(packageField).get(null))
            assertEquals(slot.labelKey, prefs.getField(labelField).get(null))
        }
    }

    private fun qsServices(): Map<String, Boolean> {
        val services = document().getElementsByTagName("service")
        val found = linkedMapOf<String, Boolean>()
        for (index in 0 until services.length) {
            val service = services.item(index) as Element
            if (!isQsTile(service)) continue
            val name = attr(service, "name")
            found[expand(name)] = activeTile(service)
        }
        return found
    }

    private fun receiverNames(): List<String> {
        val receivers = document().getElementsByTagName("receiver")
        return (0 until receivers.length).map { attr(receivers.item(it) as Element, "name") }
    }

    private fun isQsTile(service: Element): Boolean {
        val children = service.childNodes
        for (index in 0 until children.length) {
            val child = children.item(index)
            if (child !is Element || child.tagName != "intent-filter") continue
            val actions = child.getElementsByTagName("action")
            for (actionIndex in 0 until actions.length) {
                if (attr(actions.item(actionIndex) as Element, "name") == QS_TILE) return true
            }
        }
        return false
    }

    private fun activeTile(service: Element): Boolean {
        val metas = service.childNodes
        for (index in 0 until metas.length) {
            val child = metas.item(index)
            if (child !is Element || child.tagName != "meta-data") continue
            if (attr(child, "name") == ACTIVE_TILE) return attr(child, "value") == "true"
        }
        return false
    }

    private fun expand(name: String): String =
        if (name.startsWith(".")) "com.tileshelf.app$name" else name

    private fun attr(element: Element, name: String): String {
        val namespaced = element.getAttributeNS(ANDROID_NS, name)
        if (namespaced.isNotEmpty()) return namespaced
        return element.getAttribute("android:$name")
    }

    private fun document() = DocumentBuilderFactory.newInstance().apply {
        isNamespaceAware = true
    }.newDocumentBuilder().parse(manifestFile())

    private fun manifestFile() = File(moduleRoot(), "src/main/AndroidManifest.xml")

    private fun returnedConstant(source: String, method: String): String {
        val pattern = Regex("""$method\(\)\s*\{\s*return SharedPreferencesUtil\.([A-Z0-9_]+);\s*\}""")
        return pattern.find(source)?.groupValues?.get(1)
            ?: error("$method does not return a SharedPreferencesUtil constant")
    }

    private fun moduleRoot(): File {
        var dir = File("").absoluteFile
        repeat(4) {
            if (File(dir, "src/main/AndroidManifest.xml").isFile) return dir
            if (File(dir, "app/src/main/AndroidManifest.xml").isFile) return File(dir, "app")
            dir = dir.parentFile ?: return@repeat
        }
        error("AndroidManifest.xml not found from ${File("").absolutePath}")
    }

    private companion object {
        const val ANDROID_NS = "http://schemas.android.com/apk/res/android"
        const val QS_TILE = "android.service.quicksettings.action.QS_TILE"
        const val ACTIVE_TILE = "android.service.quicksettings.ACTIVE_TILE"
    }
}
