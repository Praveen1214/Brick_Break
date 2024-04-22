package com.brickbreak

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge

class gameover : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private var score = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gameover)

        val score = intent.getIntExtra("SCORE", 0)

        // Display the score on the game over screen
        val scoreTextView: TextView = findViewById(R.id.scoreText)
        scoreTextView.text = "$score"

        val homebtn: ImageView = findViewById(R.id.home)
        homebtn.setOnClickListener {
            startActivity(Intent(this, play::class.java))
        }

        val restartbtn: ImageView = findViewById(R.id.restart)
        restartbtn.setOnClickListener {
            startActivity(Intent(this, game::class.java))
        }
    }
}
