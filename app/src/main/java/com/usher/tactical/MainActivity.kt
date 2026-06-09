package com.usher.tactical

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.usher.tactical.ui.navigation.NavGraph
import com.usher.tactical.ui.theme.TacticalTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TacticalTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
