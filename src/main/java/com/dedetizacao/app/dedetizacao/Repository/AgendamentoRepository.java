package com.dedetizacao.app.dedetizacao.Repository;

import com.dedetizacao.app.dedetizacao.Model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
}