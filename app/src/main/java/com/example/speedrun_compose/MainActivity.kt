package com.example.speedrun_compose

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speedrun_compose.ui.theme.SpeedRunAppTheme
import android.provider.Settings
import androidx.annotation.RequiresApi


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
        val navController = rememberNavController()
        var games by remember {
            mutableStateOf(
                listOf(
                    "Super Mario Bros",
                    "The Legend of Zelda",
                    "Minecraft",
                    "Dark Souls"
                )
            )
        }

        // Diálogos
        var showCreateDialog by remember { mutableStateOf(false) }
        var showEditDialog by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var showSelectDialog by remember { mutableStateOf(false) }
        var selectedGame by remember { mutableStateOf("") }
        var newGameName by remember { mutableStateOf(TextFieldValue("")) }

        // Variable para almacenar la ruta actual
        var currentRoute by remember { mutableStateOf<String?>(null) }

        // Utilizamos LaunchedEffect para observar cambios en la ruta
        LaunchedEffect(navController) {
            snapshotFlow { navController.currentBackStackEntry?.destination?.route }
                .collect { route ->
                    // Actualizamos currentRoute solo si ha cambiado
                    currentRoute = route
                    Log.d(
                        "NavRoute",
                        "Ruta actual: $route"
                    ) // Log para ver todas las rutas, incluyendo "game_details"
                }
        }

        // Comenzamos con el Scaffold para la estructura de la pantalla
        Scaffold(
            topBar = { /* Personaliza el topBar si lo deseas */ },
            bottomBar = {
                // Solo mostramos el menú cuando estamos en "game_list"
                if (currentRoute == "game_list") {
                    BottomMenuBar(
                        onCreateGame = { showCreateDialog = true },
                        onEditGame = { showSelectDialog = true },
                        onDeleteGame = { showSelectDialog = true }
                    )
                }
            }
        ) { paddingValues ->
            // El contenido de las pantallas de la aplicación
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
                    // Agrega este log para ver cuando llegas a la pantalla de detalles
                    Log.d(
                        "NavRoute",
                        "Ruta actual en Game Details: ${navController.currentBackStackEntry?.destination?.route}"
                    )
                }
                composable("add_speedrun/{gameName}") { backStackEntry ->
                    val gameName = backStackEntry.arguments?.getString("gameName") ?: ""
                    AddSpeedrunScreen(gameName)
                }
            }
        }

        // Diálogos de la aplicación
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Crear un nuevo juego") },
                text = {
                    Column {
                        Text("Introduce el nombre del nuevo juego:")
                        BasicTextField(
                            value = newGameName,
                            onValueChange = { newGameName = it },
                            textStyle = TextStyle(color = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newGameName.text.isNotBlank()) {
                                games = games + newGameName.text
                                newGameName = TextFieldValue("") // Limpiar el campo de texto
                            }
                            showCreateDialog = false
                        }
                    ) {
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

        // Mantener el valor actualizado de selectedGame
        LaunchedEffect(showSelectDialog) {
            if (showSelectDialog && selectedGame.isBlank() && games.isNotEmpty()) {
                selectedGame =
                    games.first() // Si no hay juego seleccionado, se selecciona el primero de la lista
            }
        }

        // 1. Modificar el nombre de un juego
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("Editar nombre del juego") },
                text = {
                    Column {
                        Text("Introduce el nuevo nombre para \"$selectedGame\":")
                        BasicTextField(
                            value = newGameName,
                            onValueChange = { newGameName = it },
                            textStyle = TextStyle(color = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newGameName.text.isNotBlank()) {
                                // Actualizar la lista de juegos con el nuevo nombre
                                games = games.map {
                                    if (it == selectedGame) newGameName.text else it
                                }
                                selectedGame =
                                    newGameName.text // Asegurarse de que el juego seleccionado también tenga el nuevo nombre
                                newGameName = TextFieldValue("") // Limpiar el campo de texto
                            }
                            showEditDialog = false
                        }
                    ) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showEditDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // 2. Dialogo para seleccionar juego
        if (showSelectDialog) {
            AlertDialog(
                onDismissRequest = { showSelectDialog = false },
                title = { Text("Seleccionar juego") },
                text = {
                    Column {
                        Text("Selecciona un juego para editar o eliminar:")
                        LazyColumn {
                            items(games) { game ->
                                Text(
                                    text = game,
                                    modifier = Modifier
                                        .clickable {
                                            selectedGame =
                                                game // Se selecciona el juego sin cerrar el diálogo
                                        }
                                        .padding(8.dp)
                                )
                            }
                        }
                        // Mostrar el juego seleccionado
                        if (selectedGame.isNotBlank()) {
                            Text("Juego seleccionado: $selectedGame", color = Color.Gray)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedGame.isNotBlank()) {
                                showEditDialog = true // Muestra el diálogo de edición
                                showSelectDialog = false // Cerramos el diálogo de selección
                            }
                        }
                    ) {
                        Text("Editar")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            if (selectedGame.isNotBlank()) {
                                // Eliminar el juego seleccionado
                                games = games.filter { it != selectedGame }
                                showDeleteDialog = false // Cerramos el diálogo de eliminación
                            }
                        }
                    ) {
                        Text("Eliminar")
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
                    Text("¿Estás seguro de que quieres eliminar \"$selectedGame\"?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            games = games.filter { it != selectedGame }
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }


    @Composable
    fun GameListScreen(
        games: List<String>,
        onAddGame: () -> Unit,
        onGameSelected: (String) -> Unit
    ) {
        // Lista de juegos
        LazyColumn {
            items(games) { game ->
                GameItem(game, onGameSelected)
            }
        }
    }

    @Composable
    fun GameItem(game: String, onGameSelected: (String) -> Unit) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = game)
            Button(onClick = { onGameSelected(game) }) {
                Text("Ver detalles")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GameDetailScreen(gameName: String) {
        var sections by remember { mutableStateOf(listOf<String>()) }
        var showCreateSectionDialog by remember { mutableStateOf(false) }
        var showEditSectionDialog by remember { mutableStateOf(false) }
        var showDeleteSectionDialog by remember { mutableStateOf(false) }
        var selectedSection by remember { mutableStateOf("") }
        var newSectionName by remember { mutableStateOf(TextFieldValue("")) }

        // State del Drawer para mostrar el menú lateral
        val drawerState = rememberDrawerState(DrawerValue.Open) // Lo mantenemos abierto
        val scope = rememberCoroutineScope()

        // Column que contiene el contenido principal
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                // Contenido del Drawer (menú lateral)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text("Opciones", style = MaterialTheme.typography.titleLarge)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Play Button
                    Button(onClick = { /* Lógica para reproducir */ }) {
                        Text("Play")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Pause Button
                    Button(onClick = { /* Lógica para pausar */ }) {
                        Text("Pause")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Next Section Button
                    Button(onClick = { /* Lógica para la siguiente sección */ }) {
                        Text("Next Section")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Cancel Button
                    Button(onClick = { /* Lógica para cancelar */ }) {
                        Text("Cancel")
                    }
                }
            },
            content = {
                // Contenido principal de la pantalla
                Column(modifier = Modifier.padding(16.dp)) {
                    // Título con el nombre del juego
                    Text("Detalles de $gameName", style = MaterialTheme.typography.headlineSmall)

                    // Lista de secciones
                    if (sections.isEmpty()) {
                        Text("No hay secciones añadidas.", color = Color.Gray)
                    } else {
                        LazyColumn {
                            items(sections) { section ->
                                Text(
                                    section,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clickable {
                                            selectedSection =
                                                section // Permitir seleccionar sección
                                        }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Menú de gestión de secciones (añadir, editar, eliminar)
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Botón para agregar una nueva sección
                        IconButton(onClick = { showCreateSectionDialog = true }) {
                            Icon(Icons.Filled.Add, contentDescription = "Añadir Sección")
                        }

                        // Botón para editar una sección
                        IconButton(onClick = {
                            if (selectedSection.isNotBlank()) {
                                showEditSectionDialog = true
                            }
                        }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar Sección")
                        }

                        // Botón para eliminar una sección
                        IconButton(onClick = {
                            if (selectedSection.isNotBlank()) {
                                showDeleteSectionDialog = true
                            }
                        }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar Sección")
                        }
                    }
                }
            }
        )

        // Diálogo para crear una nueva sección
        if (showCreateSectionDialog) {
            AlertDialog(
                onDismissRequest = { showCreateSectionDialog = false },
                title = { Text("Crear una nueva sección") },
                text = {
                    Column {
                        Text("Introduce el nombre de la nueva sección:")
                        BasicTextField(
                            value = newSectionName,
                            onValueChange = { newSectionName = it },
                            textStyle = TextStyle(color = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newSectionName.text.isNotBlank()) {
                                sections = sections + newSectionName.text
                                newSectionName = TextFieldValue("") // Limpiar el campo
                            }
                            showCreateSectionDialog = false
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

        // Diálogo para editar una sección
        if (showEditSectionDialog && selectedSection.isNotBlank()) {
            AlertDialog(
                onDismissRequest = { showEditSectionDialog = false },
                title = { Text("Editar sección") },
                text = {
                    Column {
                        Text("Introduce el nuevo nombre para \"$selectedSection\":")
                        BasicTextField(
                            value = newSectionName,
                            onValueChange = { newSectionName = it },
                            textStyle = TextStyle(color = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newSectionName.text.isNotBlank()) {
                                sections = sections.map {
                                    if (it == selectedSection) newSectionName.text else it
                                }
                                selectedSection = newSectionName.text
                                newSectionName = TextFieldValue("") // Limpiar el campo
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

        // Diálogo para eliminar una sección
        if (showDeleteSectionDialog && selectedSection.isNotBlank()) {
            AlertDialog(
                onDismissRequest = { showDeleteSectionDialog = false },
                title = { Text("Eliminar sección") },
                text = {
                    Text("¿Estás seguro de que quieres eliminar \"$selectedSection\"?")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            sections = sections.filter { it != selectedSection }
                            showDeleteSectionDialog = false
                            selectedSection = "" // Limpiar selección
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