package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Service.FuncionarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/funcionarios") // 🟢 Alinhado com o padrão /api do ecossistema
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final FuncionarioService funcionarioService;

    // 🟢 Repositório removido daqui para respeitar o isolamento de camadas (Controller -> Service -> Repository)
    public FuncionarioController(FuncionarioService funcionarioService) {
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public ResponseEntity<List<FuncionarioDto>> listarTodos() {
        List<FuncionarioDto> funcionarios = funcionarioService.listarTodos()
                .stream()
                .map(funcionarioService::toDTO)
                .toList();
        return ResponseEntity.ok(funcionarios);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        // 🟢 Toda a lógica de verificação de existência e deleção agora fica dentro do Service
        try {
            funcionarioService.deletarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<FuncionarioDto> criar(@RequestBody FuncionarioDto dto,
                                                @RequestParam(required = false, name = "empresa_id") Long empresaId) {
        // 🟢 Padronizado internamente para camelCase, mas aceitando o parâmetro 'empresa_id' vindo do Mobile
        Funcionario criado = funcionarioService.salvar(dto, empresaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioService.toDTO(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDto> atualizar(@PathVariable Long id, @RequestBody FuncionarioDto dto) {
        Funcionario atualizado = funcionarioService.atualizar(id, dto);
        return ResponseEntity.ok(funcionarioService.toDTO(atualizado));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody String novoStatus) {
        // 🟢 Evita problemas de aspas extras enviadas por requisições HTTP text/plain ou JSON puras
        String statusLimpo = novoStatus.replace("\"", "").trim();
        funcionarioService.atualizarStatus(id, statusLimpo);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/online")
    public ResponseEntity<List<FuncionarioDto>> listarOnline() {
        List<FuncionarioDto> online = funcionarioService.listarPorStatus("ONLINE")
                .stream()
                .map(funcionarioService::toDTO)
                .toList();
        return ResponseEntity.ok(online);
    }
}