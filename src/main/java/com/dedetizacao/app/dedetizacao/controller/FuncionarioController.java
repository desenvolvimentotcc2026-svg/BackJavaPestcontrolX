package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import com.dedetizacao.app.dedetizacao.Service.FuncionarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
@CrossOrigin(origins = "*")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioRepository funcionarioRepository, FuncionarioService funcionarioService){
        this.funcionarioRepository = funcionarioRepository;
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public List<FuncionarioDto> listartodos() {
        return funcionarioService.listarTodos()
                .stream()
                .map(f -> funcionarioService.toDTO(f))
                .toList();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable long id){
        // Validação prévia profissional para evitar falsos retornos 404
        if (!funcionarioRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }

        funcionarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<FuncionarioDto> criar(@RequestBody FuncionarioDto dto,
                                                @RequestParam(required = false) Long empresa_id) {
        Funcionario criado = funcionarioService.salvar(dto, empresa_id);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioService.toDTO(criado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioDto> atualizar(@PathVariable Long id, @RequestBody FuncionarioDto dto){
        Funcionario atualizado = funcionarioService.atualizar(id, dto);
        return ResponseEntity.ok(funcionarioService.toDTO(atualizado));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody String novoStatus) {
        funcionarioService.atualizarStatus(id, novoStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/online")
    public List<FuncionarioDto> listarOnline() {
        return funcionarioService.listarPorStatus("ONLINE")
                .stream()
                .map(f -> funcionarioService.toDTO(f))
                .toList();
    }
}