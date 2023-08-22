package com.example.noodleolympics.activity

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Repository class for fetching questions from OpenAI service.
 *
 * @param context The application context used for making network requests.
 */
class OpenAIRepository(private val context: Context) {

    /**
     * Fetches questions from the OpenAI service for the specified category.
     *
     * @param category The category of questions to fetch.
     * @param retryCount The number of times to retry the request in case of failure (default is 3).
     * @param successCallback Callback function called on successful response, providing the list of questions.
     * @param errorCallback Callback function called on error, providing the exception that occurred during the request.
     */
    fun fetchQuestionsFromOpenAI(
        category: String,
        retryCount: Int = 3,
        successCallback: (List<String>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val lambdaUrl = "https://2fcepaljfmk5hkkt7qnzdiqopi0ceebu.lambda-url.us-east-1.on.aws/"
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        val request = Request.Builder()
            .url("$lambdaUrl?category=$category")
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (retryCount > 0) {
                    // Retry the request after a delay
                    Handler(Looper.getMainLooper()).postDelayed({
                        fetchQuestionsFromOpenAI(category, retryCount - 1, successCallback, errorCallback)
                    }, 1000)
                } else {
                    errorCallback(e)
                }
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    errorCallback(IOException("Unexpected code ${response.code}"))
                } else {
                    response.body?.let { responseBody ->
                        val responseString = responseBody.string()
                        Log.d("Response", responseString)
                        val json = JSONObject(responseString)
                        val wordsArray = json.getJSONArray("words")
                        val questions = ArrayList<String>()
                        for (i in 0 until wordsArray.length()) {
                            questions.add(wordsArray.getString(i))
                        }
                        successCallback(questions)
                    }
                }
            }
        })
    }
}
