package  com.dedetizacao.app.dedetizacao.Service;

import com.dedetizacao.app.dedetizacao.Exception.ResourceNotFoundException;
import com.dedetizacao.app.dedetizacao.Model.Cliente;
import com.dedetizacao.app.dedetizacao.Model.Empresa;
import com.dedetizacao.app.dedetizacao.Model.Funcionario;
import com.dedetizacao.app.dedetizacao.Model.Servico;
import com.dedetizacao.app.dedetizacao.Repository.ClienteRepository;
import com.dedetizacao.app.dedetizacao.Repository.EmpresaRepository;
import com.dedetizacao.app.dedetizacao.Repository.FuncionarioRepository;
import com.dedetizacao.app.dedetizacao.Repository.ServicoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;

@Service
public class  ServicoService{
    private final EmpresaRepository empresaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ClienteRepository clienteRepository;
    private final ServicoRepository servicoRepository;

    public ServicoService(EmpresaRepository empresaRepository,
                          FuncionarioRepository funcionarioRepository,
                          ClienteRepository clienteRepository,
                          ServicoRepository servicoRepository){

        this.empresaRepository = empresaRepository;
        this.servicoRepository = servicoRepository;
        this.clienteRepository = clienteRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Servico> listarTodos(){
        return servicoRepository.findAll();
    }

    public Servico buscarPorId(Long id)  {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado!"));
    }

    public Servico salvar(Servico servico){

        Long empresaId = servico.getEmpresa().getId();
        Long clienteId = servico.getCliente().getId();
        Long funcionarioId = servico.getFuncionario().getId();

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada"));

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado"));

        if (!Objects.equals(funcionario.getEmpresa().getId(), empresa.getId())) {
            throw new RuntimeException("Funcionário não pertence à empresa informada");
        }

        servico.setEmpresa(empresa);
        servico.setCliente(cliente);
        servico.setFuncionario(funcionario);

        return servicoRepository.save(servico);
    }

    public void deletar(Long id)  {
        Servico servico = buscarPorId(id);
        servicoRepository.deleteById(id);
    }

    public Servico atualizar(Long id, Servico servicoAtualizado)  {
        Servico servico = buscarPorId(id);

        servico.setFuncionario(servicoAtualizado.getFuncionario());
        servico.setDescricao(servicoAtualizado.getDescricao());
        servico.setStatus(servicoAtualizado.getStatus());
        servico.setValor(servicoAtualizado.getValor());
        servico.setCliente(servicoAtualizado.getCliente());

        return servicoRepository.save(servico);
    }


}