package com.example.mygame

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity(), GameTask {
    lateinit var rootLayout : LinearLayout
    lateinit var startBtn : Button
    lateinit var mGameView: GameView
    lateinit var scoreTextView: TextView
    lateinit var highestScoreTextView: TextView
    var highestScore = 0
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Views
        startBtn = findViewById(R.id.startBtn)
        rootLayout = findViewById(R.id.rootLayout)
        scoreTextView = findViewById(R.id.score)
        highestScoreTextView = findViewById(R.id.highestScore)


        // Set click listener for start button
        startBtn.setOnClickListener {
            mGameView = GameView(this, this)
            mGameView.setBackgroundResource(R.drawable.back4)
            rootLayout.addView(mGameView)
            startBtn.visibility = View.GONE
            scoreTextView.visibility = View.GONE
        }

        // Load highest score from SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        highestScore = sharedPreferences.getInt("highest_score", 0)
        // Display highest score
        displayHighestScore()
    }


    // Method to handle closing the game
    override fun closeGame(mScore: Int) {
        scoreTextView.text = "Score : $mScore"
        rootLayout.removeView(mGameView)
        startBtn.visibility = View.VISIBLE
        scoreTextView.visibility = View.VISIBLE

        // Update highest score
        if (mScore > highestScore) {
            highestScore = mScore
            sharedPreferences.edit().putInt("highest_score", highestScore).apply()
            displayHighestScore()
        }
    }

    // Method to display the highest score
    private fun displayHighestScore() {
        highestScoreTextView.text = "Highest Score: $highestScore"
    }
}
