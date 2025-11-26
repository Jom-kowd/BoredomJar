package com.example.boredomjar

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
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
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity(), SensorEventListener {

    // --- LISTS ---
    private val activeTasks = listOf(
        "Do 20 jumping jacks.", "Go for a 10-minute walk.", "Clean your room for 5 minutes.",
        "Hold a plank for 45 seconds.", "Dance to your favorite song.", "Do 10 situps right now."
    )
    private val creativeTasks = listOf(
        "Draw a picture of your pet.", "Write a poem about a potato.", "Build a tower out of cards.",
        "Take a photo of something blue.", "Learn a magic trick on YouTube.", "Invent a new signature."
    )
    private val chillTasks = listOf(
        "Listen to a song from the 90s.", "Drink a glass of water.", "Meditate for 3 minutes.",
        "Read one chapter of a book.", "Watch a satisfying video.", "Stare at the wall for 60 seconds."
    )
    private val crushTasks = listOf(
        "Reply to your crush's story with a '🔥'.", "Send your crush a funny meme.",
        "Ask your crush for a song recommendation.", "Comment on their latest photo.",
        "Send a risky text, then put your phone away!", "Like their old photo from 2 years ago."
    )
    private val funnyTasks = listOf(
        "Try to lick your elbow.", "Talk in a British accent for 10 mins.", "Walk backwards into the next room.",
        "Sing 'Happy Birthday' to a random friend via voice note.", "Try not to blink for 60 seconds.",
        "Take a selfie making the ugliest face possible."
    )

    private var lastMission = ""
    private var mediaPlayer: MediaPlayer? = null
    private var sensorManager: SensorManager? = null
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mission Generator"

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.pop_sound)
        } catch (e: Exception) { e.printStackTrace() }

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val btnGenerate = findViewById<Button>(R.id.btnGetActivity)
        val btnShare = findViewById<Button>(R.id.btnShare)

        btnGenerate.setOnClickListener { generateMission() }

        btnShare.setOnClickListener {
            val tvResult = findViewById<TextView>(R.id.tvResult)
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

    private fun generateMission() {
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val rbActive = findViewById<RadioButton>(R.id.rbActive)
        val rbCreative = findViewById<RadioButton>(R.id.rbCreative)
        val rbCrush = findViewById<RadioButton>(R.id.rbCrush)
        val rbFunny = findViewById<RadioButton>(R.id.rbFunny)

        val selectedList = when {
            rbActive.isChecked -> activeTasks
            rbCreative.isChecked -> creativeTasks
            rbCrush.isChecked -> crushTasks
            rbFunny.isChecked -> funnyTasks
            else -> chillTasks
        }

        var newMission = selectedList[Random.nextInt(selectedList.size)]
        while (newMission == lastMission && selectedList.size > 1) {
            newMission = selectedList[Random.nextInt(selectedList.size)]
        }
        lastMission = newMission

        tvResult.alpha = 0f
        tvResult.text = newMission
        tvResult.animate().alpha(1f).setDuration(500)

        // Save Score
        val sharedPref = getSharedPreferences("GameStats", Context.MODE_PRIVATE)
        val currentCount = sharedPref.getInt("kill_count", 0)
        sharedPref.edit().putInt("kill_count", currentCount + 1).apply()

        vibratePhone()
        playSound()
    }

    private fun playSound() {
        val sharedPref = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        if (!sharedPref.getBoolean("sound_on", true)) return

        try {
            if (mediaPlayer?.isPlaying == true) mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
        } catch (e: Exception) {}
    }

    private fun vibratePhone() {
        val sharedPref = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        if (!sharedPref.getBoolean("vibrate_on", true)) return

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

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

            if (acceleration > 12) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastShakeTime > 1000) {
                    lastShakeTime = currentTime
                    generateMission()
                    Toast.makeText(this, "🫨 Jar Shaken!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onSupportNavigateUp(): Boolean { finish(); return true }
    override fun onDestroy() { super.onDestroy(); mediaPlayer?.release(); mediaPlayer = null }
}