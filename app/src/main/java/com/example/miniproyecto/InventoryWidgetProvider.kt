package com.example.miniproyecto

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class InventoryWidgetProvider : AppWidgetProvider() {

    private var isBalanceVisible = false
    private var balanceAmount: String = "$ 3.326.00,00" // Ejemplo, luego conecta con Room

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, widgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_inventory)

        // Mostrar saldo u ocultar
        if (isBalanceVisible) {
            views.setTextViewText(R.id.widget_balance, balanceAmount)
            views.setImageViewResource(R.id.widget_eye_icon, R.drawable.ic_eye_closed)
        } else {
            views.setTextViewText(R.id.widget_balance, "$ ****")
            views.setImageViewResource(R.id.widget_eye_icon, R.drawable.ic_eye_open)
        }

        // Clic en el ojo para mostrar/ocultar
        val eyeIntent = Intent(context, InventoryWidgetProvider::class.java).apply {
            action = "TOGGLE_BALANCE"
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val eyePendingIntent = PendingIntent.getBroadcast(
            context, appWidgetId, eyeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_eye_icon, eyePendingIntent)

        // Clic en "Gestionar inventario", abrir Login
        val manageIntent = Intent(context, LoginActivity::class.java) // Remplaza por tu actividad de login real
        val managePendingIntent = PendingIntent.getActivity(
            context, 0, manageIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_manage_icon, managePendingIntent)
        views.setOnClickPendingIntent(R.id.widget_manage_label, managePendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == "TOGGLE_BALANCE" && context != null) {
            isBalanceVisible = !isBalanceVisible
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}


