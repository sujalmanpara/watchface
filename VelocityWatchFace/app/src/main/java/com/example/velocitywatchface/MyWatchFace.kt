package com.example.velocitywatchface

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.view.SurfaceHolder
import java.util.Calendar
import java.util.TimeZone

class MyWatchFace : CanvasWatchFaceService() {

    override fun onCreateEngine(): Engine {
        return Engine()
    }

    inner class Engine : CanvasWatchFaceService.Engine() {

        private lateinit var calendar: Calendar
        
        // Raw bitmaps
        private lateinit var rawBg: Bitmap
        private lateinit var rawHour: Bitmap
        private lateinit var rawMinute: Bitmap
        private lateinit var rawSecond: Bitmap

        // Scaled bitmaps
        private lateinit var bgBitmap: Bitmap
        private lateinit var hourBitmap: Bitmap
        private lateinit var minuteBitmap: Bitmap
        private lateinit var secondBitmap: Bitmap

        private var isInitialized = false

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)
            setWatchFaceStyle(WatchFaceStyle.Builder(this@MyWatchFace)
                .setAcceptsTapEvents(true)
                .build())

            calendar = Calendar.getInstance()
            
            rawBg = BitmapFactory.decodeResource(resources, R.drawable.bg_dial)
            rawHour = BitmapFactory.decodeResource(resources, R.drawable.hand_hour)
            rawMinute = BitmapFactory.decodeResource(resources, R.drawable.hand_minute)
            rawSecond = BitmapFactory.decodeResource(resources, R.drawable.hand_second)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            
            // Scale assets once
            bgBitmap = Bitmap.createScaledBitmap(rawBg, width, height, true)
            hourBitmap = Bitmap.createScaledBitmap(rawHour, width, height, true)
            minuteBitmap = Bitmap.createScaledBitmap(rawMinute, width, height, true)
            secondBitmap = Bitmap.createScaledBitmap(rawSecond, width, height, true)
            
            isInitialized = true
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            if (!isInitialized) return

            calendar.timeZone = TimeZone.getDefault()
            calendar.timeInMillis = System.currentTimeMillis()

            val centerX = bounds.width() / 2f
            val centerY = bounds.height() / 2f

            // Draw Background
            canvas.drawBitmap(bgBitmap, 0f, 0f, null)

            // Calculate rotation
            val seconds = calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f
            val minutes = calendar.get(Calendar.MINUTE) + seconds / 60f
            val hours = calendar.get(Calendar.HOUR) + minutes / 60f
            
            val secRot = seconds * 6f
            val minRot = minutes * 6f
            val hourRot = hours * 30f

            // Draw Hour Hand
            canvas.save()
            canvas.rotate(hourRot, centerX, centerY)
            canvas.drawBitmap(hourBitmap, 0f, 0f, null)
            canvas.restore()

            // Draw Minute Hand
            canvas.save()
            canvas.rotate(minRot, centerX, centerY)
            canvas.drawBitmap(minuteBitmap, 0f, 0f, null)
            canvas.restore()

            // Draw Second Hand (only if visible in ambient mode logic, simplified here)
            if (!isInAmbientMode) {
                canvas.save()
                canvas.rotate(secRot, centerX, centerY)
                canvas.drawBitmap(secondBitmap, 0f, 0f, null)
                canvas.restore()
                
                // Continuous animation
                invalidate()
            }
        }
    }
}