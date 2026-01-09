package com.teste.cliente.vr.mini_autorizador.service.strategy;

import com.teste.cliente.vr.mini_autorizador.domain.Cartao;
import com.teste.cliente.vr.mini_autorizador.dto.TransacaoDTO;

public interface ValidadorTransacao {
    void validar(Cartao cartao, TransacaoDTO transacao);
}