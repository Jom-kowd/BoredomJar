package com.example.boredomjar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView // Import CardView

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        // Hide the top action bar to make it look full-screen professional
        supportActionBar?.hide()

        // 1. Find the Cards (Not buttons anymore, but Cards!)
        val cardStart = findViewById<CardView>(R.id.cardStart)
        val cardCredits = findViewById<CardView>(R.id.cardCredits)

        // 2. Start Game Logic
        cardStart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 3. Credits Logic
        cardCredits.setOnClickListener {
            val intent = Intent(this, CreditsActivity::class.java)
            startActivity(intent)
        }
    }
}