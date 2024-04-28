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
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import androidx.activity.enableEdgeToEdge

class play : AppCompatActivity() {

    private lateinit var musicMediaPlayer: MediaPlayer
    private lateinit var soundMediaPlayer1: MediaPlayer
    private lateinit var soundMediaPlayer2: MediaPlayer

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_play)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("com.brickbreak.preferences", Context.MODE_PRIVATE)

        // Initialize MediaPlayer
        musicMediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        soundMediaPlayer1 =  MediaPlayer.create(this, R.raw.brick_hit_sound)
        soundMediaPlayer2 =  MediaPlayer.create(this, R.raw.paddle_hit_sound)

        musicMediaPlayer.isLooping = true

        val btnNavigate: ImageView = findViewById(R.id.play)
        btnNavigate.setOnClickListener {
            startActivity(Intent(this, game::class.java))
            finish()
        }

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

        val musicSwitch : Switch = dialog.findViewById(R.id.music)

        val soundSwitch : Switch = dialog.findViewById(R.id.sound)


        // Set initial state of switches based on saved preferences

        soundSwitch.isChecked = isSoundOn()

        musicSwitch.isChecked = isMusicOn()

        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the music switch
            saveMusicSetting(
                isChecked)
            // Pause or resume the MediaPlayer based on the state of the music switch
            if (isMusicOn()) {
                musicMediaPlayer.start()
            } else {
                musicMediaPlayer.pause()
            }
        }

        soundSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the sound switch
            saveSoundSetting(isChecked)
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
        val editor = sharedPreferences.edit()
        editor.putBoolean("music", isMusicOn)
        editor.apply()
        updateMusicPlayer(isMusicOn) // Corrected function call
    }

    private fun saveSoundSetting(isSoundOn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("sound", isSoundOn)
        editor.apply()
        updateSoundPlayers(isSoundOn)
    }

    private fun updateSoundPlayers(isSoundOn: Boolean) {
        if (isSoundOn) {
            soundMediaPlayer1.start()
            soundMediaPlayer2.start()
        } else {
            soundMediaPlayer1.pause()
            soundMediaPlayer2.pause()
        }
    }

    private fun updateMusicPlayer(isMusicOn: Boolean) {
        if (isMusicOn) {
            musicMediaPlayer.start()
        } else {
            musicMediaPlayer.pause()
        }
    }



    private fun isSoundOn(): Boolean {
        return sharedPreferences.getBoolean("sound", true)
    }

    private fun isMusicOn(): Boolean {
        return sharedPreferences.getBoolean("music", true)
    }


}
