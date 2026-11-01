package com.flxholle.quicktiles.catalog

sealed interface Offer {
    data object Always : Offer
    data class Since(val api: Int) : Offer
    data class Until(val api: Int) : Offer
    data object Never : Offer
}

sealed interface RowSwitch {
    data class Offered(val published: Boolean) : RowSwitch
    data object TurnOffOnly : RowSwitch
}

fun Tile.shelfSwitch(sdk: Int, published: Boolean): RowSwitch? {
    val offered = when (val rule = offer) {
        Offer.Always -> true
        is Offer.Since -> sdk >= rule.api
        is Offer.Until -> sdk < rule.api
        Offer.Never -> false
    }
    return when {
        offered -> RowSwitch.Offered(published)
        published -> RowSwitch.TurnOffOnly
        else -> null
    }
}
