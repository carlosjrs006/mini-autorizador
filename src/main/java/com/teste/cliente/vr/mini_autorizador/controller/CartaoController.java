package com.teste.cliente.vr.mini_autorizador.controller;

import com.teste.cliente.vr.mini_autorizador.dto.CartaoDTO;
import com.teste.cliente.vr.mini_autorizador.exception.CartaoJaExisteException;
import com.teste.cliente.vr.mini_autorizador.service.AutorizadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
@RequiredArgsConstructor
public class CartaoController {

    private final AutorizadorService autorizadorService;

    @PostMapping
    public ResponseEntity<CartaoDTO> criar(@RequestBody @Valid CartaoDTO cartaoDTO) {
        try {
            CartaoDTO novoCartao = autorizadorService.criarCartao(cartaoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(novoCartao);
        } catch (CartaoJaExisteException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(e.getCartaoDTO());
        }
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> obterSaldo(@PathVariable String numeroCartao) {
        return autorizadorService.obterSaldo(numeroCartao)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}