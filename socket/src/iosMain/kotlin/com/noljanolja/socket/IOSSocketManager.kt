package com.noljanolja.socket

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IOSSocketManager(
    val socketManager: SocketManager,
) : CallbackSocketManager() {
    private val scope = MainScope()
    private val _streamData = MutableStateFlow<String>("")
    val streamData = _streamData.asStateFlow().asCallbacks()

    fun startStreams(token: String) {
        scope.launch {
            socketManager.streamConversations(token).collect {
                _streamData.emit(it)
            }
        }
    }
}