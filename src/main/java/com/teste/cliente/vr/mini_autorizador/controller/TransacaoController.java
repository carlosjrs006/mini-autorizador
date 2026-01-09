package com.teste.cliente.vr.mini_autorizador.controller;

import com.teste.cliente.vr.mini_autorizador.dto.TransacaoDTO;
import com.teste.cliente.vr.mini_autorizador.service.AutorizadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
@RequiredArgsConstructor
public class TransacaoController {

    private final AutorizadorService autorizadorService;

    @PostMapping
    public ResponseEntity<String> realizarTransacao(@Valid @RequestBody TransacaoDTO transacaoDTO) {
        autorizadorService.processarTransacao(transacaoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}