package com.example.speedrun_compose

import com.example.speedrun_compose.viewmodels.SharedGameViewModel
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speedrun_compose.viewmodels.GameListViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
            topBar = { MyTopBar(title = "SpeedBomb") },
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
                    Column {
                        Text("Introduce el nuevo nombre para el juego:")
                        TextField(
                            value = newGameName,  // Usamos la variable para el nuevo nombre
                            onValueChange = { newGameName = it },  // Actualizamos el nombre a medida que el usuario escribe
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Añadimos un espacio
                        Text("Selecciona un juego para editar:")
                        LazyColumn {
                            items(games) { game ->
                                Text(
                                    text = game.name,
                                    modifier = Modifier
                                        .clickable {
                                            selectedGame = game
                                            newGameName = game.name // Aseguramos que el nombre del juego se cargue al editar
                                        }
                                        .padding(8.dp)
                                        .border(
                                            width = 2.dp,
                                            color = if (selectedGame == game) Color.Gray else Color.Transparent, // Borde visible solo cuando está seleccionada
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(if (selectedGame == game) Color(0xFF9B9B9B) else Color.Transparent, shape = RoundedCornerShape(8.dp))
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        selectedGame?.let {
                            if (newGameName.isNotBlank()) {
                                // Llama a la función editGame del ViewModel con el nuevo nombre
                                viewModel.editGame(it, newGameName)
                            }
                        }
                        showSelectDialog = false
                        newGameName = ""  // Limpiamos el campo después de la edición
                    }) {
                        Text("Editar")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        selectedGame = null
                        showSelectDialog = false
                        newGameName = ""  // Limpiamos el campo si se cancela
                    }) {
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
        onGameSelected: (String) -> Unit
    ) {
        LazyColumn {
            items(games) { game ->
                // Separador encima del ítem
                HorizontalDivider(thickness = 1.dp, color = Color(0xFF757575))

                // Hacer que toda la fila sea clickeable para navegar a la pantalla de detalles
                Text(
                    text = game.name, // Usamos el nombre del juego
                    modifier = Modifier
                        .clickable { onGameSelected(game.name) }
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                // Separador debajo del ítem
                HorizontalDivider(thickness = 1.dp, color = Color(0xFF757575))
            }
        }
    }


    @Composable
    fun GameDetailScreen(gameName: String, viewModel: SharedGameViewModel = hiltViewModel()) {
        val sections by viewModel.sections.collectAsState(initial = emptyMap())
        var selectedSection by remember { mutableStateOf("") }
        val sectionTimers = remember { mutableStateMapOf<String, Pair<Long, Boolean>>() }
        var totalElapsedTime by remember { mutableLongStateOf(0L) }
        var showCreateSectionDialog by remember { mutableStateOf(false) }
        var showEditSectionDialog by remember { mutableStateOf(false) }
        var showDeleteSectionDialog by remember { mutableStateOf(false) }
        val visuallyDeletedSections = remember { mutableStateListOf<String>() }
        var newSectionName by remember { mutableStateOf(TextFieldValue("")) }

        // Actualizar los cronómetros
        LaunchedEffect(key1 = sections) {
            sectionTimers.clear()
            sections.forEach { (_, sectionsList) ->
                sectionsList.forEach { section ->
                    sectionTimers[section] = sectionTimers[section] ?: (0L to false)
                }
            }
        }

        LaunchedEffect(sectionTimers) {
            while (true) {
                kotlinx.coroutines.delay(10L)
                sectionTimers.forEach { (key, value) ->
                    val (time, isRunning) = value
                    if (isRunning) {
                        sectionTimers[key] = time + 10L to true
                    }
                }
                totalElapsedTime = sectionTimers.values.sumOf { it.first }
            }
        }

        // Función para convertir tiempo en milisegundos a formato hh:mm:ss
        fun formatTime(milliseconds: Long): String {
            val hours = (milliseconds / 3600000).toInt()
            val minutes = ((milliseconds % 3600000) / 60000).toInt()
            val seconds = ((milliseconds % 60000) / 1000).toInt()
            val millis = (milliseconds % 1000).toInt()
            return String.format("%02d:%02d:%02d.%03d", hours, minutes, seconds, millis)
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text("Detalles de $gameName", style = MaterialTheme.typography.headlineSmall)

                if (sections.isEmpty()) {
                    Text("No hay secciones añadidas.", color = Color.Gray)
                } else {
                    LazyColumn {
                        sections.forEach { (game, sectionsList) ->
                            if (game == gameName) { // Verifica si el juego coincide
                                item {
                                    Text(
                                        game,
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                                // Filtra las secciones eliminadas visualmente
                                val filteredSections = sectionsList.filter { section ->
                                    !visuallyDeletedSections.contains(section)
                                }

                                items(filteredSections) { section ->
                                    val (time, isRunning) = sectionTimers[section] ?: (0L to false)
                                    val isSelected = section == selectedSection // Define si esta sección está seleccionada

                                    // Verifica si hay algún cronómetro en ejecución
                                    val isAnySectionRunning = sectionTimers.values.any { it.second }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .clickable {
                                                if (!isAnySectionRunning) { // Solo permite la selección si no hay cronómetros en ejecución
                                                    selectedSection = section
                                                }
                                            }
                                            .border(
                                                width = 2.dp,
                                                color = if (isSelected) Color.Gray else Color.Transparent, // Borde visible solo cuando está seleccionada
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .background(if (isSelected) Color(0xFF1D1D1D) else Color.Transparent), // Fondo con color cuando seleccionada
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(section) // Nombre de la sección
                                            Text("Tiempo: ${formatTime(time)}")
                                        }

                                        // Solo permite cambiar el estado del cronómetro si no hay cronómetros en ejecución
                                        IconButton(
                                            onClick = {
                                                if (!isAnySectionRunning || isRunning) { // Solo permite activar o pausar si todos están pausados
                                                    val (currentTime, isRunning) = sectionTimers[section]
                                                        ?: (0L to false)
                                                    sectionTimers[section] = currentTime to !isRunning
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
                    }
                }

                Text(
                    "Tiempo total acumulado: ${formatTime(totalElapsedTime)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF9B9B9B)
                )
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { showCreateSectionDialog = true }) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir Sección")
                }

                IconButton(
                    onClick = { if (selectedSection.isNotEmpty()) showEditSectionDialog = true },
                    enabled = selectedSection.isNotEmpty()
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Editar Sección")
                }

                IconButton(
                    onClick = { if (selectedSection.isNotEmpty()) showDeleteSectionDialog = true },
                    enabled = selectedSection.isNotEmpty()
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Eliminar Sección")
                }
            }

            if (showCreateSectionDialog) {
                AlertDialog(
                    onDismissRequest = { showCreateSectionDialog = false },
                    title = { Text("Crear nueva sección") },
                    text = {
                        TextField(
                            value = newSectionName,
                            onValueChange = { newSectionName = it }
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.viewModelScope.launch {
                                    viewModel.addSection(gameName, newSectionName.text)
                                }
                                newSectionName = TextFieldValue("")
                                showCreateSectionDialog = false
                            }
                        ) {
                            Text("Crear")
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
                                    viewModel.viewModelScope.launch {
                                        viewModel.editSection(gameName, selectedSection, newSectionName.text)
                                    }
                                    selectedSection = newSectionName.text // Cambia la sección seleccionada
                                    newSectionName = TextFieldValue("") // Limpia el campo
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
                    text = { Text("¿Estás seguro de que deseas eliminar \"$selectedSection\" de la vista?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Añadir la sección a la lista de eliminados visualmente
                                visuallyDeletedSections.add(selectedSection)
                                selectedSection = "" // Limpiar la selección
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyTopBar(title: String) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,  // Opcional, elige el peso de la fuente
                        fontSize = 20.sp  // Opcional, ajusta el tamaño de la fuente
                    )
                )
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White
            ),
            modifier = Modifier.background(MaterialTheme.colorScheme.primary),
        )
    }




    @Composable
    fun BottomMenuBar(
        onCreateGame: () -> Unit,
        onEditGame: () -> Unit,
        onDeleteGame: () -> Unit
    ) {
        BottomAppBar(
            containerColor = Color(0xFF212121), // Color de fondo personalizado (gris oscuro)
            contentColor = Color.White, // Color de los íconos y texto (blanco)
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { onCreateGame() }) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Crear Juego",
                        tint = Color.White // Ícono blanco
                    )
                }
                IconButton(onClick = { onEditGame() }) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Editar Juego",
                        tint = Color.White // Ícono blanco
                    )
                }
                IconButton(onClick = { onDeleteGame() }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Borrar Juego",
                        tint = Color.White // Ícono blanco
                    )
                }
            }
        }
    }


}

