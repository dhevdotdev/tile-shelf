package com.flxholle.quicktiles.abstract_tiles

abstract class WriteSecureSettingsTileService<T : Any> : SelectionTileService<T>() {

    final override fun onClick() {
        unlockAndRun {
            super.onClick()
        }
    }
}
