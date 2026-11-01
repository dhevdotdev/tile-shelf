package com.flxholle.quicktiles.catalog

import android.service.quicksettings.TileService

object GateCheck {
    @JvmStatic
    fun proceed(service: TileService): Boolean {
        val tile = Tile.fromService(service.javaClass)
        if (tile.gate.allows(DeviceGrants.held(service))) return true
        TileIntents.collapseToGrants(service, tile)
        return false
    }
}
