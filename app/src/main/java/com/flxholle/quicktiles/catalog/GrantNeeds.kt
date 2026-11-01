package com.flxholle.quicktiles.catalog

enum class GrantNeed {
    WriteSecure,
    Dump,
    ModifySystem,
    NotificationPolicy,
    Accessibility,
}

fun missingNeeds(gate: Gate, held: HeldGrants): List<GrantNeed> = when (gate) {
    Gate.None -> emptyList()
    Gate.WriteSecure -> missing(held.writeSecure, GrantNeed.WriteSecure)
    Gate.ModifySystem -> missing(held.modifySystem, GrantNeed.ModifySystem)
    Gate.NotificationPolicy -> missing(held.notificationPolicy, GrantNeed.NotificationPolicy)
    Gate.Accessibility -> missing(held.accessibility, GrantNeed.Accessibility)
    Gate.SecureAndDump -> missing(held.writeSecure, GrantNeed.WriteSecure) +
        missing(held.dump, GrantNeed.Dump)
    Gate.SecureAndModifySystem -> missing(held.writeSecure, GrantNeed.WriteSecure) +
        missing(held.modifySystem, GrantNeed.ModifySystem)
}

private fun missing(held: Boolean, need: GrantNeed): List<GrantNeed> =
    if (held) emptyList() else listOf(need)
