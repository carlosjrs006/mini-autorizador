package com.teste.cliente.vr.mini_autorizador.service.strategy;

import com.teste.cliente.vr.mini_autorizador.domain.Cartao;
import com.teste.cliente.vr.mini_autorizador.dto.TransacaoDTO;
import com.teste.cliente.vr.mini_autorizador.exception.BusinessException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Order(1)
public class ValidadorSenha implements ValidadorTransacao {
    @Override
    public void validar(Cartao cartao, TransacaoDTO transacao) {
        Optional.of(cartao.getSenha())
                .filter(senha -> senha.equals(transacao.getSenhaCartao()))
                .orElseThrow(() -> new BusinessException("SENHA_INVALIDA"));
    }
}