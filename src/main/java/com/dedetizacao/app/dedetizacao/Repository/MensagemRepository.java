package com.dedetizacao.app.dedetizacao.Repository;

import com.dedetizacao.app.dedetizacao.Model.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {

    // Usado no ChatController (Abordagem Empresa/Cliente)
    List<Mensagem> findByEmpresaIdAndClienteIdOrderByDataHoraAsc(Long empresaId, Long clienteId);

    // Usado no MensagemService (Abordagem Genérica de Histórico Bidirecional)
    @Query("SELECT m FROM Mensagem m WHERE (m.remetenteId = :user1 AND m.destinatarioId = :user2) " +
            "OR (m.remetenteId = :user2 AND m.destinatarioId = :user1) ORDER BY m.dataHora ASC")
    List<Mensagem> buscarHistoricoConversa(@Param("user1") Long user1, @Param("user2") Long user2);

    // Usado no MensagemService para contar interações e ativar o Auto-Bot
    long countByRemetenteIdAndDestinatarioId(Long remetenteId, Long destinatarioId);

    List<Mensagem> findByEmpresaIdAndClienteId(Long empresaId, Long clienteId);
}