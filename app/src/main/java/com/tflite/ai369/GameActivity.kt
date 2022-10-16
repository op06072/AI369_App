package com.tflite.ai369

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import kotlin.concurrent.timer

class GameActivity : AppCompatActivity() {

    var START_MILLI_SECONDS = 1000L

    var countdown_timer: CountDownTimer? = null
    var isRunning: Boolean = false
    var time_in_milli_seconds = START_MILLI_SECONDS
    private var turn = 0
    private var n = 1
    private var Core369 = core369(this)
    private var timerTask: Timer?=null
    private var aiRun = 1
    private var gameRunning = 1

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        val intent = intent
        START_MILLI_SECONDS *= intent.getStringExtra("timelimit")!!.toLong()
        time_in_milli_seconds = START_MILLI_SECONDS
        updateTextUI()
        val toolbar = findViewById(R.id.gametoolbar) as Toolbar
        setSupportActionBar(toolbar)
        val ab = supportActionBar!!
        ab.setDisplayShowTitleEnabled(false)
        ab.setDisplayHomeAsUpEnabled(true)
        TimeBar.max = START_MILLI_SECONDS.toInt()

        Core369
            .initialize()
            .addOnFailureListener { e -> Log.e(TAG, "Error to setting up 369 Game Core.", e) }

        gamestart.setOnClickListener {
            if (isRunning) {
                resetTimer()
            } else {
                startTimer(time_in_milli_seconds)
                if (n == 1) gameRun()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gameRun() {
        timerTask = timer(period = 1) {
            if (gameRunning == 1) {
                gameRunning = 0
                if (turn==0){
                    runOnUiThread {
                        buttonNum.text = n.toString()
                    }
                    buttonNum.setOnClickListener {
                        runOnUiThread { gameProcess(n.toString()) }
                        gameRunning = 1
                        aiRun = 1
                    }
                    buttonX.setOnClickListener {
                        runOnUiThread { gameProcess("X") }
                        gameRunning = 1
                        aiRun = 1
                    }
                    buttonXX.setOnClickListener {
                        runOnUiThread { gameProcess("XX") }
                        gameRunning = 1
                        aiRun = 1
                    }
                    buttonXXX.setOnClickListener {
                        runOnUiThread { gameProcess("XXX") }
                        gameRunning = 1
                        aiRun = 1
                    }
                } else if (aiRun==1){
                    aiRun=0
                    runOnUiThread {
                        buttonNum.text = n.toString()
                        gamestart.text = "AI의 턴"
                        gameCore()
                    }
                    gameRunning = 1
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gameProcess(str: String) {
        if (turn == 0) {
            if (tsn() == str) {
                countdown_timer?.cancel()
                time_in_milli_seconds = START_MILLI_SECONDS
                updateTextUI()
                TimeBar.progress = time_in_milli_seconds.toInt()
                makeText(GameText.text.toString() + " " + str + "\n")
                turn++
                n++
            } else {
                resetTimer()
                Core369.closeAll()
                intent = Intent(this@GameActivity, LoseActivity::class.java)
                START_MILLI_SECONDS /= 1000
                intent.putExtra("timelimit", START_MILLI_SECONDS.toString())
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GameActivity).toBundle())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, 1) {
            override fun onFinish() {
                if (turn == 0) {
                    resetTimer()
                    Core369.closeAll()
                    val intent = Intent(this@GameActivity, LoseActivity::class.java)
                    START_MILLI_SECONDS /= 1000
                    intent.putExtra("timelimit", START_MILLI_SECONDS.toString())
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GameActivity).toBundle())
                }
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                updateTextUI()
                TimeBar.progress = p0.toInt()
            }
        }
        countdown_timer?.start()

        isRunning = true
        gamestart.text = "중지"
    }

    @SuppressLint("SetTextI18n")
    private fun resetTimer() {
        gamestart.text = "시작"
        countdown_timer?.cancel()
        isRunning = false
        time_in_milli_seconds = START_MILLI_SECONDS
        TimeBar.progress = time_in_milli_seconds.toInt()
        GameText.text = "Human :"
        buttonNum.text = "1"
        turn = 0
        n = 1
        updateTextUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTextUI() {
        val seconds = time_in_milli_seconds / 1000
        val milliseconds = time_in_milli_seconds % 1000

        RestTime.text = "$seconds.$milliseconds"
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    private fun gameCore() {
        if ((turn != 0) && (Core369.isInitialized)){
            var core: core369
            if (turn <= 3) {
                Core369
                    .classifyAsync(n, turn)
                    ?.addOnSuccessListener { resultText ->
                        if ("AI"+turn.toString()+" : "+tsn() == resultText) {
                            makeText(GameText.text.toString() + resultText + "\n")
                            turn++
                            n++
                            aiRun = 1
                            gameRunning = 1
                        } else {
                            resetTimer()
                            Core369.closeAll()
                            val intent = Intent(this@GameActivity, WinActivity::class.java)
                            START_MILLI_SECONDS /= 1000
                            intent.putExtra("timelimit", START_MILLI_SECONDS.toString())
                            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GameActivity).toBundle())
                        }
                    }
                    ?.addOnFailureListener { e ->
                        GameText?.text = getString(
                            R.string.tfe_dc_classification_error_message,
                            e.localizedMessage
                        )
                        Log.e(TAG, "Error classifying compute.", e)
                        resetTimer()
                        Core369.closeAll()
                        val intent = Intent(this@GameActivity, WinActivity::class.java)
                        START_MILLI_SECONDS /= 1000
                        intent.putExtra("timelimit", START_MILLI_SECONDS.toString())
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GameActivity).toBundle())
                    }
            }

            if (turn > 3) {
                turn = 0
                countdown_timer?.start()
                gamestart.text = "중지"
            }
        }
    }

    private fun tsn(): String {
        val Turn = n.toString()
        var many = 0
        for (i in Turn) {
            when (i.toInt()-48) {
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

    private fun makeText(str: String) {
        val gamestr = str.split("\n")
        var gameStr = ""
        if (gamestr.size >= 14){
            gameStr = gamestr.slice(1..13).joinToString("\n")
        } else {
            gameStr = gamestr.joinToString("\n")
        }
        if (turn == 3) {
            gameStr += "Human :"
            GameText.text = gameStr
        } else {
            GameText.text = gameStr
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                if (countdown_timer != null) {
                    resetTimer()
                }
                Core369.closeAll()
                val intent = Intent(this@GameActivity, GameLevelActivity::class.java)
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GameActivity).toBundle())
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBackPressed() {
        resetTimer()
        val intent = Intent(this@GameActivity, GameLevelActivity::class.java)
        Core369.closeAll()
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this@GameActivity).toBundle())
    }

    companion object {
        const val TAG = "GameActivity"
    }
}