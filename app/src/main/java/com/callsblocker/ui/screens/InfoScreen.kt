package com.callsblocker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.callsblocker.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Info") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // App Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(24.dp)
            ) {
                Text(
                    text = "CallsBlocker",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "v${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Description
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Descrizione",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "CallsBlocker è un'app per il blocco intelligente di chiamate in arrivo. Permette di creare una blacklist personalizzata con:\n\n• Blocco totale (rifiuta e manda occupato)\n• Silenzio (mette in muto senza rifiutare)\n• Whitelist (lascia passare)\n\nSupporta sia numeri esatti che prefissi wildcard per bloccare intere famiglie di numeri.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            HorizontalDivider()

            // Features
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Funzionalità",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "✓ Blocco intelligente per numero o prefisso\n✓ Azioni configurabili per voce\n✓ Import/Export CSV e TXT\n✓ Interfaccia Material Design 3\n✓ Database locale con Room\n✓ Sincronizzazione istantanea",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            HorizontalDivider()

            // Requirements
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Requisiti",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Android 10 (API 29) o superiore",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            HorizontalDivider()

            // Permissions
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Autorizzazioni",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "• Registro chiamate: leggi le chiamate recenti\n• Gestore schermata chiamate: intercetta le chiamate in arrivo\n• Esenzione batteria: previeni la sospensione del servizio",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            HorizontalDivider()

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Copyright © 2026 CallsBlocker",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontStyle = FontStyle.Italic
                )
                Text(
                    text = "Sviluppato con Kotlin e Jetpack Compose",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
