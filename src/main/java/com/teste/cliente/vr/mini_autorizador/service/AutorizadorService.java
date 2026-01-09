package com.teste.cliente.vr.mini_autorizador.service;

import com.teste.cliente.vr.mini_autorizador.domain.Cartao;
import com.teste.cliente.vr.mini_autorizador.dto.CartaoDTO;
import com.teste.cliente.vr.mini_autorizador.dto.TransacaoDTO;
import com.teste.cliente.vr.mini_autorizador.exception.BusinessException;
import com.teste.cliente.vr.mini_autorizador.exception.CartaoJaExisteException;
import com.teste.cliente.vr.mini_autorizador.repository.CartaoRepository;
import com.teste.cliente.vr.mini_autorizador.service.strategy.ValidadorTransacao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AutorizadorService {

    private final CartaoRepository repository;
    private final List<ValidadorTransacao> validadores;

    @Transactional
    public CartaoDTO criarCartao(CartaoDTO dto) {
        repository.findById(dto.getNumeroCartao()).ifPresent(c -> {
            throw new CartaoJaExisteException(dto);
        });

        Cartao novoCartao = new Cartao(dto.getNumeroCartao(), dto.getSenha());
        repository.save(novoCartao);
        return dto;
    }

    public Optional<BigDecimal> obterSaldo(String numeroCartao) {
        return repository.findById(numeroCartao).map(Cartao::getSaldo);
    }

    @Transactional
    public void processarTransacao(TransacaoDTO dto) {
        Cartao cartao = repository.findByNumeroCartaoComLock(dto.getNumeroCartao())
                .orElseThrow(() -> new BusinessException("CARTAO_INEXISTENTE"));

        validadores.forEach(v -> v.validar(cartao, dto));

        cartao.setSaldo(cartao.getSaldo().subtract(dto.getValor()));
        repository.save(cartao);
    }
}