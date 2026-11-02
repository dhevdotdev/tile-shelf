package com.tileshelf.app

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

class KeyboardPickerActivity : Activity() {
    private var requested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requested = savedInstanceState?.getBoolean(REQUESTED) ?: false
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        val field = EditText(this).apply {
            alpha = 0f
            isFocusableInTouchMode = true
        }
        setContentView(field)
        field.requestFocus()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !requested) {
            requested = true
            window.decorView.post {
                val manager = getSystemService(InputMethodManager::class.java)
                if (manager == null) finish() else manager.showInputMethodPicker()
            }
        } else if (hasFocus && requested) {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(REQUESTED, requested)
    }

    private companion object {
        const val REQUESTED = "requested"
    }
}
