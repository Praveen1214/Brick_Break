package com.brickbreak

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge

class gameover : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var maxScoreText: TextView
    private var score = 0
    private var maxScore = 0
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gameover)

        sharedPreferences = getSharedPreferences("com.brickbreak.preferences", Context.MODE_PRIVATE)

        // Retrieve the current score and maximum score from intent extras
        score = intent.getIntExtra("SCORE", 0)
        maxScore = intent.getIntExtra("MAX_SCORE", 0)

        // Display the current score on the game over screen
        val scoreTextView: TextView = findViewById(R.id.scoreText)
        scoreTextView.text = "$score"

        // Display the maximum score on the game over screen
        val maxScoreTextView: TextView = findViewById(R.id.maxScoreText)
        maxScoreTextView.text = "$maxScore"

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
