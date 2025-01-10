package com.example.automacorp.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.automacorp.AutomacorpTopAppBar
import com.example.automacorp.MainActivity
import com.example.automacorp.PlaceActivity
import com.example.automacorp.model.PlaceDto
import com.example.automacorp.model.PlaceViewModel
import com.example.automacorp.ui.theme.AutomacorpTheme
import kotlinx.coroutines.flow.asStateFlow

class PlaceListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: PlaceViewModel by viewModels()

        val openPlace: (id: Long) -> Unit = { id ->
            val intent = Intent(this, PlaceActivity::class.java).apply {
                putExtra("PLACE_PARAM", id.toString())
            }
            startActivity(intent)
        }

        val navigateBack: () -> Unit = {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val onPlaceCreate: () -> Unit = {
            showCreatePlaceDialog(viewModel)
        }

        setContent {
            AutomacorpTheme {
                val placesState by viewModel.placesState.asStateFlow().collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.findAll()
                }

                Scaffold(
                    topBar = { AutomacorpTopAppBar("Places", navigateBack) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = onPlaceCreate) {
                            Icon(Icons.Filled.Create, contentDescription = "Create Place")
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        when {
                            placesState.error != null -> {
                                Text(
                                    "Error loading Places: ${placesState.error}",
                                    color = Color.Red,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                            placesState.places.isEmpty() -> {
                                Text("No Places found", modifier = Modifier.padding(16.dp))
                            }
                            placesState.places.isNotEmpty() -> {
                                PlaceList(places = placesState.places, openPlace = openPlace)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showCreatePlaceDialog(viewModel: PlaceViewModel) {
        val placeNameInput = EditText(this).apply {
            hint = "Place Name"
            inputType = InputType.TYPE_CLASS_TEXT
        }

        val currentHumidityInput = EditText(this).apply {
            hint = "Current Humidity"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val currentTemperatureInput = EditText(this).apply {
            hint = "Current Temperature"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val dialogView = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            addView(placeNameInput)
            addView(currentHumidityInput)
            addView(currentTemperatureInput)
        }

        AlertDialog.Builder(this)
            .setTitle("Create Place")
            .setView(dialogView)
            .setPositiveButton("Create") { _, _ ->
                val placeName = placeNameInput.text.toString()
                val currentHumidity = currentHumidityInput.text.toString().toDoubleOrNull()
                val currentTemperature = currentTemperatureInput.text.toString().toDoubleOrNull()

                if (placeName.isEmpty() || currentHumidity == null || currentTemperature == null) {
                    Toast.makeText(this, "Please fill in all fields correctly.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newPlace = PlaceDto(
                    id = System.currentTimeMillis(),
                    name = placeName,
                    currentTemperature = currentTemperature,
                    currentHumidity = currentHumidity,
                    windows = emptyList()
                )

                viewModel.createPlace(newPlace)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

@Composable
fun PlaceItem(place: PlaceDto, openPlace: (id: Long) -> Unit, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, Color.Gray),
        modifier = modifier
            .padding(4.dp)
            .clickable {
                openPlace(place.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Current Humidity: ${place.currentHumidity ?: "?"}%",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = "${place.currentTemperature ?: "?"}°C",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Right,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun PlaceList(
    places: List<PlaceDto>,
    openPlace: (id: Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(places, key = { it.id }) { place ->
            PlaceItem(
                place = place,
                openPlace = openPlace,
                modifier = Modifier
            )
        }
    }
}
