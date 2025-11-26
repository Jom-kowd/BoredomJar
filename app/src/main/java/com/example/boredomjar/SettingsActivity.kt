package com.example.boredomjar

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat // IMPT: Must match XML

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.title = "Preferences"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 1. Find Views (Matching IDs: swVibrate, swSound)
        val swVibrate = findViewById<SwitchCompat>(R.id.swVibrate)
        val swSound = findViewById<SwitchCompat>(R.id.swSound)

        // 2. Load Preferences
        val sharedPref = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)

        swVibrate.isChecked = sharedPref.getBoolean("vibrate_on", true)
        swSound.isChecked = sharedPref.getBoolean("sound_on", true)

        // 3. Save Listeners
        swVibrate.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("vibrate_on", isChecked).apply()
        }

        swSound.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("sound_on", isChecked).apply()
            if (isChecked) {
                Toast.makeText(this, "Sound ON 🔊", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}