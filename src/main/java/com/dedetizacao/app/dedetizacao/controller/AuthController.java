package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Dto.LoginRequest;
import com.dedetizacao.app.dedetizacao.Dto.ClienteDto;
import com.dedetizacao.app.dedetizacao.Model.Usuario;
import com.dedetizacao.app.dedetizacao.Model.TipoUsuario;
import com.dedetizacao.app.dedetizacao.Service.*;
import com.dedetizacao.app.dedetizacao.security.JwtService;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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
    private final EmailService emailService; // INJETADO

    // Construtor com EmailService incluído
    public AuthController(
            UsuarioService usuarioService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            EmpresaService empresaService,
            ClienteService clienteService,
            FuncionarioService funcionarioService,
            EmailService emailService
    ) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.empresaService = empresaService;
        this.clienteService = clienteService;
        this.funcionarioService = funcionarioService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(req.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Usuário não encontrado"));
        }

        Usuario user = userOpt.get();

        if (!passwordEncoder.matches(req.getSenha(), user.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Senha inválida"));
        }

        String token = jwtService.gerarToken(user.getEmail());

        // Login retorna apenas o token e dados do usuário. Nada de tokens de cadastro aqui!
        return ResponseEntity.ok(Map.of(
                "token", token,
                "tipo", user.getTipo().name(),
                "id", user.getId(),
                "nome", user.getNome()
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (usuarioService.buscarPorEmail(req.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Email já cadastrado"));
        }

        Usuario user = new Usuario();
        user.setNome(req.getNome());
        user.setEmail(req.getEmail());
        user.setSenha(passwordEncoder.encode(req.getSenha()));
        user.setTipo(TipoUsuario.valueOf(req.getTipo()));

        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setCodigoVerificacao(token)

        Usuario salvo = usuarioService.salvar(user);


        // Conversão segura do CNPJ/ID para funcionário
        Long empresaId = 0L;
        if (req.getCnpj() != null && !req.getCnpj().isEmpty()) {
            try {
                empresaId = Long.valueOf(req.getCnpj().replaceAll("[^0-9]", ""));
            } catch (NumberFormatException ignored) {}
        }

        // Lógica de salvamento das entidades específicas
        if (user.getTipo() == TipoUsuario.EMPRESA) {
            empresaService.salvarFromRegister(req, salvo.getId());
        } else if (user.getTipo() == TipoUsuario.CLIENTE) {
            ClienteDto dto = new ClienteDto();
            dto.setNome(req.getNome());
            dto.setEmail(req.getEmail());
            dto.setTelefone("Não informado");
            clienteService.criarFromDto(dto, null);
        } else if (user.getTipo() == TipoUsuario.FUNCIONARIO) {
            funcionarioService.criarFromRegister(req, empresaId);
        }

        try {
            emailService.enviarCodigo(salvo.getEmail(), "Seu Código de Verificação", "Seu código é: " + token);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Cadastro realizado com sucesso!",
                        "token", token // Deixei aqui pra facilitar o teste na apresentação
                ));
    }
}