package com.dedetizacao.app.dedetizacao.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendPushNotification(String targetFcmToken, String title, String body) {
        try {
            // Cria a estrutura visual da notificação que aparece na barra do Android
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            // Monta a mensagem direcionada para o token específico do aparelho
            Message message = Message.builder()
                    .setToken(targetFcmToken)
                    .setNotification(notification)
                    .build();

            // Envia de forma assíncrona para o Firebase entregar
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            System.out.println("Notificação enviada com sucesso! ID: " + response);

        } catch (Exception e) {
            System.err.println("Falha ao enviar push notification: " + e.getMessage());
        }
    }
}