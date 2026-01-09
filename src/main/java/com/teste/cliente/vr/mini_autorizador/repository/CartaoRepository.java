package com.teste.cliente.vr.mini_autorizador.repository;

import com.teste.cliente.vr.mini_autorizador.domain.Cartao;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartaoRepository extends JpaRepository<Cartao, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cartao c WHERE c.numeroCartao = :numero")
    Optional<Cartao> findByNumeroCartaoComLock(String numero);
}
