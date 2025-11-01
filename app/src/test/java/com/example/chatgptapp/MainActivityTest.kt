package com.example.chatgptapp

import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.lang.reflect.Method

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    private lateinit var activity: MainActivity

    // Helper function to invoke the private getResponse method using reflection
    private fun invokeGetResponse(instance: MainActivity, param: String) {
        val method: Method = MainActivity::class.java.getDeclaredMethod("getResponse", String::class.java)
        method.isAccessible = true
        method.invoke(instance, param)
    }

    // Helper function to get the private conversationHistory field using reflection
    private fun getConversationHistory(instance: MainActivity): JSONArray {
        val field = MainActivity::class.java.getDeclaredField("conversationHistory")
        field.isAccessible = true
        return field.get(instance) as JSONArray
    }

    @Before
    fun setUp() {
        // Set up the activity before each test
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
    }

    @Test
    fun getResponse_withTextOnly_formatsRequestCorrectly() {
        val question = "Hello, how are you?"

        // Call the private getResponse method using our reflection helper
        invokeGetResponse(activity, question)

        val history = getConversationHistory(activity)
        // The history will contain the system message and our new message
        val lastMessage = history.getJSONObject(history.length() - 1)

        assertEquals("user", lastMessage.getString("role"))
        assertEquals(question, lastMessage.getString("content"))
    }

    @Test
    fun getResponse_withUrl_formatsRequestCorrectly() {
        val url = "https://example.com/image.png"
        val text = "What is in this image?"
        val question = "$text $url"

        invokeGetResponse(activity, question)

        val history = getConversationHistory(activity)
        val lastMessage = history.getJSONObject(history.length() - 1)
        val contentArray = lastMessage.getJSONArray("content")

        assertEquals("user", lastMessage.getString("role"))
        assertNotNull(contentArray)
        assertEquals(2, contentArray.length())

        val textObject = contentArray.getJSONObject(0)
        assertEquals("text", textObject.getString("type"))
        assertEquals(text, textObject.getString("text"))

        val imageObject = contentArray.getJSONObject(1)
        assertEquals("image_url", imageObject.getString("type"))
        assertEquals(url, imageObject.getJSONObject("image_url").getString("url"))
    }

    @Test
    fun newChatButton_clearsHistory() {
        val history = getConversationHistory(activity)
        history.put(JSONObject().put("role", "user").put("content", "test message"))
        assertTrue(history.length() > 1)

        activity.findViewById<android.widget.Button>(R.id.btnNewChat).performClick()
        val newHistory = getConversationHistory(activity)

        assertEquals(1, newHistory.length())
        assertEquals("system", newHistory.getJSONObject(0).getString("role"))
    }
}