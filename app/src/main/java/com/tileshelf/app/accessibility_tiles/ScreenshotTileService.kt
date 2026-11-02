package com.tileshelf.app.accessibility_tiles

import com.tileshelf.app.abstract_tiles.AccessibilityTileService
import com.tileshelf.app.utils.CustomAccessibilityService

class ScreenshotTileService : AccessibilityTileService() {

    override fun getActionString() = CustomAccessibilityService.TAKE_SCREENSHOT
}