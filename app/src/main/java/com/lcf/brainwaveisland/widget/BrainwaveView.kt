package com.lcf.brainwaveisland.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.lcf.brainwaveisland.util.dp
import kotlin.math.*

class BrainwaveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val banWidth = 350.dp(context).toInt()
    val banHeight = 100.dp(context).toInt()
    var banX = Int.MIN_VALUE

    var ballRadius = 50.dp(context)

    val rectPaint = Paint()

    var a = 100f
    var v = 1200f

    var angleRadians = 210.0

    companion object {
        val KnockMinDis = 15f
    }

    var ballX = 0f
    var ballY = 0f

    var lastDrawMillis = 0L

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        banX = width / 2 - banWidth / 2
        ballX = width / 2f
        ballY = height - banHeight - ballRadius
    }

    var isInRange = false

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            if (event.x < banX) return true
            if (event.x > banX + banWidth) return true
            if (event.y < bottom - banHeight) return true
            if (event.y > bottom) return true
            isInRange = true
            return true
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            banX = (event.x - banWidth / 2).toInt()
            postInvalidate()
            return true
        } else if (event.action == MotionEvent.ACTION_UP) {
            isInRange = false
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (banX == Int.MIN_VALUE) banX = width / 2 - banWidth / 2

        rectPaint.setColor(Color.parseColor("#5020d5e1"))
        canvas.drawRoundRect(
            banX.toFloat(),
            (height - banHeight).toFloat(),
            (banX + banWidth).toFloat(),
            height.toFloat(),
            banHeight / 2f,
            banHeight / 2f,
            rectPaint
        )
        rectPaint.setColor(Color.parseColor("#50345599"))
        canvas.drawCircle(ballX, ballY, ballRadius, rectPaint)

        if (checkDeath()) return

        postInvalidate()

        val currentMillis = System.currentTimeMillis()
        if (lastDrawMillis == 0L) lastDrawMillis = currentMillis

        val t = (currentMillis - lastDrawMillis) / 1000f
        lastDrawMillis = currentMillis


        val s = v * t + 0.5f * a * t * t
        if (v < 5000f) {
            v += a * t
        }
        ballX += (s * cos(Math.toRadians(angleRadians))).toFloat()
        ballY += (s * sin(Math.toRadians(angleRadians))).toFloat()

        resolveKnock()
    }

    private fun checkDeath(): Boolean {
        return height - (ballY + ballRadius) < KnockMinDis
    }

    fun resolveKnock() {
        val startDis = ballX - ballRadius
        val endDis = width - (ballX + ballRadius)
        val topDis = ballY - ballRadius


        if (topDis <= KnockMinDis) {
            if (angleRadians >= 180) {
                angleRadians = 360 - angleRadians
                angleRadians %= 360
            }
        }

        if (startDis <= KnockMinDis) {
            if (angleRadians in 90.0..270.0) {
                angleRadians = 540 - angleRadians
                angleRadians %= 360
            }
        }
        if (endDis <= KnockMinDis) {
            if (angleRadians in 0.0..90.0 || angleRadians in 270.0..360.0) {
                angleRadians = 540 - angleRadians
                angleRadians %= 360
            }
        }

        //距离滑块近
        val ballOffsetBan = ballX - banX
        if (ballOffsetBan >= 0.5 * banHeight && ballOffsetBan <= banWidth - 0.5f * banHeight) {
            if ((height - banHeight - ballY - ballRadius) <= KnockMinDis) {
                if (angleRadians <= 180) {
                    angleRadians = 360 - angleRadians
                    angleRadians %= 360
                }
            }
        } else if (ballOffsetBan >= -ballRadius && ballOffsetBan <= 0.5 * banHeight) {
            val leftBanCircleY = height - banHeight / 2f
            val leftBanCircleX = banX + banHeight * 0.5f
            if ((leftBanCircleY - ballY).toDouble()
                    .pow(2.0) + (leftBanCircleX - ballX).toDouble()
                    .pow(2.0) < ((banHeight / 2f + ballRadius + KnockMinDis).toDouble()
                    .pow(2.0))
            ) {
                val radiansBall2Bar =
                    Math.toDegrees(atan(((abs(leftBanCircleY - ballY)) / abs(leftBanCircleX - ballX)).toDouble()))
                if (angleRadians >= radiansBall2Bar) {
                    angleRadians = radiansBall2Bar + 180 - abs(angleRadians - radiansBall2Bar)
                } else {
                    angleRadians = radiansBall2Bar + 180 + abs(angleRadians - radiansBall2Bar)
                }
            }
        } else if (ballOffsetBan >= banWidth - banHeight / 2f && ballOffsetBan <= banWidth + ballRadius) {
            if (cos(angleRadians) <= 0) {
                val rightBanCircleY = height - banHeight / 2f
                val rightBanCircleX = banX + banWidth - banHeight / 2f
            }
        }
    }

}