/*
 * Copyright (C) 2017 Adrián García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tileshelf.app.abstract_tiles

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import androidx.annotation.CallSuper
import com.tileshelf.app.catalog.GateCheck

abstract class SelectionTileService<T : Any> : BaseTileService() {

    lateinit var value: T

    final override fun onStartListening() {
        value = queryValue()
        updateState()
    }

    @CallSuper
    override fun onClick() {
        setNextValue()
    }

    private fun setNextValue() {
        val newValue = nextValue(value, getValueList())

        if (checkPermission()) {
            if (saveValue(newValue)) {
                value = newValue
            }
        }

        updateState()
    }

    fun checkPermission(): Boolean = GateCheck.proceed(this)

    private fun updateState() {
        qsTile.state = if (isActive(value)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        if (updateLabel())
            qsTile.label = getLabel(value)
        if (updateIcon())
            qsTile.icon = getIcon(value)

        qsTile.updateTile()
    }

    abstract fun isActive(value: T): Boolean

    abstract fun getValueList(): List<T>

    open fun nextValue(current: T, values: List<T>): T =
        values[(values.indexOf(current) + 1) % values.size]

    abstract fun queryValue(): T

    abstract fun saveValue(value: T): Boolean

    abstract fun getIcon(value: T): Icon

    abstract fun getLabel(value: T): CharSequence

    open fun updateLabel(): Boolean {
        return true
    }

    open fun updateIcon(): Boolean {
        return true
    }
}
