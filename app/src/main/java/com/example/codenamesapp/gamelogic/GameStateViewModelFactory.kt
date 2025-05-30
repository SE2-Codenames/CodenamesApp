package com.example.codenamesapp.gamelogic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class GameStateViewModelFactory(
    private val gameManager: GameManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameStateViewModel::class.java)) {
            return GameStateViewModel(gameManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
