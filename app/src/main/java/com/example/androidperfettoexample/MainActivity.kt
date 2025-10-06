package com.example.androidperfettoexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidperfettoexample.ui.theme.AndroidPerfettoExampleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidPerfettoExampleTheme {
                DemoScreen()
            }
        }
    }
}

@Composable
fun DemoScreen() {
    var items by remember { mutableStateOf(listOf<String>()) }
    var loading by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(20.dp))
        Button(onClick = {
            loading = true
            // ❌ Simulate slow blocking work on UI thread
            Thread.sleep(2000)  // This will cause jank
            items = List(20) { "Item SLOW - $it" }
            loading = false
        }) {
            Text("Load Items (SLOW)")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = {
            loading = true
            // ✅ Move work to background with coroutine
            GlobalScope.launch {
                val data = withContext(Dispatchers.Default) {
                    Thread.sleep(2000)  // heavy work
                    List(20) { "Item OPTIMIZED - $it" }
                }
                withContext(Dispatchers.Main) {
                    items = data
                    loading = false
                }
            }
        }) {
            Text("Load Items (OPTIMIZED)")
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(thickness = 20.dp, color = Color.Green)
        if (loading) {
            CircularProgressIndicator()
        } else {
            LazyColumn {
                items(items.size) { index ->
                    Text(text = items[index], modifier = Modifier.padding(8.dp))
                }
            }
        }
        HorizontalDivider(thickness = 20.dp, color = Color.Yellow)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidPerfettoExampleTheme {
        Greeting("Android")
    }
}

