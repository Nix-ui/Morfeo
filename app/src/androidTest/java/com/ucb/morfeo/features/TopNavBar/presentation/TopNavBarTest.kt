package com.ucb.morfeo.features.TopNavBar.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class TopNavBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun topNavBar_displaysTitleAndScreenName() {
        val screenName = "Dashboard"
        
        composeTestRule.setContent {
            TopNavBar(
                isBackEnable = false,
                currentScreenName = screenName
            )
        }

        // Verifica que el título de la app esté presente
        composeTestRule.onNodeWithText("Morfeo").assertIsDisplayed()
        
        // Verifica que el nombre de la pantalla pasada por parámetro esté presente
        composeTestRule.onNodeWithText(screenName).assertIsDisplayed()
    }

    @Test
    fun topNavBar_backButtonShows_whenEnabled() {
        composeTestRule.setContent {
            TopNavBar(
                isBackEnable = true,
                currentScreenName = "Test"
            )
        }

        // Verifica que el botón de atrás con su descripción de contenido esté presente
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun topNavBar_backButtonHidden_whenDisabled() {
        composeTestRule.setContent {
            TopNavBar(
                isBackEnable = false,
                currentScreenName = "Test"
            )
        }

        // Verifica que el botón de atrás NO exista
        composeTestRule.onNodeWithContentDescription("Back").assertDoesNotExist()
    }

    @Test
    fun topNavBar_callsBackCallback_onButtonClick() {
        var backClicked = false
        
        composeTestRule.setContent {
            TopNavBar(
                isBackEnable = true,
                currentScreenName = "Test",
                onBackScreen = { backClicked = true }
            )
        }

        // Simula clic en el botón de atrás
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verifica que se haya llamado al callback
        assert(backClicked)
    }
}