package com.tflite.ai369

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_bench.*
import java.util.*
import kotlin.concurrent.timer

class BenchActivity: AppCompatActivity() {
    private var isRunning: Boolean = false
    private var turn = 1
    private var timerTask: Timer?=null
    private var Core369 = core369(this)
    private var aiRun = 1
    private var run1 = 1
    private var run2 = 1
    private var run3 = 1
    private var run = arrayOf(1, 1, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bench)
        val toolbar = findViewById(R.id.benchtoolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)

        Core369
            .initialize()
            .addOnFailureListener { e -> Log.e(BenchActivity.TAG, "Error to setting up 369 Game Core.", e) }

        benchstart.setOnClickListener {
            if (isRunning) {
                run = arrayOf(run1, run2, run3)
                aiRun = 0
                pause()
                benchprogress.visibility = View.INVISIBLE
            } else {
                isRunning = true
                aiRun = 1
                run1 = run[0]
                run2 = run[1]
                run3 = run[2]
                startTimer()
                benchstart.text = "일시정지"
                benchprogress.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startTimer() {
        timerTask = timer(period = 1) {
            if (aiRun == 1) {
                aiRun = 0
                if (run1 + run2 + run3 == 0) {
                    timerTask?.cancel()
                    benchprogress.visibility = View.INVISIBLE
                }
                if (Core369.isInitialized) {
                    Core369
                        .benchAll(turn)
                        .addOnSuccessListener { resultArray ->
                            if ("AI1 : "+tsn() != resultArray[0]) run1 = 0
                            if ("AI2 : "+tsn() != resultArray[1]) run2 = 0
                            if ("AI3 : "+tsn() != resultArray[2]) run3 = 0
                            aiRun = 1
                            turn++
                        }
                }
                runOnUiThread {
                    if (run1 != 0) bench1.text = "AI1 : " + turn.toString()
                    if (run2 != 0) bench2.text = "AI2 : " + turn.toString()
                    if (run3 != 0) bench3.text = "AI3 : " + turn.toString()
                    if (run1 + run2 + run3 == 0) benchstart.text = "벤치마킹 시작"
                }
            }
        }
    }

    private fun pause(){
        benchstart.text = "계속"
        timerTask?.cancel()
        isRunning = false
    }

    private fun tsn(): String {
        val Turn = turn.toString()
        var many = 0
        for (i in Turn) {
            when (i.toInt() - 48) {
                3, 6, 9 -> many += 1
            }
        }
        var result = ""
        when (many) {
            0 -> result = Turn
            1 -> result = "X"
            2 -> result = "XX"
            3 -> result = "XXX"
        }
        return result
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                pause()
                Core369.closeAll()
                val intent = Intent(this@BenchActivity, MainActivity::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@BenchActivity).toBundle())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        pause()
        val intent = Intent(this@BenchActivity, MainActivity::class.java)
        Core369.closeAll()
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@BenchActivity).toBundle())
    }

    companion object {
        private const val TAG = "BenchActivity"
    }
}