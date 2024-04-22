package com.brickbreak

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.max = 100
        val currentProgress = progressBar.max

        ObjectAnimator.ofInt(progressBar, "progress", currentProgress)
            .setDuration(4000)
            .start()

        handler.postDelayed({
            startActivity(Intent(this, play::class.java))
            finish()
        }, 4000L)
    }
}
