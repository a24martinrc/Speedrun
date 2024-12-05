package com.example.speedrun_compose

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speedrun_compose.ui.theme.SpeedRunAppTheme
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speedrun_compose.viewmodels.GameListViewModel
import com.example.speedrun_compose.viewmodels.SharedGameViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedRunAppTheme {
                Surface(
                    modifier = Modifier.padding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SpeedRunAppNavigation() // Composable para gestionar permisos
                }
            }
        }
    }

    @Composable
    fun SpeedRunAppNavigation() {
        val viewModel: GameListViewModel = viewModel() // Obtén el ViewModel
        val games by viewModel.games.collectAsState(initial = emptyList()) // Observa los juegos del ViewModel
        val navController = rememberNavController()

        // Diálogos y estados
        var showCreateDialog by remember { mutableStateOf(false) }
        var showSelectDialog by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) } // Aquí declaras showDeleteDialog
        var selectedGame by remember { mutableStateOf<Game?>(null) }
        var newGameName by remember { mutableStateOf("") }
        var currentRoute by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(navController) {
            snapshotFlow { navController.currentBackStackEntry?.destination?.route }
                .collect { route -> currentRoute = route }
        }

        Scaffold(
            topBar = { /* Configura una barra superior si lo deseas */ },
            bottomBar = {
                if (currentRoute == "game_list") {
                    BottomMenuBar(
                        onCreateGame = { showCreateDialog = true },
                        onEditGame = { showSelectDialog = true },
                        onDeleteGame = { showSelectDialog = true }
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "game_list",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("game_list") {
                    GameListScreen(
                        games = games,
                        onAddGame = { showCreateDialog = true },
                        onGameSelected = { gameName ->
                            navController.navigate("game_details/$gameName")
                        }
                    )
                }
                composable("game_details/{gameName}") { backStackEntry ->
                    val gameName = backStackEntry.arguments?.getString("gameName") ?: ""
                    GameDetailScreen(gameName)
                }
                composable("add_speedrun/{gameName}") { backStackEntry ->
                    val gameName = backStackEntry.arguments?.getString("gameName") ?: ""
                    AddSpeedrunScreen(gameName)
                }
            }
        }

        // Diálogo para crear un nuevo juego
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Crear un nuevo juego") },
                text = {
                    Column {
                        Text("Introduce el nombre del nuevo juego:")
                        TextField(
                            value = newGameName,
                            onValueChange = { newGameName = it },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (newGameName.isNotBlank()) {
                            viewModel.addGame(Game(0, newGameName)) // Agrega un juego manualmente
                            newGameName = ""
                            showCreateDialog = false
                        }
                    }) {
                        Text("Crear")
                    }
                },
                dismissButton = {
                    Button(onClick = { showCreateDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Diálogo para seleccionar un juego (puedes expandir esto según lo necesites)
        if (showSelectDialog) {
            AlertDialog(
                onDismissRequest = { showSelectDialog = false },
                title = { Text("Seleccionar juego") },
                text = {
                    LazyColumn {
                        items(games) { game ->
                            Text(
                                text = game.name,
                                modifier = Modifier
                                    .clickable { selectedGame = game }
                                    .padding(8.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        // Aquí podrías manejar ediciones, por ejemplo.
                        showSelectDialog = false
                    }) {
                        Text("Aceptar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showSelectDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Diálogo para confirmar la eliminación del juego
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar juego") },
                text = {
                    Text("¿Estás seguro de que quieres eliminar \"${selectedGame?.name}\"?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedGame?.let { viewModel.removeGame(it) } // Llama al ViewModel para eliminar el juego
                            selectedGame = null
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        selectedGame = null
                        showDeleteDialog = false
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }


        @Composable
    fun GameListScreen(
        games: List<Game>, // Cambié de List<String> a List<Game>
        onAddGame: () -> Unit,
        onGameSelected: (String) -> Unit
    ) {
        LazyColumn {
            items(games) { game ->
                Text(
                    text = game.name, // Usamos el nombre del juego
                    modifier = Modifier
                        .clickable { onGameSelected(game.name) } // Pasamos el nombre del juego
                        .padding(8.dp)
                )
            }
        }
    }

    @Composable
    fun GameDetailScreen(gameName: String, viewModel: SharedGameViewModel = hiltViewModel()) {
        var sections = viewModel.getSections(gameName)
        var selectedSection by remember { mutableStateOf("") }
        var sectionTimers by remember { mutableStateOf(mapOf<String, Pair<Long, Boolean>>()) }
        var totalElapsedTime by remember { mutableStateOf(0L) }
        var showCreateSectionDialog by remember { mutableStateOf(false) }
        var showEditSectionDialog by remember { mutableStateOf(false) }
        var showDeleteSectionDialog by remember { mutableStateOf(false) }
        var newSectionName by remember { mutableStateOf(TextFieldValue("")) }

        // Efecto para gestionar los cronómetros activos
        LaunchedEffect(sectionTimers) {
            while (true) {
                kotlinx.coroutines.delay(10L) // Reducimos el intervalo a 10 ms para contar milisegundos
                sectionTimers = sectionTimers.mapValues { (key, value) ->
                    val (time, isRunning) = value
                    if (isRunning) time + 10 to isRunning else time to isRunning
                }
                totalElapsedTime = sectionTimers.values.sumOf { it.first }
            }
        }

        // Función para convertir tiempo en milisegundos a horas:minutos:segundos.milisegundos
        fun formatTime(milliseconds: Long): String {
            val hours = (milliseconds / 3600000).toInt() // 1 hora = 3600000 milisegundos
            val minutes = ((milliseconds % 3600000) / 60000).toInt() // 1 minuto = 60000 milisegundos
            val seconds = ((milliseconds % 60000) / 1000).toInt() // 1 segundo = 1000 milisegundos
            val millis = (milliseconds % 1000).toInt() // Milisegundos

            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            // Contenido principal
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text("Detalles de $gameName", style = MaterialTheme.typography.headlineSmall)

                // Lista de secciones
                if (sections.isEmpty()) {
                    Text("No hay secciones añadidas.", color = Color.Gray)
                } else {
                    LazyColumn {
                        items(sections) { section ->
                            val (time, isRunning) = sectionTimers[section] ?: 0L to false
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .background(
                                        if (selectedSection == section) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        else Color.Transparent
                                    )
                                    .border(
                                        width = if (selectedSection == section) 2.dp else 0.dp,
                                        color = if (selectedSection == section) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                    .clickable { selectedSection = section }, // Cambiar la selección al hacer clic
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        section,
                                        style = if (selectedSection == section) MaterialTheme.typography.bodyLarge.copy(
                                            color = MaterialTheme.colorScheme.primary
                                        ) else MaterialTheme.typography.bodyLarge
                                    )
                                    Text("Tiempo: ${formatTime(time)}")
                                }
                                IconButton(
                                    onClick = {
                                        sectionTimers = sectionTimers.mapValues { (key, value) ->
                                            if (key == section) value.first to !value.second else value
                                        }
                                    }
                                ) {
                                    Icon(
                                        if (isRunning) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = if (isRunning) "Pausar" else "Reanudar"
                                    )
                                }
                            }
                        }
                    }

                }

                Spacer(modifier = Modifier.weight(1f))

                // Cronómetro total (con formato adecuado)
                Text(
                    "Tiempo total acumulado: ${formatTime(totalElapsedTime)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Blue
                )
            }

            // Menú de gestión de secciones (abajo)
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(vertical = 8.dp)
            ) {
                // Botón para agregar una nueva sección
                IconButton(onClick = { showCreateSectionDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir Sección")
                }

                // Botón para editar la sección seleccionada
                IconButton(
                    onClick = {
                        if (selectedSection.isNotEmpty()) showEditSectionDialog = true
                    },
                    enabled = selectedSection.isNotEmpty()
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar Sección")
                }

                // Botón para eliminar la sección seleccionada
                IconButton(
                    onClick = {
                        if (selectedSection.isNotEmpty()) showDeleteSectionDialog = true
                    },
                    enabled = selectedSection.isNotEmpty()
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar Sección")
                }
            }
        }

        // Diálogos (Crear, Editar, Eliminar)
        if (showCreateSectionDialog) {
            AlertDialog(
                onDismissRequest = { showCreateSectionDialog = false },
                title = { Text("Crear una nueva sección") },
                text = {
                    TextField(
                        value = newSectionName,
                        onValueChange = { newSectionName = it },
                        label = { Text("Nombre de la sección") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newSectionName.text.isNotBlank()) {
                                viewModel.viewModelScope.launch {
                                    viewModel.addSection(gameName, newSectionName.text)
                                }
                                newSectionName = TextFieldValue("")
                                showCreateSectionDialog = false
                            }
                        }
                    ) {
                        Text("Crear")
                    }
                },
                dismissButton = {
                    Button(onClick = { showCreateSectionDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
        if (showEditSectionDialog && selectedSection.isNotEmpty()) {
            AlertDialog(
                onDismissRequest = { showEditSectionDialog = false },
                title = { Text("Editar sección") },
                text = {
                    TextField(
                        value = newSectionName,
                        onValueChange = { newSectionName = it },
                        label = { Text("Nuevo nombre para \"$selectedSection\"") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newSectionName.text.isNotBlank()) {
                                val updatedSections = sections.map {
                                    if (it == selectedSection) newSectionName.text else it
                                }
                                val updatedTimers = sectionTimers.mapKeys {
                                    if (it.key == selectedSection) newSectionName.text else it.key
                                }
                                sections = updatedSections
                                sectionTimers = updatedTimers
                                selectedSection = newSectionName.text
                                newSectionName = TextFieldValue("") // Limpiar campo
                            }
                            showEditSectionDialog = false
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showEditSectionDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (showDeleteSectionDialog && selectedSection.isNotEmpty()) {
            AlertDialog(
                onDismissRequest = { showDeleteSectionDialog = false },
                title = { Text("Eliminar sección") },
                text = { Text("¿Estás seguro de que deseas eliminar \"$selectedSection\"?") },
                confirmButton = {
                    Button(
                        onClick = {
                            sections = sections.filter { it != selectedSection }
                            sectionTimers = sectionTimers - selectedSection
                            selectedSection = ""
                            totalElapsedTime = sectionTimers.values.sumOf { it.first }
                            showDeleteSectionDialog = false
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteSectionDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }

    @Composable
    fun AddSpeedrunScreen(gameName: String) {
        var time by remember { mutableStateOf(TextFieldValue("")) }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Detalles de $gameName", style = MaterialTheme.typography.titleLarge)

            BasicTextField(
                value = time,
                onValueChange = { time = it },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Button(onClick = { /* Aquí agregarías la lógica para guardar el speedrun */ }) {
                Text("Guardar Speedrun")
            }
        }
    }

    @Composable
    fun BottomMenuBar(onCreateGame: () -> Unit, onEditGame: () -> Unit, onDeleteGame: () -> Unit) {
        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onCreateGame() }) {
                    Icon(Icons.Filled.Add, contentDescription = "Crear Juego")
                }
                IconButton(onClick = { onEditGame() }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar Juego")
                }
                IconButton(onClick = { onDeleteGame() }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Borrar Juego")
                }
            }
        }
    }
}

