package com.dedetizacao.app.dedetizacao.Repository;

import com.dedetizacao.app.dedetizacao.Model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    void deleteById(Servico servico);
}