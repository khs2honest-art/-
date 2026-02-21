package com.example.tapcircles

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tap_times")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TapCirclesScreen()
                }
            }
        }
    }
}

@Composable
private fun TapCirclesScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val k1 = remember { stringPreferencesKey("circle_1_time") }
    val k2 = remember { stringPreferencesKey("circle_2_time") }
    val k3 = remember { stringPreferencesKey("circle_3_time") }

    val t1 by context.dataStore.data.map { it[k1] ?: "-" }.collectAsState(initial = "-")
    val t2 by context.dataStore.data.map { it[k2] ?: "-" }.collectAsState(initial = "-")
    val t3 by context.dataStore.data.map { it[k3] ?: "-" }.collectAsState(initial = "-")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        CircleRow(label = "1", timeText = t1) {
            scope.launch { context.dataStore.edit { it[k1] = nowString() } }
        }
        CircleRow(label = "2", timeText = t2) {
            scope.launch { context.dataStore.edit { it[k2] = nowString() } }
        }
        CircleRow(label = "3", timeText = t3) {
            scope.launch { context.dataStore.edit { it[k3] = nowString() } }
        }
    }
}

@Composable
private fun CircleRow(
    label: String,
    timeText: String,
    onTap: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(56.dp)
                .clickable { onTap() },
            shape = CircleShape,
            tonalElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.width(18.dp))

        Text(
            text = timeText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun nowString(): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
    return fmt.format(Date())
}
