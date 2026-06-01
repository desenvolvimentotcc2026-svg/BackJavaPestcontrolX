package com.dedetizacao.app.dedetizacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.dedetizacao.app.dedetizacao", "com.example.pestcontrolx"})
@EnableAsync // Habilita o envio de e-mail sem travar a requisição de login
public class AppDedetizacaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppDedetizacaoApplication.class, args);
	}
}