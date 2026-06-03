package com.example.pestcontrolx.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            String jsonConfig = System.getenv("FIREBASE_CONFIG_JSON");

            if (jsonConfig == null || jsonConfig.isEmpty()) {
                System.err.println("ERRO: Variável FIREBASE_CONFIG_JSON não encontrada!");
                return;
            }

            InputStream serviceAccount = new ByteArrayInputStream(jsonConfig.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("====== FIREBASE ADMIN SDK INICIALIZADO VIA VARIÁVEL DE AMBIENTE ======");
            }
        } catch (Exception e) {
            System.err.println("Erro ao inicializar Firebase: " + e.getMessage());
        }
    }
}