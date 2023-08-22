package com.example.noodleolympics.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.noodleolympics.R
import kotlin.system.exitProcess

/**
 * Represents the EndGameActivity that displays the end of the game screen.
 */
class EndGameActivity : AppCompatActivity() {

    private lateinit var restartGameButton: Button
    private lateinit var exitGameButton: Button
    private lateinit var correctAnswersTextView: TextView
    private lateinit var builder: AlertDialog.Builder

    /**
     * Called when the activity is created. Initializes the UI components, sets click listeners for buttons,
     * and displays the number of correct answers.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_endgame)

        restartGameButton = findViewById(R.id.new_game_button)
        exitGameButton = findViewById(R.id.exit_button)
        correctAnswersTextView = findViewById(R.id.guess_count_textview)

        // Get the number of correct answers from the previous activity
        val numberOfCorrectAnswers = getNumberOfCorrectAnswers()

        // Update the TextView text to display the number of correct answers
        correctAnswersTextView.text = "Great! You got $numberOfCorrectAnswers words correct"

        // Restart Game: Set click listener for the restart game button
        restartGameButton.setOnClickListener{
            // Start the MainActivity to restart the game
            val mainActivityIntent = Intent(this, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        // Exit the game Implementation: Set click listener for the exit game button
        builder = AlertDialog.Builder(this)
        exitGameButton.setOnClickListener{
            // Finish the current activity and all activities immediately below it in the stack
            finishAffinity()

            // Terminate the JVM process to forcefully exit the app
            exitProcess(0)
        }
    }

    /**
     * Get the number of correct answers from the intent extras.
     * @return The number of correct answers as an integer. If not found, returns 0.
     */
    private fun getNumberOfCorrectAnswers(): Int {
        return intent.getIntExtra("score", 0)
    }

}
