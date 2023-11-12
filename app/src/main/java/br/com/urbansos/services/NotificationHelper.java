package br.com.urbansos.services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import org.json.JSONException;
import org.json.JSONObject;
import br.com.urbansos.Main;
import br.com.urbansos.R;
import br.com.urbansos.functions.Functions;
import br.com.urbansos.http.Volley;
import br.com.urbansos.interfaces.IVolleyCallback;

public class NotificationHelper {
    private final String CHANNEL_ID = "urbansos_channel";
    private final int NOTIFICATION_ID = 1;
    private Context mContext;
    private AppCompatActivity activity;

    public NotificationHelper(Context mContext, AppCompatActivity activity) throws JSONException {
        this.mContext = mContext;
        this.activity = activity;

        createNotificationChannel();
    }

    private void createNotificationChannel() throws JSONException
    {
        // Verifique se o dispositivo está executando o Android Oreo ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "UrbanSOS Channel Notifications";
            String description = "Used to trigger report status change notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            // Cria o NotificationChannel
            NotificationChannel channel = new NotificationChannel(this.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);

            // Registra o canal no sistema
            NotificationManager notificationManager = this.activity.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Busca notificações e dispara elas
        getNotifications();
    }

    private void getNotifications() throws JSONException
    {
        Main.requestQueue.add((new Volley()).sendRequestGET("/notification/count/" + (Functions.getCachedAuth()).getString("id"), new IVolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) throws JSONException {
                if (response.length() > 0)
                {
                    JSONObject n = response.getJSONObject("notification0");

                    String countLocal = "0";
                    try
                    {
                        countLocal = (Functions.getCachedNotification()).getString("count");
                    }
                    catch (Exception ex) {}

                    if (! n.getString("count").equals(countLocal) && ! n.getString("count").equals("0"))
                    {
                        sendNotification("Report status have changed", "Access the application to view the progress of your reports");

                        Functions.setCachedNotification(n.getString("count"));
                    }
                }
            }
            @Override
            public void onError(JSONObject response) throws JSONException { }
        }, (Functions.getCachedAuth()).getString("token"), "notification"));
    }

    private void sendNotification(String title, String content)
    {
        // Caso não tenha permissão para enviar notificações cancela
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            return;

        // Crie uma intenção para abrir quando o usuário tocar na notificação
        Intent intent = new Intent(mContext, Main.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, this.CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_urbansos)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridade como alta para exibir como um Heads-up Notification
                .setDefaults(NotificationCompat.DEFAULT_ALL) // Ative som, vibração e luzes
                .setContentIntent(pendingIntent) // Define a intenção para abrir quando tocar na notificação
                .setAutoCancel(true); // Fecha a notificação quando o usuário a toca

        // Exiba a notificação
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);

        // Dispara a notificação
        notificationManager.notify(this.NOTIFICATION_ID, builder.build());
    }
}
