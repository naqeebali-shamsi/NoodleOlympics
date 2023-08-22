package com.example.noodleolympics.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.noodleolympics.R
import com.example.noodleolympics.constants.IntegerConstants.Companion.oneMinInMilliseconds
import com.example.noodleolympics.enums.GameState
import com.example.noodleolympics.ui.theme.correctGuessColor
import com.example.noodleolympics.ui.theme.passWordColor
import com.example.noodleolympics.ui.theme.regularGameScreen
import com.example.noodleolympics.util.TimeUtil

/**
 * Represents the GameActivity that displays the gameplay screen for the word guessing game.
 */
class GameActivity : AppCompatActivity(), SensorEventListener {

    private var soundMediaplayer: MediaPlayer? = null
    private var currentQuestionIndex = 0
    private var leftTime = oneMinInMilliseconds
    private var isCounting = true

    private var questionsToDisplay = mutableListOf<String>()
    private var shouldMoveToNextQuestion = false
    private var questionTextView: TextView? = null

    private var timerTextView: TextView? = null
    private var questionLayout: RelativeLayout? = null

    private lateinit var sensorManager: SensorManager
    private lateinit var correctGuesses: HashSet<String>

    private var countDownTimer: CountDownTimer? = object : CountDownTimer(leftTime, 100) {
        override fun onFinish() {
            countdownTimerFinish()
        }

        override fun onTick(millisUntilFinished: Long) {
            updateView(millisUntilFinished)
        }
    }.start()

    /**
     * Updates the timer TextView to display the remaining time in minutes and seconds.
     */
    private fun updateView(millisUntilFinished: Long) {
        leftTime = millisUntilFinished
        val seconds = TimeUtil.convertMillisecondsToSeconds(milliseconds = millisUntilFinished)
        timerTextView?.text = String.format("%02d:%02d", seconds, millisUntilFinished)
    }

    /**
     * Called when the activity is created. Initializes UI components and retrieves questions for gameplay.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        // Initialize UI components
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        questionTextView = findViewById(R.id.question_textview)
        timerTextView = findViewById(R.id.timer_textview)
        questionLayout = findViewById(R.id.question_relative_layout)

        // Get questions from the intent extras
        val questionsList = intent.getStringArrayListExtra("questions")
        if (questionsList != null) {
            questionsToDisplay.addAll(questionsList)
        }

        // Initialize HashSet to store correct guesses
        correctGuesses = HashSet()
        soundMediaplayer = MediaPlayer.create(this, R.raw.tip)
    }

    /**
     * Called when the activity is starting. Sets the first question, and configures the magnetic field sensor.
     */
    override fun onStart() {
        super.onStart()
        questionTextView?.text = questionsToDisplay[currentQuestionIndex]
        configureSensor()
    }

    /**
     * Called when the activity is about to be destroyed. Unregisters the sensor listener and releases the media player.
     */
    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        if (soundMediaplayer?.isPlaying == true) {
            soundMediaplayer?.stop()
        }
        soundMediaplayer?.release()
    }

    /**
     * Configures the magnetic field sensor to listen for changes in the magnetic field strength.
     */
    private fun configureSensor() {
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magnetometer ->
            sensorManager.registerListener(
                this,
                magnetometer,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    /**
     * Called when the sensor detects changes in the magnetic field.
     * Determines the game state based on the magnetic field strength on the X and Z axes.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
            val magneticFieldStrengthOnXAxis = event.values[0]
            val magneticFieldStrengthOnZAxis = event.values[2]

            when (getGameState(magneticFieldStrengthOnXAxis, magneticFieldStrengthOnZAxis)) {
                GameState.CORRECT -> {
                    questionLayout?.setBackgroundColor(correctGuessColor)
                    pauseCountdownTimer()
                    shouldMoveToNextQuestion = true
                    correctGuesses.add(questionsToDisplay[currentQuestionIndex])
                }
                GameState.PASS -> {
                    questionLayout?.setBackgroundColor(passWordColor)
                    pauseCountdownTimer()
                    shouldMoveToNextQuestion = true
                }
                else -> { // default playing state
                    resumeCountdownTimer()
                    if (shouldMoveToNextQuestion) {
                        shouldMoveToNextQuestion = false
                        questionLayout?.setBackgroundColor(regularGameScreen)
                        if (currentQuestionIndex + 1 < questionsToDisplay.size) {
                            questionTextView?.text = questionsToDisplay[++currentQuestionIndex]
                        }
                    }
                }
            }
        }
    }

    /**
     * Called when the sensor accuracy changes. Not used in this implementation.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    /**
     * Resumes the countdown timer when the game is in the playing state.
     */
    private fun resumeCountdownTimer() {
        if (!isCounting) {
            isCounting = true
            countDownTimer = object : CountDownTimer(leftTime, 100) {
                override fun onFinish() {
                    countdownTimerFinish()
                }

                override fun onTick(millisUntilFinished: Long) {
                    updateView(millisUntilFinished)
                }
            }.start()
        }
    }

    /**
     * Pauses the countdown timer when the game is in the correct or pass state.
     */
    private fun pauseCountdownTimer() {
        if (isCounting) {
            isCounting = false
            countDownTimer?.cancel()
            countDownTimer = null
        }
    }

    /**
     * Called when the countdown timer finishes. Plays a sound and hides the question text view.
     */
    private fun countdownTimerFinish() {
        soundMediaplayer?.setOnCompletionListener {
            finishGameActivity()
        }

        soundMediaplayer?.start()
        isCounting = false
        countDownTimer = null
        questionTextView?.visibility = View.GONE
    }

    /**
     * Finishes the GameActivity and starts the EndGameActivity to display the game results.
     */
    private fun finishGameActivity() {
        val endGameIntent = Intent(this, EndGameActivity::class.java)
        endGameIntent.putExtra("score", correctGuesses.size)
        startActivity(endGameIntent)
        finish() // Add this line to destroy GameActivity
    }

    /**
     * Determines the game state based on the magnetic field strength on the X and Z axes.
     */
    private fun getGameState(magneticFieldStrengthOnXAxis: Float, magneticFieldStrengthOnZAxis: Float): GameState {
        // Magnetic field strength units are in microTesla
        return if (magneticFieldStrengthOnXAxis >= -20 && magneticFieldStrengthOnZAxis >= 35) {
            GameState.CORRECT
        } else if (magneticFieldStrengthOnXAxis >= -40 && magneticFieldStrengthOnZAxis <= -10) {
            GameState.PASS
        } else {
            GameState.GUESS
        }
    }
}
