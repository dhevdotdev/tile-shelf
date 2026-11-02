package com.tileshelf.app.abstract_tiles

abstract class WriteSecureSettingsTileService<T : Any> : SelectionTileService<T>() {

    final override fun onClick() {
        unlockAndRun {
            super.onClick()
        }
    }
}
