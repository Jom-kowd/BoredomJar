package com.example.boredomjar

import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val checkVibrate = findViewById<CheckBox>(R.id.checkVibration)
        val checkSound = findViewById<CheckBox>(R.id.checkSound)

        val sharedPref = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)

        checkVibrate.isChecked = sharedPref.getBoolean("vibrate_on", true)
        checkVibrate.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("vibrate_on", isChecked).apply()
        }

        checkSound.isChecked = sharedPref.getBoolean("sound_on", true)
        checkSound.setOnCheckedChangeListener { _, isChecked ->
            sharedPref.edit().putBoolean("sound_on", isChecked).apply()
            if(isChecked) Toast.makeText(this, "Sound ON", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}