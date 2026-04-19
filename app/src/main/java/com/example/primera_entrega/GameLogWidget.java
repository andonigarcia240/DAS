package com.example.primera_entrega;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameLogWidget extends AppWidgetProvider {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager,
                                    int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_gamelog);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.tvWidgetJugando, pendingIntent);

        SessionManager sessionManager = new SessionManager(context);

        if (!sessionManager.isLoggedIn()) {
            views.setTextViewText(R.id.tvWidgetUsername, "Sin sesión");
            views.setTextViewText(R.id.tvWidgetActualizado, "Inicia sesión en la app");
            appWidgetManager.updateAppWidget(appWidgetId, views);
            return;
        }

        // Mostrar nombre de usuario
        views.setTextViewText(R.id.tvWidgetUsername, sessionManager.getUsername());
        appWidgetManager.updateAppWidget(appWidgetId, views);

        executor.execute(() -> {
            try {
                String response = ApiClient.getJuegos(sessionManager.getUserId());
                JSONObject json = new JSONObject(response);

                int jugando = 0, completados = 0, abandonados = 0;

                if (json.getBoolean("success")) {
                    JSONArray array = json.getJSONArray("juegos");
                    for (int i = 0; i < array.length(); i++) {
                        String estado = array.getJSONObject(i).getString("estado");
                        switch (estado) {
                            case "Jugando": jugando++; break;
                            case "Completado": completados++; break;
                            case "Abandonado": abandonados++; break;
                        }
                    }
                }

                final int j = jugando, c = completados, a = abandonados;
                String hora = new SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(new Date());

                views.setTextViewText(R.id.tvWidgetJugando, String.valueOf(j));
                views.setTextViewText(R.id.tvWidgetCompletado, String.valueOf(c));
                views.setTextViewText(R.id.tvWidgetAbandonado, String.valueOf(a));
                views.setTextViewText(R.id.tvWidgetActualizado, "Actualizado: " + hora);

                appWidgetManager.updateAppWidget(appWidgetId, views);

            } catch (Exception e) {
                views.setTextViewText(R.id.tvWidgetActualizado, "Error de conexión");
                appWidgetManager.updateAppWidget(appWidgetId, views);
            }
        });
    }

    // Método estático para forzar actualización desde fuera
    public static void forceUpdate(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName widget = new ComponentName(context, GameLogWidget.class);
        int[] ids = manager.getAppWidgetIds(widget);
        for (int id : ids) {
            updateWidget(context, manager, id);
        }
    }
}