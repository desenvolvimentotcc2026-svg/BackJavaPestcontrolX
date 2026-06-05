package com.dedetizacao.app.dedetizacao.Repository;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrdemDeServicoRepository extends JpaRepository<OrdemDeServico, Long> {

    // Busca todas as ordens de uma empresa específica
    List<OrdemDeServico> findByEmpresaId(Long empresaId);

    // Busca todas as ordens de um cliente específico
    List<OrdemDeServico> findByClienteId(Long clienteId);


    @Query("SELECT o FROM OrdemDeServico o WHERE o.funcionario = :funcionarioId") 
    List<OrdemDeServico> findByFuncionarioId(@Param("funcionarioId") Long funcionarioId);
}