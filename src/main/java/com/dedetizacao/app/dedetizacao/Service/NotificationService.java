package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void sendPushNotification(String targetFcmToken, String title, String body) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setToken(targetFcmToken)
                    .setNotification(notification)
                    .build();

            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
            System.out.println("Notificacao enviada com sucesso. ID: " + response);
        } catch (Exception e) {
            System.err.println("Falha ao enviar push notification: " + e.getMessage());
        }
    }

    public void dispararMudancaStatus(OrdemDeServico ordem) {
        if (ordem != null) {
            System.out.println("Mudanca de status OS #" + ordem.getId() + ": " + ordem.getStatus());
        }
    }
}