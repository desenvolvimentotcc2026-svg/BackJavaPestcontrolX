package com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Dto.AgendamentoDto;
import com.dedetizacao.app.dedetizacao.Model.Agendamento;
import com.dedetizacao.app.dedetizacao.Model.Cliente;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Repository.AgendamentoRepository;
import com.dedetizacao.app.dedetizacao.Repository.ClienteRepository;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import com.dedetizacao.app.dedetizacao.Repository.OrdemDeServicoRepository;
import org.springframework.stereotype.Service;


@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final OrdemDeServicoRepository ordemRepository;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            ClienteRepository clienteRepository,
            FuncionarioRepository funcionarioRepository,
            OrdemDeServicoRepository ordemRepository) {

        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository = clienteRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.ordemRepository = ordemRepository;
    }

    public Agendamento criar(AgendamentoDto dto) {

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Funcionario funcionario = funcionarioRepository.findById(dto.getFuncionarioId())
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        OrdemDeServico ordem = ordemRepository.findById(dto.getOrdemId())
                .orElseThrow(() -> new RuntimeException("OS não encontrada"));

        Agendamento agendamento = new Agendamento();

        agendamento.setCliente(cliente);
        agendamento.setFuncionario(funcionario);
        agendamento.setServicoId(dto.getServicoId());
        agendamento.setData(dto.getData());

        Agendamento salvo = agendamentoRepository.save(agendamento);

        ordem.setDataAgendamento(dto.getData().toString());

        ordem.setStatus("AGENDADA");

        ordemRepository.save(ordem);

        return salvo;
    }
}