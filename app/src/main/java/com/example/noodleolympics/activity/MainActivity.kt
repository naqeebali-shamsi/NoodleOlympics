package com.example.noodleolympics.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.airbnb.lottie.LottieAnimationView
import android.app.Dialog
import android.widget.ImageButton
import com.example.noodleolympics.LoadingScreen
import com.example.noodleolympics.R
import com.example.noodleolympics.ui.theme.NoodleOlympicsTheme
import com.example.noodleolympics.viewmodel.GPTViewModel
import com.example.noodleolympics.viewmodel.GPTViewModelFactory

/**
 * Represents the MainActivity that handles the app's main functionality and UI interactions.
 */
class MainActivity : ComponentActivity() {
    // Initialize ViewModel using viewModels extension function with GPTViewModelFactory
    private val viewModel: GPTViewModel by viewModels { GPTViewModelFactory(application) }
    private lateinit var progressBar: ProgressBar
    private lateinit var animationView: LottieAnimationView

    /**
     * Hides the soft keyboard.
     */
    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    /**
     * Called when the activity is created. Sets up the splash screen and initial content view.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000) // Simulate a 3-second delay (can be removed in production)
        installSplashScreen() // Install the splash screen
        setContent {
            NoodleOlympicsTheme {
                LoadingScreen() // Set the initial content view to the loading screen
            }
        }
        setContentView(R.layout.homepage) // Set the actual content view to the homepage layout
        findViewById<Button>(R.id.playButton)?.setOnClickListener {
            val topic = findViewById<EditText>(R.id.topicEditText).text.toString()
            startGame(topic) // Call the startGame function when the play button is clicked
        }
        findViewById<Button>(R.id.playButton)?.setOnClickListener {
            val topic = findViewById<EditText>(R.id.topicEditText).text.toString()
            startGame(topic) // Call the startGame function when the play button is clicked
        }

        // Find the "How to play" button and set the click listener
        findViewById<Button>(R.id.howToPlayButton)?.setOnClickListener {
            showInstructionsOverlay() // Show the overlay when the "How to play" button is clicked
        }
        progressBar = findViewById(R.id.progressBar)
        animationView = findViewById(R.id.animationView)
    }

    private fun showInstructionsOverlay() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.overlay_instructions)

        // Find the "X" button and set the click listener to close the overlay
        dialog.findViewById<ImageButton>(R.id.closeButton)?.setOnClickListener {
            dialog.dismiss() // Close the overlay when the "X" button is clicked
        }

        dialog.show()
    }

    /**
     * Starts the game with the provided topic.
     */
    private fun startGame(topic: String) {
        // Hide the keyboard
        val view = this.currentFocus
        if (view != null) {
            hideKeyboard(view)
        }

        showProgressBar() // Show the progress bar while fetching questions

        // Fetch questions from the ViewModel
        if (topic.isNotEmpty()) {
            viewModel.fetchQuestions(
                topic,
                onSuccess = { questions ->
                    runOnUiThread {
                        hideProgressBar() // Hide the progress bar after questions are fetched
                        val intent = Intent(this, GameActivity::class.java)
                        intent.putExtra("topic", topic)
                        intent.putStringArrayListExtra("questions", ArrayList(questions))
                        startActivity(intent) // Start the GameActivity with the fetched questions
                    }
                },
                onError = {
                    // Handle the error here
                    runOnUiThread {
                        hideProgressBar() // Hide the progress bar on error
                        Toast.makeText(this, "Failed to fetch questions. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            Toast.makeText(this, "Please enter a topic!", Toast.LENGTH_SHORT).show()
            hideProgressBar() // Hide the progress bar if the topic is empty
        }
    }

    /**
     * Shows the progress bar animation.
     */
    private fun showProgressBar() {
        animationView.visibility = View.VISIBLE
    }

    /**
     * Hides the progress bar animation.
     */
    private fun hideProgressBar() {
        animationView.visibility = View.GONE
    }
}