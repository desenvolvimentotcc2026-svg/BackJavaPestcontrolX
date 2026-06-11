package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Dto.RegisterRequest;
import com.dedetizacao.app.dedetizacao.Dto.LoginRequest;
import com.dedetizacao.app.dedetizacao.Dto.ClienteDto;
import com.dedetizacao.app.dedetizacao.Model.Usuario;
import com.dedetizacao.app.dedetizacao.Model.TipoUsuario;
import com.dedetizacao.app.dedetizacao.Service.*;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Dto.ValidarEmpresaRequest;
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

        String novoTokenLogin = String.format("%06d", new Random().nextInt(1000000));
        user.setCodigoVerificacao(novoTokenLogin);
        usuarioService.salvar(user);

        try {
            emailService.enviarCodigo(user.getEmail(), "Código de Acesso - PestControlX", "Seu código de verificação para entrar é: " + novoTokenLogin);
        } catch (Exception e) {
            System.err.println("Erro ao disparar e-mail de login: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of(
                "message", "Código de verificação enviado ao seu e-mail.",
                "requiresVerification", true,
                "email", user.getEmail()
        ));
    }

    @PostMapping("/validar")
    public ResponseEntity<?> validarToken(@RequestBody Map<String, String> req) {
        String email = req.get("email");
        String codigo = req.get("codigo") != null ? req.get("codigo") : req.get("code");

        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuário não encontrado"));
        }

        Usuario user = userOpt.get();

        if (codigo != null) {
            codigo = codigo.trim();
        }

        System.out.println("============== DEBUG VERIFY LOGIN ==============");
        System.out.println("👉 Código que está no BANCO: [" + user.getCodigoVerificacao() + "]");
        System.out.println("👉 Código que o FRONT enviou: [" + codigo + "]");
        System.out.println("================================================");

        if (user.getCodigoVerificacao() == null || !user.getCodigoVerificacao().equals(codigo)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Código de verificação inválido ou expirado"));
        }

        // Sucesso: limpa o token usado e gera o JWT de sessão
        user.setCodigoVerificacao(null);
        usuarioService.salvar(user);

        String tokenJwt = jwtService.gerarToken(user.getEmail());

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
                .body(Map.of("message", "Cadastro realizado com sucesso! Faça login para receber o código."));
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

        // Código de recuperação também 100% numérico agora
        String codigoRecuperacao = String.format("%06d", new Random().nextInt(1000000));
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
        String codigo = req.get("codigo") != null ? req.get("codigo") : req.get("code");
        String novaSenha = req.get("novaSenha") != null ? req.get("novaSenha") : req.get("password");

        Optional<Usuario> userOpt = usuarioService.buscarPorEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuário não encontrado"));
        }

        Usuario user = userOpt.get();

        if (codigo != null) {
            codigo = codigo.trim();
        }

        if (user.getCodigoVerificacao() == null || !user.getCodigoVerificacao().equals(codigo)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Código de verificação inválido ou expirado"));
        }

        user.setSenha(passwordEncoder.encode(novaSenha));
        user.setCodigoVerificacao(null);
        usuarioService.salvar(user);

        return ResponseEntity.ok(Map.of("message", "Senha alteredada com sucesso! Agora você já pode fazer login."));
    }

    @PostMapping("/validar-empresa")
    public ResponseEntity<?> validarEmpresa(@RequestBody ValidarEmpresaRequest request) {

        // 1. Limpeza preventiva e proteção contra null vindo do front
        String cnpjInput = request.getCnpj() != null ? request.getCnpj() : "";
        String chaveEnviada = request.getChaveCorporativa() != null ? request.getChaveCorporativa().trim() : "";

        String cnpjLimpo = cnpjInput.replaceAll("[^0-9]", "");

        if (cnpjLimpo.isEmpty() || chaveEnviada.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "CNPJ e Chave corporativa são obrigatórios."));
        }

        Optional<Empresa> empresaOpt = empresaService.buscarPorCnpj(cnpjLimpo);

        if (empresaOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Empresa não encontrada com o CNPJ informado."));
        }

        Empresa empresa = empresaOpt.get();
        
        // Se a empresa no banco estiver com a chave nula, barramos com erro 401 limpo.
        if (empresa.getChaveCorporativa() == null || empresa.getChaveCorporativa().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Esta empresa ainda não possui uma chave corporativa configurada no sistema."));
        }

        String chaveBancoClean = empresa.getChaveCorporativa().trim();

        if (!chaveBancoClean.equals(chaveEnviada)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Chave corporativa inválida para esta empresa."));
        }

        // Sucesso total! Retorna 200 OK para o Android seguir para a MainActivity
        return ResponseEntity.ok().build();
    }
}