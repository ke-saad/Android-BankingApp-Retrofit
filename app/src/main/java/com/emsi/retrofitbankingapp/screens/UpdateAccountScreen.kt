package com.emsi.retrofitbankingapp.screens

import Compte
import MainViewModel
import TypeCompte
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun UpdateAccountScreen(viewModel: MainViewModel, compteId: Long) {
    var balance by remember { mutableStateOf("") }
    var accountType by remember { mutableStateOf(TypeCompte.COURANT) }
    var contentType by remember { mutableStateOf("application/json") }
    var compte by remember { mutableStateOf<Compte?>(null) }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = sdf.format(Date())

    LaunchedEffect(compteId, contentType) {
        val response = if (contentType == "application/json") {
            viewModel.apiJSONService.getCompteByIdJson(compteId)
        } else {
            viewModel.apiXMLService.getCompteByIdXml(compteId)
        }

        if (response.isSuccessful) {
            compte = response.body()
            balance = compte?.solde?.let {
                "%.2f".format(it)
            } ?: ""
            accountType = compte?.type ?: TypeCompte.COURANT
            Log.d("UpdateAccountScreen", "Détails du compte récupérés : $compte")
        } else {
            scope.launch {
                showSnackbar = true
                snackbarHostState.showSnackbar("Erreur lors de la récupération des données")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFEBEE))
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color(0xFFFFEBEE)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Editer le compte", color = Color.Red, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = balance,
                onValueChange = { newBalance ->
                    val formattedBalance = newBalance.toDoubleOrNull()?.let {
                        "%.2f".format(it)
                    } ?: newBalance
                    balance = formattedBalance
                },
                label = { Text("Solde") },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.CenterVertically),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White, focusedIndicatorColor = Color.Red
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Créé en : $formattedDate", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            UpdateAccountTypeDropdown(accountType, { accountType = it })

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (balance.isEmpty()) {
                        scope.launch {
                            showSnackbar = true
                            snackbarHostState.showSnackbar("Le solde ne peut pas être vide")
                        }
                        return@Button
                    }

                    val balanceAmount = balance.toDoubleOrNull()?.let {
                        "%.2f".format(it).toDouble()
                    } ?: run {
                        scope.launch {
                            showSnackbar = true
                            snackbarHostState.showSnackbar("Valeur du solde invalide")
                        }
                        return@Button
                    }

                    val updatedCompte = compte?.copy(
                        solde = balanceAmount, dateCreation = formattedDate, type = accountType
                    )

                    if (updatedCompte != null) {
                        Log.d("UpdateAccountScreen", "Mise à jour du compte avec : $updatedCompte")
                        viewModel.updateCompte(updatedCompte)
                        scope.launch {
                            snackbarHostState.showSnackbar("Compte mis à jour avec succès")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(Color(0xFFD32F2F)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text(
                    text = "Mettre à jour le compte",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateAccountTypeDropdown(accountType: TypeCompte, onAccountTypeChange: (TypeCompte) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = accountType.name,
            onValueChange = {},
            label = { Text("Type") },
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White, focusedIndicatorColor = Color.Red
            )
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("COURANT") },
                onClick = { onAccountTypeChange(TypeCompte.COURANT); expanded = false })
            DropdownMenuItem(text = { Text("EPARGNE") },
                onClick = { onAccountTypeChange(TypeCompte.EPARGNE); expanded = false })
        }
    }
}
