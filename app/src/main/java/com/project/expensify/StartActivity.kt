package com.project.expensify

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        //set video path
        val videoPath="android.resource://$packageName/raw/splash"
        videoAccess.setVideoPath(videoPath)
        videoAccess.setOnCompletionListener {
            val r= Runnable {
                startActivity(Intent(this@StartActivity,SignInActivity::class.java))
                finish()
            }
            Handler().postDelayed(r,5)
        }

        videoAccess.start()//play
    }
    }
