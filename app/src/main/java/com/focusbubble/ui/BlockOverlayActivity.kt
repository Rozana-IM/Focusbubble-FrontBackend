package com.focusbubble.ui

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.focusbubble.R

class BlockOverlayActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_block_overlay)

        val packageName = intent.getStringExtra("PACKAGE_NAME") ?: "Unknown App"
        val message = findViewById<TextView>(R.id.blockMessage)
        message.text = "Focus Mode Active!\n\nYou can't use $packageName right now."
    }

    override fun onBackPressed() {
        // Prevent exiting the screen by back button
    }
}
