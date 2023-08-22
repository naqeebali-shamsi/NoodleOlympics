package com.example.noodleolympics.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Factory class for creating instances of [GPTViewModel].
 *
 * @param application The application context.
 */
class GPTViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the [GPTViewModel] class.
     *
     * @param modelClass The class of the ViewModel to create.
     * @return A new instance of [GPTViewModel].
     * @throws IllegalArgumentException If the ViewModel class is unknown.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        (GPTViewModel(application) as T)
}