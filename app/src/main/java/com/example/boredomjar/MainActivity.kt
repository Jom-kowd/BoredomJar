package com.example.boredomjar

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    // --- 1. THE TASK LISTS ---

    private val activeTasks = listOf(
        "Do 20 jumping jacks.",
        "Go for a 10-minute walk.",
        "Clean your room for 5 minutes.",
        "Hold a plank for 45 seconds.",
        "Dance to your favorite song.",
        "Do 10 situps right now."
    )

    private val creativeTasks = listOf(
        "Draw a picture of your pet.",
        "Write a poem about a potato.",
        "Build a tower out of cards.",
        "Take a photo of something blue.",
        "Learn a magic trick on YouTube.",
        "Invent a new signature."
    )

    private val chillTasks = listOf(
        "Listen to a song from the 90s.",
        "Drink a glass of water.",
        "Meditate for 3 minutes.",
        "Read one chapter of a book.",
        "Watch a satisfying video.",
        "Stare at the wall and do nothing for 60 seconds."
    )

    // NEW: Crush / Social Challenges
    private val crushTasks = listOf(
        "Reply to your crush's story with a '🔥' reaction.",
        "Send your crush a funny meme.",
        "Ask your crush for a song recommendation.",
        "Comment on their latest photo.",
        "Send a risky text to your crush, then put your phone away!",
        "Like their old photo from 2 years ago (if you dare!).",
        "Text them: 'I had a weird dream about you...'"
    )

    // NEW: Funny / Playable Moments
    private val funnyTasks = listOf(
        "Try to lick your elbow.",
        "Talk in a British accent for the next 10 minutes.",
        "Walk backwards into the next room.",
        "Send a voice message singing 'Happy Birthday' to a random friend.",
        "Try not to blink for 60 seconds.",
        "Balance a spoon on your nose.",
        "Take a selfie making the ugliest face possible."
    )

    private var lastMission = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mission Generator"

        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnGenerate = findViewById<Button>(R.id.btnGetActivity)
        val btnShare = findViewById<Button>(R.id.btnShare)

        // Find Radio Buttons
        val rbActive = findViewById<RadioButton>(R.id.rbActive)
        val rbCreative = findViewById<RadioButton>(R.id.rbCreative)
        val rbCrush = findViewById<RadioButton>(R.id.rbCrush) // New
        val rbFunny = findViewById<RadioButton>(R.id.rbFunny) // New

        // LOGIC: GENERATE MISSION
        btnGenerate.setOnClickListener {

            // Determine which list to use
            val selectedList = when {
                rbActive.isChecked -> activeTasks
                rbCreative.isChecked -> creativeTasks
                rbCrush.isChecked -> crushTasks   // New Logic
                rbFunny.isChecked -> funnyTasks   // New Logic
                else -> chillTasks
            }

            // Pick random item
            var newMission = selectedList[Random.nextInt(selectedList.size)]

            // Avoid Repeats
            while (newMission == lastMission && selectedList.size > 1) {
                newMission = selectedList[Random.nextInt(selectedList.size)]
            }
            lastMission = newMission

            // Animation
            tvResult.alpha = 0f
            tvResult.text = newMission
            tvResult.animate().alpha(1f).setDuration(500)

            // Vibrate if allowed
            vibratePhone()
        }

        // SHARE LOGIC
        btnShare.setOnClickListener {
            val currentMission = tvResult.text.toString()
            if (currentMission.contains("Pick a vibe")) {
                Toast.makeText(this, "Generate a mission first!", Toast.LENGTH_SHORT).show()
            } else {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Boredom Jar Challenge:\n$currentMission")
                    type = "text/plain"
                }
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
        }
    }

    private fun vibratePhone() {
        // Check settings first (Optional, but good practice)
        val sharedPref = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        val isVibrateOn = sharedPref.getBoolean("vibrate_on", true)

        if (!isVibrateOn) return // Stop if vibration is off in settings

        val vibrator = if (Build.VERSION.SDK_INT >= 31) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}