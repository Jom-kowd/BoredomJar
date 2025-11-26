package com.example.boredomjar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_name)
        supportActionBar?.hide()

        val etName = findViewById<EditText>(R.id.etName)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        // Check if name already exists to skip
        val sharedPref = getSharedPreferences("GameStats", Context.MODE_PRIVATE)
        val savedName = sharedPref.getString("user_name", "")
        if (!savedName.isNullOrEmpty()) {
            etName.setText(savedName)
        }

        btnContinue.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                sharedPref.edit().putString("user_name", name).apply()
                startActivity(Intent(this, MenuActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}