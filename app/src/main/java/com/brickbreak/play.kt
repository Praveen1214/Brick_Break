package com.brickbreak

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import androidx.activity.enableEdgeToEdge

class play : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_play)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("com.brickbreak.preferences", Context.MODE_PRIVATE)

        // Initialize MediaPlayer
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer.isLooping = true

        val btnNavigate: ImageView = findViewById(R.id.play)
        btnNavigate.postDelayed({
            startActivity(Intent(this, game::class.java))
            finish()
        }, 4000L)

        val settingsButton: ImageView = findViewById(R.id.settings)
        settingsButton.setOnClickListener {
            showCustomDialog()
        }
    }

    private fun showCustomDialog() {
        // Create custom dialog
        val dialog = Dialog(this, R.style.CustomDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.activity_popup)

        val musicSwitch = dialog.findViewById<SwitchCompat>(R.id.music)
        val soundSwitch = dialog.findViewById<SwitchCompat>(R.id.sound)

        // Set initial state of switches based on saved preferences
        musicSwitch.isChecked = isMusicOn()
        soundSwitch.isChecked = isSoundOn()

        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the music switch
            saveMusicSetting(isChecked)
            // Pause or resume the MediaPlayer based on the state of the music switch
            if (isChecked) {
                mediaPlayer.start()
            } else {
                mediaPlayer.pause()
            }
        }

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the sound switch
            saveSoundSetting(isChecked)
            if (isChecked) {
                // If sound is on, set volume to full
                mediaPlayer.setVolume(1f, 1f)
            } else {
                // If sound is off, mute both music and sound
                mediaPlayer.setVolume(0f, 0f)
            }
        }

        // Adjust dialog width and height if needed
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)

        // Don't close the dialog when clicked outside of it
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.PopupAnimation
        dialog.show()
    }

    private fun saveMusicSetting(isMusicOn: Boolean) {
        sharedPreferences.edit {
            putBoolean("music", isMusicOn)
        }
    }

    private fun isMusicOn(): Boolean {
        return sharedPreferences.getBoolean("music", true)
    }

    private fun saveSoundSetting(isSoundOn: Boolean) {
        sharedPreferences.edit {
            putBoolean("sound", isSoundOn)
        }
    }

    private fun isSoundOn(): Boolean {
        return sharedPreferences.getBoolean("sound", true)
    }
}
