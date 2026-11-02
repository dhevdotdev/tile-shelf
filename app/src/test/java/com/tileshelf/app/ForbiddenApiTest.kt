package com.tileshelf.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ForbiddenApiTest {
    @Test
    fun component_enable_is_only_publication() {
        val hits = mainSources().filter { it.readText().contains("setComponentEnabledSetting") }
        assertEquals(listOf("Publication.kt"), hits.map { it.name })
    }

    @Test
    fun collapse_is_only_tile_intents_and_uses_the_wrapper() {
        val hits = mainSources().filter { it.readText().contains("startActivityAndCollapse") }
        assertEquals(listOf("TileIntents.kt"), hits.map { it.name })
        val text = hits.single().readText()
        assertTrue(text.contains("PendingIntentActivityWrapper"))
        assertTrue(text.contains("TileServiceCompat"))
    }

    @Test
    fun clear_clipboard_service_starts_no_activity() {
        val text = File(
            moduleRoot(),
            "src/main/java/com/tileshelf/app/tiles/ClearClipboardTileService.kt",
        ).readText()
        assertFalse(text.contains("startActivity"))
        assertFalse(text.contains("TileIntents"))
        assertFalse(text.contains("Intent("))
    }

    @Test
    fun intent_tile_null_check_precedes_add_flags() {
        val text = File(
            moduleRoot(),
            "src/main/java/com/tileshelf/app/abstract_tiles/IntentTileService.java",
        ).readText()
        val nullCheck = text.indexOf("intent == null")
        val addFlags = text.indexOf("addFlags")
        assertTrue(nullCheck >= 0 && addFlags > nullCheck)
    }

    @Test
    fun banned_symbols_are_absent() {
        val symbols = listOf(
            "AlertDialog",
            "SearchPreference",
            "aboutlibraries",
            "WirelessUsbDebugging",
            "usb_wifi_debugging",
            "AD_ID",
            "com.google.android.gms",
            "RootTileService",
            "Runtime.getRuntime",
        )
        val failures = mutableListOf<String>()
        for (file in mainSources() + gradleSources()) {
            val text = file.readText()
            for (symbol in symbols) {
                val found = if (symbol == "aboutlibraries") {
                    text.contains(symbol, ignoreCase = true)
                } else {
                    text.contains(symbol)
                }
                if (found) failures += "${file.path} contains $symbol"
            }
            if (SU_PROCESS.containsMatchIn(text)) {
                failures += "${file.path} contains su"
            }
        }
        assertEquals(emptyList<String>(), failures)
    }

    private fun mainSources(): List<File> =
        File(moduleRoot(), "src/main").walkTopDown()
            .filter { it.isFile && it.extension in SOURCE_EXTENSIONS }
            .toList()

    private fun gradleSources(): List<File> {
        val repo = moduleRoot().parentFile
        return listOf(
            File(repo, "build.gradle.kts"),
            File(repo, "settings.gradle.kts"),
            File(repo, "gradle/libs.versions.toml"),
            File(moduleRoot(), "build.gradle.kts"),
        )
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
        val SOURCE_EXTENSIONS = setOf("kt", "java", "xml")
        val SU_PROCESS = Regex("(?<![A-Za-z0-9_])su(?![A-Za-z0-9_])")
    }
}
