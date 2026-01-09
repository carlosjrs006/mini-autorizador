package com.teste.cliente.vr.mini_autorizador.exception;

import com.teste.cliente.vr.mini_autorizador.dto.CartaoDTO;
import lombok.Getter;

@Getter
public class CartaoJaExisteException extends RuntimeException {
    private final CartaoDTO cartaoDTO;

    public CartaoJaExisteException(CartaoDTO cartaoDTO) {
        this.cartaoDTO = cartaoDTO;
    }
}