package com.teste.cliente.vr.mini_autorizador.service.strategy;

import com.teste.cliente.vr.mini_autorizador.domain.Cartao;
import com.teste.cliente.vr.mini_autorizador.dto.TransacaoDTO;
import com.teste.cliente.vr.mini_autorizador.exception.BusinessException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ValidadorSenha implements ValidadorTransacao {
    @Override
    public void validar(Cartao cartao, TransacaoDTO transacao) {
        if (!cartao.getSenha().equals(transacao.getSenhaCartao())) {
            throw new BusinessException("SENHA_INVALIDA");
        }
    }
}