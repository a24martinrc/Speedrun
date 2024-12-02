package com.example.speedrun_compose

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView

class FloatingMenuService : Service() {

    private lateinit var floatingView: ComposeView
    private val CHANNEL_ID = "ForegroundServiceChannel"

    @SuppressLint("ForegroundServiceType")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()

        // Verificar si tiene permiso para dibujar sobre otras aplicaciones
        if (!Settings.canDrawOverlays(applicationContext)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:${applicationContext.packageName}")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            stopSelf() // Detener el servicio si no tiene permiso
            return
        }

        // Crear un canal de notificación si la versión de Android es Oreo o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Notificación para servicio en primer plano
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Servicio en ejecución")
            .setContentText("El servicio está en primer plano.")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Cambia esto por tu propio icono
            .build()

        // Iniciar el servicio en primer plano
        startForeground(1, notification) // '1' es el ID de la notificación

        Log.d("FloatingMenuService", "Servicio en onCreate")

        // Verificar el permiso para dibujar sobre otras apps
        if (Settings.canDrawOverlays(applicationContext)) {

            // Canal de notificación para Android 8.0 y superior
            val notificationManager = getSystemService(NotificationManager::class.java)
            val channelId = "floating_menu_channel"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Floating Menu Service",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            // Crear la notificación que aparecerá mientras el servicio está activo
            val notification: Notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Servicio Flotante Activo")
                .setContentText("El menú flotante está en ejecución")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build()

            // Iniciar el servicio en primer plano con notificación
            startForeground(1, notification)

            // Crear el WindowManager y configurar la vista flotante
            val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.START or Gravity.TOP
                x = 0
                y = 0
            }

            // Crear y asignar el contenido a la vista flotante
            floatingView = ComposeView(applicationContext).apply {
                setContent {
                    FloatingMenuContent()
                }
            }

            // Agregar la vista flotante al WindowManager
            windowManager.addView(floatingView, params)
            Log.d("FloatingMenuService", "Vista flotante añadida al WindowManager")

        } else {
            Log.d("FloatingMenuService", "Permiso no concedido para dibujar sobre otras apps")
            Toast.makeText(this, "No tienes permiso para mostrar contenido sobre otras apps", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        // Eliminar la vista flotante cuando el servicio sea destruido
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.removeViewImmediate(floatingView)
        Log.d("FloatingMenuService", "Vista flotante eliminada y servicio destruido")
    }

    // Composable que define el contenido del menú flotante
    @Composable
    fun FloatingMenuContent() {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Menú Flotante", color = Color.Black, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                Toast.makeText(applicationContext, "Play presionado", Toast.LENGTH_SHORT).show()
            }) {
                Text("Play")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                Toast.makeText(applicationContext, "Pause presionado", Toast.LENGTH_SHORT).show()
            }) {
                Text("Pause")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                Toast.makeText(applicationContext, "Siguiente sección", Toast.LENGTH_SHORT).show()
            }) {
                Text("Next Section")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { stopSelf() }) {
                Text("Cerrar Menú")
            }
        }
    }
}
