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
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val activeTasks = listOf("Do 20 jumping jacks.", "Go for a walk.", "Clean room for 5 mins.", "Plank for 45s.", "Dance!", "10 situps.")
    private val creativeTasks = listOf("Draw your pet.", "Write a potato poem.", "Build card tower.", "Photo of something blue.", "Learn magic trick.", "New signature.")
    private val chillTasks = listOf("Listen to 90s song.", "Drink water.", "Meditate 3 mins.", "Read a chapter.", "Watch satisfying video.", "Stare at wall.")
    private val crushTasks = listOf("Reply '🔥' to crush.", "Send meme to crush.", "Ask for song rec.", "Comment on photo.", "Risky text!", "Like old photo.")
    private val funnyTasks = listOf("Lick your elbow.", "British accent 10 mins.", "Walk backwards.", "Sing Happy Birthday voice note.", "Don't blink 60s.", "Ugly selfie.")
    private val chaosTasks = listOf("Clothes backward 1 hr.", "Text parent 'I know the truth'.", "Eat hot sauce.", "Stare at corner 5 mins.", "Whisper only.", "Mix OJ and Milk.")

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

        // Setup Sound
        try { mediaPlayer = MediaPlayer.create(this, R.raw.pop_sound) } catch (e: Exception) {}

        // Setup Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Check Secret Logic
        val rbChaos = findViewById<RadioButton>(R.id.rbChaos)
        val sharedPref = getSharedPreferences("GameStats", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("chaos_unlocked", false)) {
            rbChaos.visibility = View.VISIBLE
        }

        val btnGenerate = findViewById<Button>(R.id.btnGetActivity)
        val btnShare = findViewById<Button>(R.id.btnShare)

        btnGenerate.setOnClickListener { generateMission() }

        btnShare.setOnClickListener {
            val tvResult = findViewById<TextView>(R.id.tvResult)
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Challenge: ${tvResult.text}")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(intent, "Share via"))
        }
    }

    private fun generateMission() {
        val tvResult = findViewById<TextView>(R.id.tvResult)
        val rbActive = findViewById<RadioButton>(R.id.rbActive)
        val rbCreative = findViewById<RadioButton>(R.id.rbCreative)
        val rbCrush = findViewById<RadioButton>(R.id.rbCrush)
        val rbFunny = findViewById<RadioButton>(R.id.rbFunny)
        val rbChaos = findViewById<RadioButton>(R.id.rbChaos)

        val selectedList = when {
            rbActive.isChecked -> activeTasks
            rbCreative.isChecked -> creativeTasks
            rbCrush.isChecked -> crushTasks
            rbFunny.isChecked -> funnyTasks
            rbChaos.isChecked -> chaosTasks
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

        // Save Stats
        val sharedPref = getSharedPreferences("GameStats", Context.MODE_PRIVATE)
        val count = sharedPref.getInt("kill_count", 0)
        sharedPref.edit().putInt("kill_count", count + 1).apply()

        vibratePhone()
        playSound()
    }

    private fun playSound() {
        val sharedPref = getSharedPreferences("GameSettings", Context.MODE_PRIVATE)
        if (sharedPref.getBoolean("sound_on", true)) {
            try {
                if (mediaPlayer?.isPlaying == true) mediaPlayer?.seekTo(0)
                mediaPlayer?.start()
            } catch (e: Exception) {}
        }
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
        accelerometer?.also { sensor -> sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI) }
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