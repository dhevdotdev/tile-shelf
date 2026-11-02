package com.tileshelf.app.accessibility_tiles

import com.tileshelf.app.abstract_tiles.AccessibilityTileService
import com.tileshelf.app.utils.CustomAccessibilityService

class LockScreenTileService : AccessibilityTileService() {

    override fun getActionString() = CustomAccessibilityService.LOCK_SCREEN
}