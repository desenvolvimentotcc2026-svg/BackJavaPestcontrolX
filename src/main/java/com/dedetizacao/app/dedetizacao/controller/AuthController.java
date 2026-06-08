package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Dto.LoginRequest;
import com.dedetizacao.app.dedetizacao.Model.Usuario;
import com.dedetizacao.app.dedetizacao.Service.*;
import com.dedetizacao.app.dedetizacao.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private final EmpresaService empresaService;
    private final ClienteService clienteService;
    private final FuncionarioService funcionarioService;

    public AuthController(
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmpresaService empresaService,
            ClienteService clienteService,
            FuncionarioService funcionarioService
    ) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.empresaService = empresaService;
        this.clienteService = clienteService;
        this.funcionarioService = funcionarioService;
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuário inválido"));
        }

        Usuario user = userOpt.get();

        if (!passwordEncoder.matches(request.getSenha(), user.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Senha inválida"));
        }

        String token = jwtService.gerarToken(user);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "tipo", user.getTipo(),
                "id", user.getId(),
                "nome", user.getNome()
        ));
    }

    // =========================
    // REGISTER (FLUXO ÚNICO)
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        if (usuarioService.buscarPorEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email já existe"));
        }

        Usuario user = new Usuario();
        user.setNome(req.getNome());
        user.setEmail(req.getEmail());
        user.setSenha(passwordEncoder.encode(req.getSenha()));
        user.setTipo(req.getTipo());

        Usuario salvo = usuarioService.salvar(user);

        // ===== DISPATCH POR TIPO =====
        switch (req.getTipo()) {

            case "EMPRESA":
                empresaService.criar(req, salvo.getId());
                break;

            case "CLIENTE":
                clienteService.criar(req, salvo.getId());
                break;

            case "FUNCIONARIO":
                funcionarioService.criar(req, salvo.getId());
                break;
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Cadastro realizado com sucesso"));
    }
}