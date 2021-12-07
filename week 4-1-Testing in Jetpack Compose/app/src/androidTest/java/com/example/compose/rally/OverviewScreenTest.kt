package com.example.compose.rally

import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.rally.ui.overview.OverviewBody
import org.junit.Rule
import org.junit.Test

class OverviewScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun overviewScreen_alertsDisplayed(){
        composeTestRule.setContent {
            OverviewBody()
        }
        composeTestRule
            .onNodeWithText("Alerts")
            .assertIsDisplayed()
    }

    //infinite animation 구현
    val infiniteElevationAnimation = rememberInfiniteTransition()
    val animatedElevation: Dp by infiniteElevationAnimation.animateValue(
        initialValue = 1.dp,
        targetValue = 8.dp,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    /*Card(elevation = animatedElevation){
    }*/

}