package com.example.miniproyecto

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InventoryWidgetProvider : AppWidgetProvider() {

    // Acción para el ojo (mostrar/ocultar)
    private val ACTION_TOGGLE_VISIBILITY =
        "com.example.miniproyecto.ACTION_TOGGLE_VISIBILITY"

    // Para este proyecto usamos una variable estática simple
    companion object {
        // false = oculto (****) | true = visible (muestra saldo)
        private var isBalanceVisible = false
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // Criterios 7 y 10: clic en el ícono del ojo
        if (intent.action == ACTION_TOGGLE_VISIBILITY) {
            // Cambiar visibilidad
            isBalanceVisible = !isBalanceVisible

            // Actualizar todos los widgets
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context.packageName, javaClass.name)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        // 🔹 Intent para el ojo (toggle visibilidad)
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_VISIBILITY
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        views.setOnClickPendingIntent(
            R.id.widget_toggle_visibility_icon,
            togglePendingIntent
        )

        // 🔹 Intent para "Gestionar inventario"
        val manageIntent = Intent(context, MainActivity::class.java)
        val managePendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            manageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_manage_icon, managePendingIntent)
        views.setOnClickPendingIntent(R.id.widget_manage_label, managePendingIntent)

        // 🔹 Ahora sí: obtener el saldo desde Room (base de datos)
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val dao = db.productDao()

            // Si no hay productos, Room devuelve null → usamos 0.0
            val totalBalance = dao.getInventoryTotal() ?: 0.0

            // Formato moneda Colombia, con separador de miles y 2 decimales
            val format = NumberFormat.getCurrencyInstance(Locale("es", "CO")).apply {
                maximumFractionDigits = 2
                minimumFractionDigits = 2
            }
            val formattedBalance = format.format(totalBalance)

            if (isBalanceVisible) {
                // Mostrar saldo real
                views.setTextViewText(R.id.widget_balance_text, formattedBalance)
                views.setImageViewResource(
                    R.id.widget_toggle_visibility_icon,
                    R.drawable.ic_eye_closet_white   // 👁️ cerrado
                )
            } else {
                // Mostrar asteriscos
                views.setTextViewText(R.id.widget_balance_text, "$ ****")
                views.setImageViewResource(
                    R.id.widget_toggle_visibility_icon,
                    R.drawable.ic_eye_open_white     // 👁️ abierto
                )
            }

            // Actualizar el widget (desde el hilo de la corrutina)
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
