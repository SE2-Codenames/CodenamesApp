package com.example.codenamesapp.gamelogic

import android.content.Context
import com.example.codenamesapp.R
import com.example.codenamesapp.model.*

class GameManager(private val context: Context) {

    lateinit var gameState: GameState
        private set

    fun startNewGame() {
        val allWords = loadWords()
        val selectedWords = allWords.shuffled().take(25)
        val roles = generateRoles()

        val board = selectedWords.mapIndexed { index, word ->
            Card(word = word, role = roles[index])
        }

        gameState = GameState(board = board)
    }

    private fun loadWords(): List<String> {
        val inputStream = context.resources.openRawResource(R.raw.words)
        return inputStream.bufferedReader().readLines().filter { it.isNotBlank() }
    }

    private fun generateRoles(): List<Role> {
        return mutableListOf<Role>().apply {
            addAll(List(9) { Role.RED })
            addAll(List(8) { Role.BLUE })
            addAll(List(7) { Role.NEUTRAL })
            add(Role.ASSASSIN)
            shuffle()
        }
    }






}