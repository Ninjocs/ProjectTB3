package com.example.automacorp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.automacorp.model.PlaceViewModel
import com.example.automacorp.ui.theme.AutomacorpTheme

class PlaceDetailActivity : ComponentActivity() {

    private val placeViewModel: PlaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val placeId = intent.getLongExtra("PLACE_ID", -1L)
        if (placeId == -1L) {
            // Handle case where the place ID is not found
            Toast.makeText(this, "Place not found!", Toast.LENGTH_SHORT).show()
            finish() // Close the activity if place ID is invalid
        } else {
            setContent {
                AutomacorpTheme {
                    PlaceDetailScreen(viewModel = placeViewModel, placeId = placeId)
                }
            }
        }
    }
}

@Composable
fun PlaceDetailScreen(viewModel: PlaceViewModel, placeId: Long) {
    LaunchedEffect(placeId) {
        viewModel.findPlaceByIdOrName(placeId, null)
    }

    val place = viewModel.place

    if (place != null) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Place Details:",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Name: ${place.name}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Current Temperature: ${place.currentTemperature}°",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Current Humidity: ${place.currentHumidity}%",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    } else {
        Text(
            text = "Place details not available.",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}
