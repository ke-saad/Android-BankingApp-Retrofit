package com.emsi.retrofitbankingapp

import MainViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emsi.retrofitbankingapp.navigation.NavigationHost
import com.emsi.retrofitbankingapp.ui.theme.TpRestDataAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = viewModel()

            TpRestDataAndroidTheme {
                NavigationHost(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun HomeScreen(navController: NavController, mainViewModel: MainViewModel) {
    val formatOptions = listOf("JSON", "XML")
    val selectedFormat = remember { mutableStateOf("JSON") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEBEE))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenue",
            style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFFD32F2F)),
            modifier = Modifier.padding(bottom = 24.dp),
            fontWeight = FontWeight.Bold

        )


        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("accounts") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text(
                "Voir tous les comptes",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("create_account") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text(
                "Ajouter un nouveau compte",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DropdownMenu(selectedFormat.value, formatOptions) { selected ->
            selectedFormat.value = selected
            mainViewModel.setFormat(selected)
        }
    }
}

@Composable
fun DropdownMenu(
    selectedFormat: String,
    formatOptions: List<String>,
    onFormatSelected: (String) -> Unit
) {
    var expanded = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Button(
            onClick = { expanded.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text("Format actuel: $selectedFormat", color = Color.White)
        }
        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.align(Alignment.Center)
        ) {
            formatOptions.forEach { format ->
                DropdownMenuItem(
                    text = { Text(format, color = Color(0xFFD32F2F)) },
                    onClick = {
                        onFormatSelected(format)
                        expanded.value = false
                    }
                )
            }
        }
    }
}
