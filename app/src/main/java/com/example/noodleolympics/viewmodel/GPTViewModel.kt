package com.example.noodleolympics.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noodleolympics.activity.OpenAIRepository
import kotlinx.coroutines.launch

/**
 * ViewModel class for the Noodle Olympics app.
 *
 * @param application The application context.
 */
class GPTViewModel(private val application: Application) : ViewModel() {

    private val openAIRepository = OpenAIRepository(application)

    /**
     * Fetches questions from the OpenAI API based on the given category.
     *
     * @param category The category for which questions need to be fetched.
     * @param onSuccess The success callback function to be executed when questions are fetched successfully.
     *                  It will receive the list of fetched questions.
     * @param onError The error callback function to be executed when an error occurs during the API call.
     *                It will receive the exception representing the error.
     */
    fun fetchQuestions(category: String, onSuccess: (List<String>) -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            openAIRepository.fetchQuestionsFromOpenAI(
                category,
                successCallback = { questions ->
                    onSuccess(questions)
                },
                errorCallback = { exception ->
                    // Log the error and invoke the error callback
                    println("Error: ${exception.message}")
                    onError(exception)
                }
            )
        }
    }
}
