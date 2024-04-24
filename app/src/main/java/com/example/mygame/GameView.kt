package com.example.mygame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

class GameView (var c:Context, var gameTask: GameTask):View(c)
{
    private var myPaint : Paint? = null
    private var speed = 1
    private var time = 0
    private var score = 0
    private var myObjectsPosition= 0
    private val otherGhosts = ArrayList<HashMap<String,Any>>()

    var viewWidth = 0
    var viewHeight = 0
    init {
        myPaint = Paint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewWidth = this.measuredWidth
        viewHeight = this.measuredHeight

        if (time % 700 < 10 + speed){
            val map = HashMap<String,Any>()
            map["lane"] = (0..2).random()
            map["startTime"] = time
            otherGhosts.add(map)
        }
        time = time + 10 + speed
        val oWidth = viewWidth / 5
        var oHeight = oWidth + 10
        myPaint!!.style = Paint.Style.FILL
        val d = resources.getDrawable(R.drawable.man,null)

        d.setBounds(
            myObjectsPosition * viewWidth / 3 + viewWidth /15 + 25,
            viewHeight-2 - oHeight,
            myObjectsPosition * viewWidth / 3 + viewWidth / 15 + oWidth - 25 ,
            viewHeight - 2
        )
        d.draw(canvas!!)
        myPaint!!.color = Color.GREEN
        var highScore = 0

        for( i in otherGhosts.indices){
            try{
                val objectX = otherGhosts[i]["lane"] as Int * viewWidth / 3 + viewWidth / 15
                var objectY = time - otherGhosts[i]["startTime"] as Int
                val d2 = resources.getDrawable(R.drawable.ghost ,null)

                d2.setBounds(
                    objectX + 25 , objectY - oHeight ,objectX + oWidth - 25 , objectY
                )
                d2.draw(canvas)
                if(otherGhosts[i]["lane"] as Int == myObjectsPosition){
                    if(objectY > viewHeight - 2 - oHeight
                        && objectY < viewHeight - 2 )
                    {
                        gameTask.closeGame(score)
                    }
                }
                if(objectY > viewHeight + oHeight)
                {
                    otherGhosts.removeAt(i)
                    score++
                    speed = 1 + Math.abs(score/8)
                    if(score > highScore){
                        highScore = score
                    }
                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
        myPaint!!.color = Color.WHITE
        myPaint!!.textSize = 40f
        canvas.drawText("Score : $score",80f,80f,myPaint!!)
        canvas.drawText("Speed : $speed",80f,380f,myPaint!!)

        invalidate()


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event!!.action){
            MotionEvent.ACTION_DOWN ->{
                val x1 = event.x
                if(x1 < viewWidth / 2){
                    if(myObjectsPosition > 0){
                        myObjectsPosition--
                    }
                }
                if(x1 > viewWidth / 2){
                    if(myObjectsPosition < 2){
                        myObjectsPosition ++
                    }
                }
                invalidate()
            }
            MotionEvent.ACTION_UP->{

            }
        }

        return true
    }


}