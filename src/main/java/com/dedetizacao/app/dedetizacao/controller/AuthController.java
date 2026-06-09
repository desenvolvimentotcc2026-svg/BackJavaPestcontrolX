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

    // ETAPA 1: O usuário digita e-mail e senha. Se estiver correto, gera o código e manda por e-mail.
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

        // 1. Gera um NOVO código de verificação para este login
        String novoTokenLogin = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setCodigoVerificacao(novoTokenLogin);
        usuarioService.salvar(user); // Grava no banco substituindo o antigo

        // 2. Dispara o e-mail com o código gerado na hora
        try {
            emailService.enviarCodigo(user.getEmail(), "Código de Acesso - PestControlX", "Seu código de verificação para entrar é: " + novoTokenLogin);
        } catch (Exception e) {
            System.err.println("Erro ao disparar e-mail de login: " + e.getMessage());
        }

        // 3. Retorna apenas o aviso para o Front-end exibir a tela do código (NÃO manda o JWT ainda!)
        return ResponseEntity.ok(Map.of(
                "message", "Código de verificação enviado ao seu e-mail.",
                "requiresVerification", true,
                "email", user.getEmail()
        ));
    }

    // ETAPA 2: O usuário digita o código recebido no e-mail. Se bater, o sistema libera o JWT!
    @PostMapping("/verify-login")
    public ResponseEntity<?> verifyLogin(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String codigo = req.get("codigo");

        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuário não encontrado"));
        }

        Usuario user = userOpt.get();

        // Valida se o código enviado bate com o que acabamos de salvar no login
        if (user.getCodigoVerificacao() == null || !user.getCodigoVerificacao().equals(codigo)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Código de verificação inválido ou expirado"));
        }

        // Limpa o código do banco pois ele já usou para entrar
        user.setCodigoVerificacao(null);
        usuarioService.salvar(user);

        // Gera o Token JWT de acesso definitivo
        String tokenJwt = jwtService.gerarToken(user.getEmail());

        // Retorna os dados completos do login de sucesso
        return ResponseEntity.ok(Map.of(
                "token", tokenJwt,
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
            dto.setTelefone("Não informado");
            clienteService.criarFromDto(dto, null);
        } else if (user.getTipo() == TipoUsuario.FUNCIONARIO) {
            funcionarioService.criarFromRegister(req, empresaId);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Cadastro realizado com sucesso! Agora você já pode fazer login para receber seu primeiro código."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "E-mail não encontrado em nosso sistema"));
        }

        Usuario user = userOpt.get();

        String codigoRecuperacao = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setCodigoVerificacao(codigoRecuperacao);
        usuarioService.salvar(user);

        try {
            emailService.enviarCodigo(user.getEmail(), "Recuperação de Senha - PestControlX",
                    "Você solicitou a alteração de senha. Seu código de recuperação é: " + codigoRecuperacao);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de recuperação: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("message", "Código de recuperação enviado para o seu e-mail!"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String codigo = req.get("codigo");
        String novaSenha = req.get("novaSenha");

        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuário não encontrado"));
        }

        Usuario user = userOpt.get();

        if (user.getCodigoVerificacao() == null || !user.getCodigoVerificacao().equals(codigo)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Código de verificação inválido ou expirado"));
        }

        user.setSenha(passwordEncoder.encode(novaSenha));
        user.setCodigoVerificacao(null);
        usuarioService.salvar(user);

        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso! Agora você já pode fazer login."));
    }
}