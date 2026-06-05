package com.volfor.ondori.app.ui

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

data class Message(val id: Long, val messageTextId: Int, val formatArgs: List<Any> = emptyList())

/**
 * Class responsible for managing Snackbar messages to show on the screen
 */
interface SnackbarManager {

    val messages: StateFlow<List<Message>>

    fun showMessage(@StringRes messageTextId: Int, formatArgs: List<Any>? = null)
    fun setMessageShown(messageId: Long)
}

class SnackbarManagerImpl @Inject constructor() : SnackbarManager {

    private val _messages: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    override val messages: StateFlow<List<Message>> get() = _messages.asStateFlow()

    override fun showMessage(@StringRes messageTextId: Int, formatArgs: List<Any>?) {
        _messages.update { currentMessages ->
            currentMessages + Message(
                id = UUID.randomUUID().mostSignificantBits,
                messageTextId = messageTextId,
                formatArgs = formatArgs ?: emptyList(),
            )
        }
    }

    override fun setMessageShown(messageId: Long) {
        _messages.update { currentMessages ->
            currentMessages.filterNot { it.id == messageId }
        }
    }
}