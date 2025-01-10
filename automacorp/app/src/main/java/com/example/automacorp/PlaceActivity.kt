package com.example.automacorp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.automacorp.model.PlaceViewModel
import com.example.automacorp.ui.theme.AutomacorpTheme

class PlaceActivity : ComponentActivity() {

    private val placeViewModel: PlaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val param = intent.getStringExtra("PLACE_PARAM")
        Log.d("PlaceActivity", "Received param: $param")

        val placeId = param?.toLongOrNull()

        if (placeId != null) {
            // Attempt to find the place by ID
            placeViewModel.findPlaceByIdOrName(id = placeId, name = null)
        } else {
            if (!param.isNullOrEmpty()) {
                placeViewModel.findPlaceByIdOrName(id = null, name = param)
            } else {
                // Show an error if the parameter is invalid
                Toast.makeText(this, "Invalid Place parameter", Toast.LENGTH_SHORT).show()
            }
        }

        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }

        val onPlaceSave: () -> Unit = {
            val currentPlace = placeViewModel.place
            if (currentPlace != null) {
                placeViewModel.updatePlace(currentPlace.id, currentPlace)
                Toast.makeText(this, "Place ${currentPlace.name} was updated", Toast.LENGTH_LONG).show()
                navigateBack()
            } else {
                Toast.makeText(this, "No place selected to save", Toast.LENGTH_SHORT).show()
            }
        }

        val onPlaceDelete: () -> Unit = {
            val currentPlace = placeViewModel.place
            if (currentPlace != null) {
                AlertDialog.Builder(this)
                    .setTitle("Delete Place")
                    .setMessage("Are you sure you want to delete the place '${currentPlace.name}'?")
                    .setPositiveButton("Yes") { _, _ ->
                        placeViewModel.deletePlace(currentPlace.id)
                        Toast.makeText(this, "Place deleted successfully", Toast.LENGTH_SHORT).show()
                        navigateBack()
                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                Toast.makeText(this, "No place selected to delete", Toast.LENGTH_SHORT).show()
            }
        }

        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Place Details", navigateBack) },
                    floatingActionButton = {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            PlaceUpdateButton(onPlaceSave)
                            FloatingActionButton(onClick = onPlaceDelete) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Place")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val place = placeViewModel.place
                    if (place != null) {
                        PlaceDetail(placeViewModel, Modifier.padding(innerPadding))
                    } else {
                        NoPlace(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.create_place_message),
            )
        },
        text = { Text(text = stringResource(R.string.create_place_message)) }
    )
}

@Composable
fun NoPlace(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.act_place_none),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PlaceDetail(model: PlaceViewModel, modifier: Modifier = Modifier) {
    val place = model.place

    if (place != null) {
        Column(modifier = modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.act_place_name),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = place.name ?: "",
                onValueChange = { newName -> model.place = place.copy(name = newName) },
                label = { Text(text = stringResource(R.string.act_place_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.act_place_current_temperature),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "${place.currentTemperature} °C",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.act_place_current_humidity),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "${place.currentHumidity}%",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
