package com.example.boredomjar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        supportActionBar?.hide()

        val cardStart = findViewById<CardView>(R.id.cardStart)
        val cardCredits = findViewById<CardView>(R.id.cardCredits)
        val cardSettings = findViewById<CardView>(R.id.cardSettings)

        cardStart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        cardCredits.setOnClickListener {
            startActivity(Intent(this, CreditsActivity::class.java))
        }

        cardSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val sharedPref = getSharedPreferences("GameStats", Context.MODE_PRIVATE)

        val count = sharedPref.getInt("kill_count", 0)
        findViewById<TextView>(R.id.tvKillCount).text = "$count"

        val name = sharedPref.getString("user_name", "Player")
        findViewById<TextView>(R.id.tvGreeting).text = "Hi, $name!"
    }
}