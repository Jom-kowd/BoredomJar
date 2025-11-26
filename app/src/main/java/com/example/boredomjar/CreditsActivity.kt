package com.example.boredomjar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class CreditsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        // Update title to reflect new content
        supportActionBar?.title = "About & Help"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}