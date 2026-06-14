package com.ikeansoft.simpleweighttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.ikeansoft.simpleweighttracker.ui.theme.SimpleWeightTrackerTheme
import com.ikeansoft.simpleweighttracker.ui.WeightTrackerApp
import com.ikeansoft.simpleweighttracker.ui.WeightTrackerViewModel

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
