package com.dedetizacao.app.dedetizacao.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.Base64;

@Service
public class JwtService {

    private final String SECRET = "pestcontrolx-super-chave-jwt-2026-seguranca";

    private Key getKey(){
        return Keys.hmacShaKeyFor(Base64.getEncoder().encode(SECRET.getBytes()));
    }

    public String gerarToken(String email){
        // 30 dias de validade em milissegundos (O 'L' é obrigatório no Java para tipo Long)
        long tempoExpiracao = 2592000000L;

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tempoExpiracao))
                .signWith(getKey())
                .compact();
    }

    public String extrairEmail(String token){
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}