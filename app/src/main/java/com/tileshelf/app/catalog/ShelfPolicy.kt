package com.tileshelf.app.catalog

data class ShelfEntry(val tile: Tile, val switch: RowSwitch)

fun shelfEntries(sdk: Int, values: Map<String, *>): List<ShelfEntry> {
    return Tile.entries.mapNotNull { tile ->
        val published = values[tile.key] == true
        val switch = tile.shelfSwitch(sdk, published) ?: return@mapNotNull null
        ShelfEntry(tile, switch)
    }
}

// decideSet does not read Offer, so the shelf has to refuse a turn-on by itself.
fun shelfAllowsSet(tile: Tile, on: Boolean, sdk: Int, published: Boolean): Boolean {
    val row = tile.shelfSwitch(sdk, published) ?: return false
    return !on || row is RowSwitch.Offered
}
