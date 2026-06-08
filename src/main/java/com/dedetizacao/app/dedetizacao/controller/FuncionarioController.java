package com.dedetizacao.app.dedetizacao.controller;

import com.dedetizacao.app.dedetizacao.Dto.FuncionarioDto;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import com.dedetizacao.app.dedetizacao.Service.FuncionarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    private final FuncionarioRepository funcionarioRepository;
    private final FuncionarioService funcionarioService;

    public FuncionarioController(FuncionarioRepository funcionarioRepository,  FuncionarioService funcionarioService){
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

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar(@PathVariable long id){
        funcionarioRepository.deleteById(id);

        if (!funcionarioRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
    @PostMapping
    public Funcionario criar(@RequestBody FuncionarioDto dto,
                             @RequestParam Long empresa_id) {
        return funcionarioService.salvar(dto, empresa_id);
    }


    @PutMapping("{id}")
    public Funcionario atualizar(@PathVariable Long id, @RequestBody Funcionario funcionario){
        return funcionarioService.atualizar(id, funcionario);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody String novoStatus) {
        funcionarioService.atualizarStatus(id, novoStatus);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/online")
    public List<Funcionario> listarOnline() {
        return funcionarioService.listarPorStatus("ONLINE");
    }
}