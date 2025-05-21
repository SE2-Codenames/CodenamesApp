package com.example.codenamesapp

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameRulesTests {

    @Test
    fun playButtonCall() {
        var wasClicked = false

        val onPlayClicked = { wasClicked = true }
        onPlayClicked()

        assertTrue(wasClicked, "Play button callback should be triggered.")
    }

    @Test
    fun rulesButtonCall() {
        var wasClicked = false

        val onRulesClicked = { wasClicked = true }
        onRulesClicked()

        assertTrue(wasClicked, "Rules button callback should be triggered.")
    }

}
