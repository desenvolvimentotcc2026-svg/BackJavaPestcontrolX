package com.dedetizacao.app.dedetizacao.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final String SECRET = "pestcontrolx-super-chave-jwt-2026-seguranca-32bytes!!";

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String gerarToken(String email) {
        long expiracao = 1000L * 60 * 60 * 24 * 30;

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiracao))
                .signWith(getKey())
                .compact();
    }

    public String extrairEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}