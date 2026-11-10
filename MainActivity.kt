package com.ultra.launcher

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*\nimport androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)\nclass MainActivity : ComponentActivity() {
    private lateinit var library: LauncherLibrary
    private var refresh by mutableIntStateOf(0)
    private val picker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { selected ->
            lifecycleScope.launch {
                runCatching { library.importFile(selected) }
                    .onSuccess { refresh++ }
                    .onFailure { error = it.message ?: "Falha ao importar arquivo" }
            }
        }
    }
    private var error by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        library = LauncherLibrary(this)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                val files = remember(refresh) { library.listImportedFiles() }
                var selectedProfile by remember { mutableStateOf("Principal") }
                Scaffold(
                    topBar = { TopAppBar(title = { Text("ULTRA LAUNCHER") }) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            picker.launch(arrayOf("application/java-archive", "application/zip", "application/octet-stream"))
                        }) { Icon(Icons.Default.Add, contentDescription = "Importar arquivo") }
                    }
                ) { padding ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("Minecraft Java", style = MaterialTheme.typography.headlineMedium)
                            Text("Núcleo Android • versão 0.1.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        item {
                            Card {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Perfil ativo", style = MaterialTheme.typography.titleMedium)
                                    Spacer(Modifier.height(8.dp))
                                    var expanded by remember { mutableStateOf(false) }
                                    Box {
                                        OutlinedButton(onClick = { expanded = true }) { Text(selectedProfile) }
                                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                            listOf("Principal", "Modificado", "Vanilla").forEach {
                                                DropdownMenuItem(text = { Text(it) }, onClick = {
                                                    selectedProfile = it; expanded = false
                                                })
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text("Estado: base do launcher em construção")
                                    Text("Iniciar jogo: ainda não disponível", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        item {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Folder, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Biblioteca importada", style = MaterialTheme.typography.titleLarge)
                            }
                            Text("Use + para importar mods, pacotes de textura ou modpacks.")
                        }
                        if (files.isEmpty()) item { Text("Nenhum arquivo importado.") }
                        items(files) { file ->
                            ListItem(
                                headlineContent = { Text(file.name) },
                                supportingContent = { Text(file.parentFile?.name ?: "Biblioteca") }
                            )
                            HorizontalDivider()
                        }
                        error?.let { message ->
                            item { Text(message, color = MaterialTheme.colorScheme.error) }
                        }
                        item {
                            Text(
                                "A importação organiza arquivos; compatibilidade e instalação automática serão adicionadas depois.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
