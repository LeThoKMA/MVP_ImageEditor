package com.example.imageEditor.chat.model

import com.example.imageEditor.chat.model.Message
import com.example.imageEditor.chat.model.User

data class UserMessageModel(
    var user: User,
    var message: Message
)
