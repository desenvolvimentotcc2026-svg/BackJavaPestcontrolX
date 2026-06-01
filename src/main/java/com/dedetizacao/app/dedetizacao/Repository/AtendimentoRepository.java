package com.dedetizacao.app.dedetizacao.Repository;

import com.dedetizacao.app.dedetizacao.Model.Atendimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {
}