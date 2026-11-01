package com.flxholle.quicktiles.catalog

sealed interface Gate {
    data object None : Gate
    data object WriteSecure : Gate
    data object ModifySystem : Gate
    data object NotificationPolicy : Gate
    data object SecureAndDump : Gate
    data object SecureAndModifySystem : Gate
    data object Accessibility : Gate
}

data class HeldGrants(
    val writeSecure: Boolean = false,
    val modifySystem: Boolean = false,
    val dump: Boolean = false,
    val notificationPolicy: Boolean = false,
    val accessibility: Boolean = false,
)

fun Gate.allows(held: HeldGrants): Boolean = when (this) {
    Gate.None -> true
    Gate.WriteSecure -> held.writeSecure
    Gate.ModifySystem -> held.modifySystem
    Gate.NotificationPolicy -> held.notificationPolicy
    Gate.SecureAndDump -> held.writeSecure && held.dump
    Gate.SecureAndModifySystem -> held.writeSecure && held.modifySystem
    Gate.Accessibility -> held.accessibility
}
