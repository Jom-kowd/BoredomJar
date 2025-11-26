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

        // Load saved setting
        val sharedPref = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        val isVibrateOn = sharedPref.getBoolean("vibrate_on", true)
        checkVibrate.isChecked = isVibrateOn

        // Save setting when clicked
        checkVibrate.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPref.edit()
            editor.putBoolean("vibrate_on", isChecked)
            editor.apply()

            if(isChecked) Toast.makeText(this, "Vibration ON", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, "Vibration OFF", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}