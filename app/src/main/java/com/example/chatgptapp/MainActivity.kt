package com.example.chatgptapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    private lateinit var etQuestion: TextInputEditText
    private lateinit var btnNewChat: Button
    private lateinit var rvChat: RecyclerView
    private lateinit var chatAdapter: ChatAdapter

    private val conversationHistory = JSONArray()
    private val systemMessage = JSONObject().apply {
        put("role", "system")
        put(
            "content",
            "You are SIA, a friendly and helpful AI assistant created by Prince. " +
                    "Your personality: polite, smart, supportive, and human-like. " +
                    "Your rules: " +
                    "1) Answer clearly and briefly (2–4 sentences). " +
                    "2) Speak in a natural human tone. " +
                    "3) Help in every field with simple explanations. " +
                    "4) If someone asks who made you, say Prince created you."
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etQuestion = findViewById(R.id.etQuestion)
        btnNewChat = findViewById(R.id.btnNewChat)
        rvChat = findViewById(R.id.rvChat)

        chatAdapter = ChatAdapter(conversationHistory)
        rvChat.adapter = chatAdapter
        rvChat.layoutManager = LinearLayoutManager(this)

        btnNewChat.setOnClickListener {
            while (conversationHistory.length() > 0) {
                conversationHistory.remove(0)
            }
            conversationHistory.put(systemMessage)
            chatAdapter.notifyDataSetChanged()
        }

        conversationHistory.put(systemMessage)

        etQuestion.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val question = etQuestion.text.toString().trim()
                if (question.isNotEmpty()) {
                    getResponse(question)
                }
                return@setOnEditorActionListener true
            }
            false
        }

        etQuestion.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                val drawableEnd = etQuestion.compoundDrawables[2] // 0:left, 1:top, 2:right, 3:bottom
                if (drawableEnd != null && motionEvent.rawX >= (etQuestion.right - drawableEnd.bounds.width() - etQuestion.paddingRight - etQuestion.paddingEnd)) {
                    val question = etQuestion.text.toString().trim()
                    if (question.isNotEmpty()) {
                        getResponse(question)
                    }
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun getResponse(question: String) {
        // Regex to find URLs in the text
        val urlPattern = Pattern.compile(
            "(https?://(www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&/=]*))"
        )
        val matcher = urlPattern.matcher(question)
        var imageUrl: String? = null
        var textQuestion = question

        if (matcher.find()) {
            imageUrl = matcher.group(0)
            textQuestion = question.replace(imageUrl!!, "").trim()
        }

        val userMessage = JSONObject()
        userMessage.put("role", "user")

        val contentArray = JSONArray()
        val textObject = JSONObject().apply {
            put("type", "text")
            put("text", textQuestion)
        }
        contentArray.put(textObject)

        if (imageUrl != null) {
            val imageObject = JSONObject().apply {
                put("type", "image_url")
                put("image_url", JSONObject().put("url", imageUrl))
            }
            contentArray.put(imageObject)
            userMessage.put("content", contentArray)
        } else {
            userMessage.put("content", textQuestion)
        }

        conversationHistory.put(userMessage)
        chatAdapter.notifyItemInserted(conversationHistory.length() - 1)
        rvChat.scrollToPosition(conversationHistory.length() - 1)
        etQuestion.setText("")

        // you can on on groq site and get free api key and place below. link : https://console.groq.com/keys
        val apiKey = "YOUR_GROQ_API_KEY"
        val url = "https://api.groq.com/openai/v1/chat/completions"

        val requestBody = JSONObject().apply {
            put("model", "meta-llama/llama-4-scout-17b-16e-instruct")
            put("messages", conversationHistory)
            put("max_tokens", 400)
            put("temperature", 0.7)
        }.toString()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.v("data", body ?: "empty")

                try {
                    val jsonObject = JSONObject(body)
                    val jsonArray = jsonObject.getJSONArray("choices")
                    val textResult = jsonArray.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")

                    val botMessage = JSONObject()
                    botMessage.put("role", "assistant")
                    botMessage.put("content", textResult)
                    conversationHistory.put(botMessage)

                    runOnUiThread {
                        chatAdapter.notifyItemInserted(conversationHistory.length() - 1)
                        rvChat.scrollToPosition(conversationHistory.length() - 1)
                    }
                } catch (e: Exception) {
                    Log.e("error", "Error parsing response", e)
                }
            }
        })
    }
}
