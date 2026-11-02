package com.tileshelf.app

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
import com.tileshelf.app.catalog.Publication
import com.tileshelf.app.ui.DeviceActions
import com.tileshelf.app.ui.TileShelfHost
import com.tileshelf.app.ui.theme.AppTheme

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
                TileShelfHost(
                    device = remember { DeviceActions(this@ShelfActivity) },
                    grantsRoute = route,
                    routeToken = token,
                    generation = tick,
                )
            }
        }
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
        const val EXTRA_ROUTE = "com.tileshelf.app.ROUTE"

        fun grantsName(intent: Intent?): String? {
            val raw = intent?.getStringExtra(EXTRA_ROUTE) ?: return null
            if (!raw.startsWith("grants:")) return null
            return raw.removePrefix("grants:")
        }
    }
}
