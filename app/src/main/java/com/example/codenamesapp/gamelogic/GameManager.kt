package com.example.codenamesapp.gamelogic


class GameManager(
    private val wordProvider: () -> List<String>
) {
    private val words: List<String> by lazy { wordProvider() }

    fun getRandomWords(): List<String> {
        return words.shuffled()
    }

}
