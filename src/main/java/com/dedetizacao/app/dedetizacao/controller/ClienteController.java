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
    public List<Cliente> listarTodos(){
        return clienteService.listarTodos();
    }

    @PostMapping
    public ClienteDto criar(@RequestBody ClienteDto dto,
                            @PathVariable Long empresaId){

        Cliente cliente = clienteService.salvar(dto, empresaId);

        return clienteService.toDTO(cliente);
    }
    @PutMapping("/{clienteId}")
    public Cliente atualizarCliente(
        @PathVariable Long empresaId,
        @PathVariable Long clienteId,
        @RequestBody Cliente cliente) {

    return clienteService.atualizar(empresaId, clienteId, cliente);
}

    @DeleteMapping("/{clienteId}")
    public void deletarCliente(
        @PathVariable Long empresaId,
        @PathVariable Long clienteId) {

    clienteService.deletar(empresaId, clienteId);
}
}