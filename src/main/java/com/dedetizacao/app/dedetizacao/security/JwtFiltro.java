package com.dedetizacao.app.dedetizacao.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFiltro extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFiltro(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String metodo = request.getMethod();

        if (metodo.equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (path.contains("/ping") || path.contains("/auth") || path.contains("/swagger-ui") || path.contains("/v3/api-docs") || path.contains("/ws-pestcontrol") || path.contains("/ws-pestcontrol-sockjs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        System.out.println("️ [FILTRO JWT] Rota: " + metodo + " " + path);
        System.out.println(" [FILTRO JWT] Header Recebido: " + (authHeader != null ? "SIM (Tamanho: " + authHeader.length() + ")" : "NÃO"));

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();

            try {
                String email = jwtService.extrairEmail(token);
                System.out.println(" [FILTRO JWT] Sucesso! Autenticado como: " + email);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                System.err.println("🚨 [FILTRO JWT] BLOQUEADO: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Token invalido ou expirado! Detalhe: " + e.getMessage() + "\"}");
                return;
            }
        } else {
            System.err.println("⚠️ [FILTRO JWT] Requisição feita sem o padrão correto 'Bearer <token>'.");
        }

        filterChain.doFilter(request, response);
    }
}