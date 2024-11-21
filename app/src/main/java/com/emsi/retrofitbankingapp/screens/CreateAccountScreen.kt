package com.emsi.retrofitbankingapp.screens

import Compte
import MainViewModel
import TypeCompte
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CreateAccountScreen(viewModel: MainViewModel) {
    var balance by remember { mutableStateOf("") }
    var accountType by remember { mutableStateOf(TypeCompte.COURANT) }
    var isCreating by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = sdf.format(Date())


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEBEE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Ajouter un compte",
                style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFFC62828))
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = balance,
                onValueChange = { balance = it },
                label = { Text("Solde") },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Créé le : $formattedDate",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFC62828))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                shape = RoundedCornerShape(0.dp),
                onClick = {
                    if (balance.isEmpty()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Le solde ne peut pas être vide.")
                        }
                        return@Button
                    }

                    val balanceAmount = balance.toDoubleOrNull()?.let {
                        if (it >= 0) "%.2f".format(it).toDouble() else null
                    } ?: run {
                        scope.launch {
                            snackbarHostState.showSnackbar("Valeur de solde invalide.")
                        }
                        return@Button
                    }

                    val compteToSave = Compte(
                        id = 0,
                        solde = balanceAmount,
                        dateCreation = formattedDate,
                        type = accountType
                    )

                    isCreating = true
                    viewModel.createCompte(compteToSave)

                    scope.launch {
                        snackbarHostState.showSnackbar("Compte créé avec succès!")
                        isCreating = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                enabled = !isCreating
            ) {
                Text(
                    text = if (isCreating) "Création en cours..." else "Soumettre",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            SnackbarHost(hostState = snackbarHostState)
        }
    }
}
