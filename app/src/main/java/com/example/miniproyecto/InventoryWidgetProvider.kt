package com.example.miniproyecto

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.DecimalFormat

class InventoryWidgetProvider : AppWidgetProvider() {

    companion object {
        private const val ACTION_TOGGLE_BALANCE = "com.example.miniproyecto.TOGGLE_BALANCE"
        private const val PREFS_NAME = "inventory_widget_prefs"
        private const val KEY_BALANCE_VISIBLE = "balance_visible"
    }

    // Lista base que debería ser la misma lógica de tu inventario
    private val productosDemo = listOf(
        Producto("Arroz", "123", 3326.00, 10),
        Producto("Frijoles", "124", 12000.00, 5)
    )

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Se actualiza cada widget activo
        for (widgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        // MUY IMPORTANTE: primero llama al padre correctamente
        super.onReceive(context, intent)

        // Si el usuario presiona el icono del ojo (toggle)
        if (intent.action == ACTION_TOGGLE_BALANCE) {

            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val visible = prefs.getBoolean(KEY_BALANCE_VISIBLE, false)
            prefs.edit().putBoolean(KEY_BALANCE_VISIBLE, !visible).apply()

            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = appWidgetManager.getAppWidgetIds(
                ComponentName(context, InventoryWidgetProvider::class.java)
            )

            // Vuelve a dibujar todos los widgets
            for (id in ids) {
                updateWidget(context, appWidgetManager, id)
            }
        }
    }

    private fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isVisible = prefs.getBoolean(KEY_BALANCE_VISIBLE, false)

        val saldoGeneral = getSaldoGeneral()

        if (isVisible) {
            views.setTextViewText(R.id.widget_balance, saldoGeneral)
            views.setImageViewResource(R.id.widget_eye_icon, R.drawable.ic_eye_closed)
        } else {
            views.setTextViewText(R.id.widget_balance, "$ ****")
            views.setImageViewResource(R.id.widget_eye_icon, R.drawable.ic_eye_open)
        }

        // Toggle ojo (mostrar/ocultar)
        val toggleIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_BALANCE
        }

        val togglePending = PendingIntent.getBroadcast(
            context,
            0,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_eye_icon, togglePending)

        // Ir al login (gestionar inventario)
        val manageIntent = Intent(context, LoginActivity::class.java)
        val managePending = PendingIntent.getActivity(
            context,
            1,
            manageIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_manage_icon, managePending)
        views.setOnClickPendingIntent(R.id.widget_manage_label, managePending)

        manager.updateAppWidget(widgetId, views)
    }

    private fun getSaldoGeneral(): String {
        val total = productosDemo.sumOf { it.precioUnitario * it.cantidad }
        val df = DecimalFormat("#,###,##0.00")
        return "$ " + df.format(total).replace(",", ".")
    }
}
