package com.ucb.morfeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ucb.morfeo.navigation.AppNavigator
import com.ucb.morfeo.ui.theme.MorfeoTheme
import io.sentry.Sentry

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    // waiting for view to draw to better represent a captured error with a screenshot
        enableEdgeToEdge()
        setContent {
            MorfeoTheme {
                LaunchedEffect(true) {
                    try{
                        throw CustomSentryTestException("Sentry test exception")
                    } catch (e: Exception) {
                        Sentry.captureException(e)
                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigator()
                }
            }
        }
    }
    class CustomSentryTestException(message:String): Exception(message)
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MorfeoTheme {
        Greeting("Android")
    }
}