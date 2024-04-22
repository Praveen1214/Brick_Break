package com.brickbreak
import android.media.MediaPlayer
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.brickbreak.R
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import java.util.Random


class game : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var paddle: View
    private lateinit var ball: View
    private lateinit var brickContainer: LinearLayout

    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeedX = 0f

    private var ballSpeedY = 0f

    private var paddleX = 0f

    private var score = 0


    private val brickRows = 1

    private val brickColumns = 15
    private val brickWidth = 80
    private val brickHeight = 80
    private val brickMargin = 4

    private var isBallLaunched = false

    private var lives = 3

//    private lateinit var bgmusic: MediaPlayer
    private lateinit var brickHitSoundPlayer: MediaPlayer
    private lateinit var paddleHitSoundPlayer: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)

        sharedPreferences = getSharedPreferences("com.brickbreak.preferences", Context.MODE_PRIVATE)

        // Initialize MediaPlayer objects
       // bgmusic = MediaPlayer.create(this, R.raw.background_music)
        brickHitSoundPlayer = MediaPlayer.create(this, R.raw.brick_hit_sound)
        paddleHitSoundPlayer = MediaPlayer.create(this, R.raw.paddle_hit_sound)
       // bgmusic.isLooping = true
        scoreText = findViewById(R.id.scoreText)
        paddle = findViewById(R.id.paddle)
        ball = findViewById(R.id.ball)
        brickContainer = findViewById(R.id.brickContainer)
        initializeBricks()
        start()
    }


    private fun initializeBricks() {
        val brickWidthWithMargin = (brickWidth + brickMargin).toInt()
        val random = Random()

        // Define an array of three colors
        val colors = arrayOf(
            Color.parseColor("#7CB9E8"), // Green
            Color.parseColor("#F0F8FF"), // Orange
            Color.parseColor("#00308F"), // Blue
            Color.parseColor("#002D62"), // Light Gray
            Color.parseColor("#B2FFFF")  // Purple
        )

        for (row in 0 until brickRows) {
            val rowLayout = LinearLayout(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.layoutParams = params

            for (col in 0 until brickColumns) {
                val brick = View(this)
                val brickParams = LinearLayout.LayoutParams(brickWidth, brickHeight)
                brickParams.setMargins(brickMargin, brickMargin, brickMargin, brickMargin)
                brick.layoutParams = brickParams

                // Generate random index to select color from the array
                val colorIndex = random.nextInt(colors.size)
                brick.setBackgroundColor(colors[colorIndex])

                rowLayout.addView(brick)
            }

            brickContainer.addView(rowLayout)
        }
    }

    private var isGameOverStarted = false


    private fun isSoundOn(): Boolean {
        return sharedPreferences.getBoolean("sound", true)
    }

    private fun moveBall() {
        ballX += ballSpeedX
        ballY += ballSpeedY

        ball.x = ballX
        ball.y = ballY
    }

    private fun movePaddle(x: Float) {
        paddleX = x - paddle.width / 2
        paddle.x = paddleX
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun checkCollision() {
        // Check collision with walls
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

        if (ballX <= 0 || ballX + ball.width >= screenWidth) {
            ballSpeedX *= -1
        }

        if (ballY <= 0) {
            ballSpeedY *= -1
        }

        // Check collision with paddle
        if (ballY + ball.height >= paddle.y && ballY <= paddle.y + paddle.height
            && ballX + ball.width >= paddle.x && ballX <= paddle.x + paddle.width
            && ballSpeedY > 0  // Ensure the ball is moving downward
        ) {
            ballSpeedY *= -1
            score++
            scoreText.text = "$score"
            if (isSoundOn()) paddleHitSoundPlayer.start() else paddleHitSoundPlayer.pause()  // Play the paddle hit sound
        }


        // Check collision with bottom wall (paddle misses the ball)
        if (ballY + ball.height >= screenHeight) {
            // Game logic for when the ball goes past the paddle
            // You can implement actions such as reducing lives, resetting the ball, or displaying a message
            resetBallPosition() // Example: Reset the ball to its initial position
        }

        // Check collision with bricks
        for (row in 0 until brickRows) {
            val rowLayout = brickContainer.getChildAt(row) as LinearLayout

            val rowTop = rowLayout.y + brickContainer.y
            val rowBottom = rowTop + rowLayout.height

            for (col in 0 until brickColumns) {
                val brick = rowLayout.getChildAt(col) as View

                if (brick.visibility == View.VISIBLE) {
                    val brickLeft = brick.x + rowLayout.x
                    val brickRight = brickLeft + brick.width
                    val brickTop = brick.y + rowTop
                    val brickBottom = brickTop + brick.height

                    if (ballX + ball.width >= brickLeft && ballX <= brickRight
                        && ballY + ball.height >= brickTop && ballY <= brickBottom
                    ) {
                        brick.visibility = View.INVISIBLE
                        ballSpeedY *= -1
                        score++
                        scoreText.text = "$score"
                        if (isSoundOn())brickHitSoundPlayer.start() else brickHitSoundPlayer.pause()// Play the brick hit sound
                        return  // Exit the function after finding a collision with a brick
                    }
                }
            }
        }

        // Check collision with bottom wall (paddle misses the ball)
        if (ballY + ball.height >= screenHeight - 100) {
            // Reduce the number of lives
            lives--


            if (lives > 0 ) {
                Toast.makeText(this, "$lives balls left ", Toast.LENGTH_SHORT).show()
                updateHeartsDisplay()
            }


            paddle.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> {
                        movePaddle(event.rawX)

                    }

                }
                true



            }
            var bricksRemaining = 0  // Move the declaration here

            for (row in 0 until brickRows) {
                val rowLayout = brickContainer.getChildAt(row) as LinearLayout

                for (col in 0 until brickColumns) {
                    val brick = rowLayout.getChildAt(col) as View

                    if (brick.visibility == View.VISIBLE) {
                        bricksRemaining++
                    }
                }
            }

            if (lives <= 0) {
                // Game over condition: No more lives left
                gameOver(score)
            } else if (bricksRemaining == 0) {
                // All bricks destroyed, player wins the game
                gameWin()
            } else {
                // Reset the ball to its initial position
                resetBallPosition()
                start()
            }
        }



    }
    private fun updateHeartsDisplay() {
        val fillHeartResource = R.drawable.fillheart
        val emptyHeartResource = R.drawable.heart

        when (lives) {
            3 -> {
                findViewById<ImageView>(R.id.heart1).setImageResource(fillHeartResource)
                findViewById<ImageView>(R.id.heart2).setImageResource(fillHeartResource)
                findViewById<ImageView>(R.id.heart3).setImageResource(fillHeartResource)
            }
            2 -> {
                findViewById<ImageView>(R.id.heart1).setImageResource(emptyHeartResource)
                findViewById<ImageView>(R.id.heart2).setImageResource(fillHeartResource)
                findViewById<ImageView>(R.id.heart3).setImageResource(fillHeartResource)
            }
            1 -> {
                findViewById<ImageView>(R.id.heart1).setImageResource(emptyHeartResource)
                findViewById<ImageView>(R.id.heart2).setImageResource(emptyHeartResource)
                findViewById<ImageView>(R.id.heart3).setImageResource(fillHeartResource)
            }
            else -> {
                findViewById<ImageView>(R.id.heart1).setImageResource(emptyHeartResource)
                findViewById<ImageView>(R.id.heart2).setImageResource(emptyHeartResource)
                findViewById<ImageView>(R.id.heart3).setImageResource(emptyHeartResource)
            }
        }
    }


    private fun resetBallPosition() {
        // Reset the ball to its initial position
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        ballX = screenWidth / 2 - ball.width / 2
        ballY = screenHeight / 2 - ball.height / 2 + 525

        ball.x = ballX
        ball.y = ballY

        // Do not reset the ball's speed
        // ballSpeedX = 0 * screenDensity
        // ballSpeedY = 0 * screenDensity

        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX
    }


    private fun gameOver(score: Int) {
        if (!isGameOverStarted) {
            isGameOverStarted = true

            // Retrieve the maximum score from SharedPreferences
            val maxScore = sharedPreferences.getInt("maxScore", 0)

            // Check if the current score is greater than the maximum score
            if (score > maxScore) {
                // Update the maximum score in SharedPreferences
                sharedPreferences.edit().putInt("maxScore", score).apply()
            }

            // Start the gameover activity
            val intent = Intent(this, gameover::class.java)
            intent.putExtra("SCORE", score) // Pass the current score
            intent.putExtra("MAX_SCORE", maxScore) // Pass the maximum score
            startActivity(intent)
            finish()
        }
    }





    private fun gameWin() {

            var bricksRemaining = 0

            for (row in 0 until brickRows) {
                val rowLayout = brickContainer.getChildAt(row) as LinearLayout

                for (col in 0 until brickColumns) {
                    val brick = rowLayout.getChildAt(col) as View

                    if (brick.visibility == View.VISIBLE) {
                        bricksRemaining++
                    }
                }
            }

    }




    @SuppressLint("ClickableViewAccessibility")
    private fun movepaddle() {

        paddle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    movePaddle(event.rawX)



                }

            }
            true



        }
    }


    private fun start() {
        movepaddle()
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()
        //if (isSoundOn())bgmusic.start() else bgmusic.pause()
        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX

        ballX = paddleX + paddle.width / 2 - ball.width / 2
        ballY = screenHeight - 600  // Adjust as needed

        ball.x = ballX
        ball.y = ballY

        val brickHeightWithMargin = (brickHeight
                + brickMargin * screenDensity).toInt()

        ballSpeedX = 2 * screenDensity
        ballSpeedY = -3 * screenDensity

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = Long.MAX_VALUE
        animator.interpolator = LinearInterpolator()

        animator.addUpdateListener { animation ->
            moveBall()
            checkCollision()
        }
        animator.start()
    }

}