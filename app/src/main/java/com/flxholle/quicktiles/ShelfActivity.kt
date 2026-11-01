package com.flxholle.quicktiles

import androidx.appcompat.app.AppCompatActivity
import com.flxholle.quicktiles.catalog.Publication

class ShelfActivity : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        Publication.reconcileShelf(this)
    }

    override fun onResume() {
        super.onResume()
        Publication.reconcileShelf(this)
    }

    companion object {
        const val EXTRA_ROUTE = "com.dhev.quicktiles.ROUTE"
    }
}
