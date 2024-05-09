package com.example.mygame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View
import android.media.MediaPlayer

class GameView(var c: Context, var gameTask: GameTask) : View(c) {
    // Member variables
    private var myPaint: Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myObjectsPosition = 0
    private val otherGhosts = ArrayList<HashMap<String, Any>>()
    private var backgroundMusic: MediaPlayer? = null

    var viewWidth = 0
    var viewHeight = 0

    // Constructor
    init {
        myPaint = Paint()

        // Initialize background music
        backgroundMusic = MediaPlayer.create(c, R.raw.spooky1)
        backgroundMusic?.isLooping = true
        backgroundMusic?.start()
    }

    // Override method called when the view is detached from the window
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Release background music for game view
        backgroundMusic?.release()
    }

    // Override method called when the view needs to be drawn
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Get the width and height of the view
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        // get ghost game objects periodically
        if (time % 600 < 10 + speed) {
            val map = HashMap<String, Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherGhosts.add(map)
        }

        time = time + 10 + speed

        // Calculate dimensions of game objects
        val oWidth = viewWidth / 5
        var oHeight = oWidth + 10

        // Set paint style
        myPaint!!.style = Paint.Style.FILL

        // Get drawable for the player's object (witch)
        val d = resources.getDrawable(R.drawable.witch2, null)

        // Set bounds for player's object
        d.setBounds(
            myObjectsPosition * viewWidth / 3 + viewWidth / 15 + 25,
            viewHeight - 2 - oHeight,
            myObjectsPosition * viewWidth / 3 + viewWidth / 15 + oWidth - 25,
            viewHeight - 2
        )
        // Draw player's object on canvas
        d.draw(canvas!!)

        myPaint!!.color = Color.GREEN
        var highScore = 0

        // Iterate through other ghosts
        for (i in otherGhosts.indices) {
            try {
                // Get position of ghost object
                val objectX = otherGhosts[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                var objectY = time - otherGhosts[i]["startTime"] as Int
                val d2 = resources.getDrawable(R.drawable.whiteghost, null)

                // Set bounds for ghost object
                d2.setBounds(
                    objectX + 25, objectY - oHeight, objectX + oWidth - 25, objectY
                )
                // Draw ghost object on canvas
                d2.draw(canvas)

                // Check for collision with player's object
                if (otherGhosts[i]["lane"] as Int == myObjectsPosition) {
                    if (objectY > viewHeight - 2 - oHeight
                        && objectY < viewHeight - 2
                    ) {
                        gameTask.closeGame(score)
                    }
                }

                // Remove ghost object if it goes beyond the view
                if (objectY > viewHeight + oHeight) {
                    otherGhosts.removeAt(i)
                    score++
                    speed = 1 + Math.abs(score / 10)
                    if (score > highScore) {
                        highScore = score
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Set text properties
        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 50f
        myPaint!!.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)

        // Draw score text
        val scoreText = "Score: $score"
        val speedText = "Ghost Attack Speed: $speed"

        // Measure text widths
        val scoreTextWidth = myPaint!!.measureText(scoreText)
        val speedTextWidth = myPaint!!.measureText(speedText)

        // Calculate position for center alignment
        val scoreX = (viewWidth - scoreTextWidth) / 2
        val speedX = (viewWidth - speedTextWidth) / 2
        val textY = 80f

        // Draw the score and speed text
        canvas.drawText(scoreText, scoreX, textY, myPaint!!)
        canvas.drawText(speedText, speedX, textY + 50, myPaint!!)

        invalidate()
    }



    // Override method to handle touch events
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                val x1 = event.x
                // Move player left if touch is on left half of the screen
                if (x1 < viewWidth / 2) {
                    if (myObjectsPosition > 0) {
                        myObjectsPosition--
                    }
                }
                // Move player right if touch is on right half of the screen
                if (x1 > viewWidth / 2) {
                    if (myObjectsPosition < 2) {
                        myObjectsPosition++
                    }
                }
                // Trigger redraw of the view
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // Handle touch release if needed
            }
        }

        return true
    }
}
