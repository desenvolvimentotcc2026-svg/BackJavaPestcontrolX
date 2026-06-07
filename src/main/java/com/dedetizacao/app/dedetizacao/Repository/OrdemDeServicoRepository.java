package com.dedetizacao.app.dedetizacao.Repository;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrdemDeServicoRepository extends JpaRepository<OrdemDeServico, Long> {

    List<OrdemDeServico> findByEmpresaIdOrderByIdDesc(Long empresaId);

    List<OrdemDeServico> findByClienteIdOrderByIdDesc(Long clienteId);

    @Query("SELECT o FROM OrdemDeServico o WHERE o.funcionario = :funcionario ORDER BY o.id DESC")
    List<OrdemDeServico> findByFuncionarioId(@Param("funcionario") String funcionario);

    @Query("SELECT o FROM OrdemDeServico o WHERE LOWER(COALESCE(o.status, 'PENDENTE')) = LOWER(:status) ORDER BY o.id DESC")
    List<OrdemDeServico> findByStatus(@Param("status") String status);

    @Query("SELECT o FROM OrdemDeServico o WHERE o.clienteId = :clienteId AND LOWER(COALESCE(o.status, 'PENDENTE')) <> 'finalizada' ORDER BY o.id DESC")
    List<OrdemDeServico> findAtivasByClienteId(@Param("clienteId") Long clienteId);

    @Query("SELECT o FROM OrdemDeServico o WHERE o.empresaId = :empresaId AND LOWER(COALESCE(o.status, 'PENDENTE')) <> 'finalizada' ORDER BY o.id DESC")
    List<OrdemDeServico> findAtivasByEmpresaId(@Param("empresaId") Long empresaId);
}