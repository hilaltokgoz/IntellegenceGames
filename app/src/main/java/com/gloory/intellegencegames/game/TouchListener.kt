package com.gloory.intellegencegames.game

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.gloory.intellegencegames.view.PuzzleDetailFragment

// Code with ❤️
//┌──────────────────────────┐
//│ Created by Hilal TOKGOZ  │
//│ ──────────────────────── │
//│ hilaltokgoz98@gmail.com  │
//│ ──────────────────────── │
//│ 21.05.2024               │
//└──────────────────────────┘

class TouchListener(private val fragment: PuzzleDetailFragment) : View.OnTouchListener {

    private var xDelta = 0f
    private var yDelta = 0f

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        val x = motionEvent!!.rawX
        val y = motionEvent.rawY
        val tolerance = Math.sqrt(
            Math.pow(view!!.width.toDouble(), 2.0) +
                    Math.pow(view.height.toDouble(), 2.0) ) / 10
       val piece = view as PuzzlePiece
        if (!piece.canMove){
            return true
        }
        val lParams = view.layoutParams as RelativeLayout.LayoutParams
        when(motionEvent.action and MotionEvent.ACTION_MASK){
         MotionEvent.ACTION_DOWN ->{
             xDelta = x - lParams.leftMargin
             yDelta = y - lParams.topMargin
             piece.bringToFront()
         }
            MotionEvent.ACTION_MOVE ->{
                lParams.leftMargin = (x - xDelta).toInt()
                lParams.topMargin = (y-yDelta).toInt()    //videoda leftMargin
                view.layoutParams = lParams
            }
            MotionEvent.ACTION_UP -> {
                val xDiff = StrictMath.abs(
                    piece.xCoord - lParams.leftMargin
                )
                val yDiff = StrictMath.abs(
                    piece.yCoord - lParams.topMargin   //videoda leftMargin
                )
                if (xDiff <= tolerance && yDiff <=tolerance){
                    lParams.leftMargin = piece.xCoord
                    lParams.topMargin = piece.yCoord
                    piece.layoutParams = lParams
                    piece.canMove = false
                    sendViewToBack(piece)
                    fragment.checkGameOver()
                }
            }

        }

        return true
    }

    private fun sendViewToBack(child: View) {
        val parent = child.parent as ViewGroup

        if (parent != null){
            parent.removeView(child)
            parent.addView(child,0)
        }

    }

}