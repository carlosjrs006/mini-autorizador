package com.teste.cliente.vr.mini_autorizador.repository;

import com.teste.cliente.vr.mini_autorizador.domain.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<Cartao, String> {

}
