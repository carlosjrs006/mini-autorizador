package com.teste.cliente.vr.mini_autorizador.service.strategy;

import com.teste.cliente.vr.mini_autorizador.domain.Cartao;
import com.teste.cliente.vr.mini_autorizador.dto.TransacaoDTO;
import com.teste.cliente.vr.mini_autorizador.exception.BusinessException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2) // Executa após a validação de senha
public class ValidadorSaldo implements ValidadorTransacao {
    @Override
    public void validar(Cartao cartao, TransacaoDTO transacao) {
        if (cartao.getSaldo().compareTo(transacao.getValor()) < 0) {
            throw new BusinessException("SALDO_INSUFICIENTE");
        }
    }
}