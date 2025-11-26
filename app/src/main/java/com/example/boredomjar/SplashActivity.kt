package com.example.boredomjar

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Wait 3 seconds, then go to Name Input
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, NameActivity::class.java))
            finish()
        }, 3000)
    }
}