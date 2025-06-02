package com.example.proyectfaseii.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.proyectfaseii.notifications.NotificationHelper

/**
 * Worker que envía una notificación diaria si las notificaciones están habilitadas.
 */
class ReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = SharedPrefManager.getInstance(applicationContext)
        if (prefs.getNotificationsEnabled()) {
            val title = "¡Hora de tu hábito!"
            val message = "Revisa tus hábitos y marca tu progreso diario."
            NotificationHelper.sendNotification(applicationContext, title, message)
        }
        return Result.success()
    }
}
