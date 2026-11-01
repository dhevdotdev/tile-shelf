package com.flxholle.quicktiles

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.flxholle.quicktiles.catalog.Publication
import com.flxholle.quicktiles.ui.DeviceActions
import com.flxholle.quicktiles.ui.DhevHost
import com.flxholle.quicktiles.ui.theme.AppTheme

class ShelfActivity : ComponentActivity() {
    private var grantsRoute by mutableStateOf<String?>(null)
    private var routeToken by mutableIntStateOf(0)
    private var generation by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        grantsRoute = grantsName(intent)
        enableEdgeToEdge()
        setContent {
            val route = grantsRoute
            val token = routeToken
            val tick = generation
            AppTheme(dynamicColor = true) {
                DhevHost(
                    device = remember { DeviceActions(this@ShelfActivity) },
                    grantsRoute = route,
                    routeToken = token,
                    generation = tick,
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Publication.reconcileShelf(this)
    }

    override fun onResume() {
        super.onResume()
        Publication.reconcileShelf(this)
        generation++
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        grantsRoute = grantsName(intent)
        routeToken++
    }

    companion object {
        const val EXTRA_ROUTE = "com.dhev.quicktiles.ROUTE"

        fun grantsName(intent: Intent?): String? {
            val raw = intent?.getStringExtra(EXTRA_ROUTE) ?: return null
            if (!raw.startsWith("grants:")) return null
            return raw.removePrefix("grants:")
        }
    }
}
