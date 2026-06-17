package com.dedetizacao.app.dedetizacao.Repository;

import com.dedetizacao.app.dedetizacao.Model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List; 

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    Optional<Cliente> findByEmail(String email);

    List<Cliente> findByEmpresaId(Long empresaId);
}