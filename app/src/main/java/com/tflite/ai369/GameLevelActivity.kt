package com.tflite.ai369

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.Window
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_gamelevel.*

class GameLevelActivity : AppCompatActivity() {
    private var Core369 = core369(this)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            with(window) {
                requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
                // set an slide transition
                enterTransition = Slide(Gravity.END)
                exitTransition = Slide(Gravity.START)
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamelevel)

        easybutton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("timelimit", "7")
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        normalbutton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("timelimit", "5")
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }

        hardbutton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("timelimit", "3")
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        val intent = Intent(this@GameLevelActivity, MainActivity::class.java)
        Core369.closeAll()
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GameLevelActivity).toBundle())
    }
}