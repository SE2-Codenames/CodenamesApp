package com.example.codenamesapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.codenamesapp.MainMenu.RulesScreen
import org.junit.Rule
import org.junit.Test

class RulesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun defaultLanguageIsEnglish() {
        composeTestRule.setContent {
            RulesScreen(onBack = {})
        }

        composeTestRule.onNodeWithText("Game Rules").assertExists()
        composeTestRule.onNode(hasText("Setup", substring = true)).assertExists()

    }

    @Test
    fun switchToGermanAndCheckText() {
        composeTestRule.setContent {
            RulesScreen(onBack = {})
        }
        composeTestRule.onNodeWithText("DE").performClick()
        composeTestRule.onNodeWithTag("AufbauText").assertExists()

    }

    @Test
    fun switchBackToEnglish() {
        composeTestRule.setContent {
            RulesScreen(onBack = {})
        }

        composeTestRule.onNodeWithText("DE").performClick()
        composeTestRule.onNodeWithText("EN").performClick()

        composeTestRule.onNode(hasText("Setup", substring = true)).assertExists()

    }
}
