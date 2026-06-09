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
                    .body(Map.of("message", "Usuário não encontrado"));
        }

        Usuario user = userOpt.get();

        if (!passwordEncoder.matches(req.getSenha(), user.getSenha())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Senha inválida"));
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
                    .body(Map.of("message", "Email já cadastrado"));
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
            dto.setTelefone("Não informado");
            clienteService.criarFromDto(dto, null);
        } else if (user.getTipo() == TipoUsuario.FUNCIONARIO) {
            funcionarioService.criarFromRegister(req, empresaId);
        }

        // Envia o e-mail com o token gerado
        try {
            emailService.enviarCodigo(salvo.getEmail(), "Seu Código de Verificação - PestControlX", "Seu código é: " + token);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Cadastro realizado com sucesso!",
                        "token", token
                ));
    }

    // 🔥 NOVO ENDPOINT: SOLICITAR RECUPERAÇÃO DE SENHA
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "E-mail não encontrado em nosso sistema"));
        }

        Usuario user = userOpt.get();

        // Gera um código de 6 dígitos para recuperação
        String codigoRecuperacao = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setCodigoVerificacao(codigoRecuperacao);
        usuarioService.salvar(user);

        // Dispara o e-mail com o código de recuperação
        try {
            emailService.enviarCodigo(user.getEmail(), "Recuperação de Senha - PestControlX",
                    "Você solicitou a alteração de senha. Seu código de recuperação é: " + codigoRecuperacao);
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de recuperação: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of("message", "Código de recuperação enviado para o seu e-mail!"));
    }

    // 🔥 NOVO ENDPOINT: DEFINIR A NOVA SENHA COM O CÓDIGO
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

        // Valida se o código enviado bate com o salvo no banco
        if (user.getCodigoVerificacao() == null || !user.getCodigoVerificacao().equals(codigo)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Código de verificação inválido ou expirado"));
        }

        // Criptografa e atualiza a senha, limpando o código em seguida
        user.setSenha(passwordEncoder.encode(novaSenha));
        user.setCodigoVerificacao(null);
        usuarioService.salvar(user);

        return ResponseEntity.ok(Map.of("message", "Senha alterada com sucesso! Agora você já pode fazer login."));
    }
}