package com.example.codenamesapp.gamelogic

import com.example.codenamesapp.model.GameState

class GameManager(
    private val wordProvider: () -> List<String>
) {
    private val words: List<String> by lazy { wordProvider() }

}
