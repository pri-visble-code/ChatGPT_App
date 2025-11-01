package com.example.chatgptapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject

class ChatAdapter(private val chatHistory: JSONArray) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userMessage: TextView = itemView.findViewById(R.id.tvUserMessage)
        val botMessage: TextView = itemView.findViewById(R.id.tvBotMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = chatHistory.getJSONObject(position)
        when (message.getString("role")) {
            "user" -> {
                holder.userMessage.visibility = View.VISIBLE
                holder.botMessage.visibility = View.GONE

                val content = message.get("content")
                var displayText = ""
                if (content is JSONArray) {
                    // Handle the case where content is an array of text and image URL
                    var textPart = ""
                    var urlPart = ""
                    for (i in 0 until content.length()) {
                        val item = content.getJSONObject(i)
                        when (item.getString("type")) {
                            "text" -> textPart = item.getString("text")
                            "image_url" -> urlPart = item.getJSONObject("image_url").getString("url")
                        }
                    }
                    displayText = "$textPart\n$urlPart"
                } else {
                    // Handle the case where content is just a string
                    displayText = content.toString()
                }
                holder.userMessage.text = displayText.trim()
            }
            "assistant" -> {
                holder.botMessage.visibility = View.VISIBLE
                holder.userMessage.visibility = View.GONE
                holder.botMessage.text = message.getString("content")
            }
            else -> {
                holder.userMessage.visibility = View.GONE
                holder.botMessage.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return chatHistory.length()
    }
}
