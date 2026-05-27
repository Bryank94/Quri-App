package com.example.quritfg.datos.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.quritfg.R

class QuriHabitNotifier(private val context: Context) {
    private val appContext = context.applicationContext

    fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quri habitos",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Recordatorios inteligentes para mantener el habito de ahorro"
            }
            appContext.getSystemService(NotificationManager::class.java)
                ?.createNotificationChannel(channel)
        }
    }

    fun canNotify(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    fun sendHabitPreview(mensaje: String = "Revisa tu plan mensual: puedes acercarte a tus fondos esta semana.") {
        ensureChannel()
        if (!canNotify()) return

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_quri_pig)
            .setContentTitle("Quri detecto una oportunidad")
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(appContext).notify(HABIT_PREVIEW_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "quri_habit_notifications"
        private const val HABIT_PREVIEW_ID = 2027
    }
}

