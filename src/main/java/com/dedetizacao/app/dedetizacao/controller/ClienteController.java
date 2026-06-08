package com.dedetizacao.app.dedetizacao.controller;
import com.dedetizacao.app.dedetizacao.Dto.ClienteDto;
import com.dedetizacao.app.dedetizacao.Model.Cliente;
import com.dedetizacao.app.dedetizacao.Service.ClienteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas/{empresaId}/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService){
        this.clienteService = clienteService;
    }

    @GetMapping
    public List<ClienteDto> listar() {
        return clienteService.listarTodos()
                .stream()
                .map(c -> clienteService.toDTO(c))
                .toList();
    }

    @PostMapping
    public ClienteDto criar(@RequestBody ClienteDto dto,
                            @PathVariable Long empresaId){

        Cliente cliente = clienteService.salvar(dto, empresaId);

        return clienteService.toDTO(cliente);
    }
    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        return clienteService.atualizar(id, cliente);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        clienteService.deletar(id);
    }
}