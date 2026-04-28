package com.example.simpleweighttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.simpleweighttracker.ui.theme.SimpleWeightTrackerTheme
import com.example.simpleweighttracker.ui.WeightTrackerApp
import com.example.simpleweighttracker.ui.WeightTrackerViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WeightTrackerViewModel by viewModels {
        WeightTrackerViewModel.Factory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimpleWeightTrackerTheme {
                WeightTrackerApp(viewModel = viewModel)
            }
        }
    }
}
