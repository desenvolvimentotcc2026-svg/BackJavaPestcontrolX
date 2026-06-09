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
    private final EmailService emailService;

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
                    .body(Map.of("message", "Usuario nao encontrado"));
        }

        Usuario user = userOpt.get();

        if (!passwordEncoder.matches(req.getSenha(), user.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Senha invalida"));
        }

        // 🔒 SEGURANÇA: Se o usuário tem um código de verificação ativo no banco,
        // significa que ele acabou de se cadastrar e ainda NÃO validou a conta por e-mail.
        if (user.getCodigoVerificacao() != null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Por favor, verifique sua conta com o codigo enviado ao seu e-mail antes de fazer login."));
        }

        String token = jwtService.gerarToken(user.getEmail());

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
                    .body(Map.of("message", "Email ja cadastrado"));
        }

        Usuario user = new Usuario();
        user.setNome(req.getNome());
        user.setEmail(req.getEmail());
        user.setSenha(passwordEncoder.encode(req.getSenha()));
        user.setTipo(TipoUsuario.valueOf(req.getTipo()));

        // Gera e define o token de verificação inicial
        String token = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setCodigoVerificacao(token);

        Usuario salvo = usuarioService.salvar(user);

        Long empresaId = 0L;
        if (req.getCnpj() != null && !req.getCnpj().isEmpty()) {
            try {
                empresaId = Long.valueOf(req.getCnpj().replaceAll("[^0-9]", ""));
            } catch (NumberFormatException ignored) {}
        }

        if (user.getTipo() == TipoUsuario.EMPRESA) {
            empresaService.salvarFromRegister(req, salvo.getId());
        } else if (user.getTipo() == TipoUsuario.CLIENTE) {
            ClienteDto dto = new ClienteDto();
            dto.setNome(req.getNome());
            dto.setEmail(req.getEmail());
            dto.setTelefone("Nao informado");
            clienteService.criarFromDto(dto, null);
        } else if (user.getTipo() == TipoUsuario.FUNCIONARIO) {
            funcionarioService.criarFromRegister(req, empresaId);
        }

        // Envia o e-mail com o token gerado
        try {
            emailService.enviarCodigo(salvo.getEmail(), "Seu Codigo de Verificacao - PestControlX", "Seu codigo e: " + token);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Cadastro realizado com sucesso! Verifique seu e-mail.",
                        "token", token
                ));
    }

    // 🔥 NOVO ENDPOINT: CONFIRMAR O CÓDIGO DO CADASTRO (Ativar a Conta)
    @PostMapping("/verify-account")
    public ResponseEntity<?> verifyAccount(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String codigo = req.get("codigo");

        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario nao encontrado"));
        }

        Usuario user = userOpt.get();

        if (user.getCodigoVerificacao() == null || !user.getCodigoVerificacao().equals(codigo)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Codigo de verificacao invalido"));
        }

        // Limpa o código do banco de dados, marcando a conta como VERIFICADA e ATIVA
        user.setCodigoVerificacao(null);
        usuarioService.salvar(user);

        return ResponseEntity.ok(Map.of("message", "Conta verificada com sucesso! Agora voce ja pode realizar o login."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "E-mail nao encontrado em nosso sistema"));
        }

        Usuario user = userOpt.get();

        String codigoRecuperacao = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setCodigoVerificacao(codigoRecuperacao);
        usuarioService.salvar(user);

        try {
            emailService.enviarCodigo(user.getEmail(), "Recuperacao de Senha - PestControlX",
                    "Voce solicitou a alteracao de senha. Seu codigo de recuperacao e: " + codigoRecuperacao);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de recuperacao: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("message", "Codigo de recuperacao enviado para o seu e-mail!"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String codigo = req.get("codigo");
        String novaSenha = req.get("novaSenha");

        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario nao encontrado"));
        }

        Usuario user = userOpt.get();

        if (user.getCodigoVerificacao() == null || !user.getCodigoVerificacao().equals(codigo)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Codigo de verificacao invalido ou expirado"));
        }

        user.setSenha(passwordEncoder.encode(novaSenha));
        user.setCodigoVerificacao(null);
        usuarioService.salvar(user);

        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso! Agora voce ja pode fazer login."));
    }
}