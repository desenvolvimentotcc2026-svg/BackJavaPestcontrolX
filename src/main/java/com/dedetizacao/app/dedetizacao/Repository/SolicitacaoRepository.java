package com.dedetizacao.app.dedetizacao.Repository;

import com.dedetizacao.app.dedetizacao.Model.OrdemDeServico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitacaoRepository extends JpaRepository<OrdemDeServico, Long> {

    List<OrdemDeServico> findByDataAgendamento(String dataAgendamento);
}