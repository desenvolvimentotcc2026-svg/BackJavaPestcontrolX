package com.dedetizacao.app.dedetizacao.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void enviarCodigo(String email, String assunto, String textoMensagem) {
        // LOG CRITICAL: Garante que o código apareça no Render mesmo se o Gmail falhar
        System.out.println("==================================================");
        System.out.println("PESTCONTROLX DEBUG LOG");
        System.out.println("DESTINO: " + email);
        System.out.println("MENSAGEM: " + textoMensagem);
        System.out.println("==================================================");

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom("pestcontrolx2026@gmail.com");

            message.setTo(email);
            message.setSubject(assunto);
            message.setText(textoMensagem);

            mailSender.send(message);
            System.out.println("SMTP SUCCESS: E-mail enviado para o Brevo com sucesso!");
        } catch (Exception e) {
            System.err.println("SMTP ERROR: " + e.getMessage());
        }
    }
}