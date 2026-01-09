package com.teste.cliente.vr.mini_autorizador.service;

import com.teste.cliente.vr.mini_autorizador.domain.Cartao;
import com.teste.cliente.vr.mini_autorizador.dto.CartaoDTO;
import com.teste.cliente.vr.mini_autorizador.dto.TransacaoDTO;
import com.teste.cliente.vr.mini_autorizador.exception.BusinessException;
import com.teste.cliente.vr.mini_autorizador.exception.CartaoJaExisteException;
import com.teste.cliente.vr.mini_autorizador.repository.CartaoRepository;
import com.teste.cliente.vr.mini_autorizador.service.strategy.ValidadorTransacao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutorizadorService {

    private final CartaoRepository repository;
    private final List<ValidadorTransacao> validadores;

    @Transactional
    public CartaoDTO criarCartao(CartaoDTO dto) {
        log.info("Tentativa de criação de cartão: {}", dto.getNumeroCartao());

        repository.findById(dto.getNumeroCartao())
                .ifPresent(c -> {
                    log.warn("Falha na criação: Cartão {} já existe", dto.getNumeroCartao());
                    throw new CartaoJaExisteException(dto);
                });

        repository.save(new Cartao(dto.getNumeroCartao(), dto.getSenha()));
        log.info("Cartão {} criado com sucesso", dto.getNumeroCartao());
        return dto;
    }

    @Transactional
    public void processarTransacao(TransacaoDTO dto) {
        log.info("Processando transação para o cartão: {}. Valor: {}", dto.getNumeroCartao(), dto.getValor());

        Cartao cartao = repository.findByNumeroCartaoComLock(dto.getNumeroCartao())
                .orElseThrow(() -> {
                    log.error("Transação negada: Cartão {} inexistente", dto.getNumeroCartao());
                    return new BusinessException("CARTAO_INEXISTENTE");
                });

        validadores.forEach(v -> v.validar(cartao, dto));

        cartao.setSaldo(cartao.getSaldo().subtract(dto.getValor()));
        repository.save(cartao);

        log.info("Transação aprovada para o cartão: {}. Novo saldo: {}", dto.getNumeroCartao(), cartao.getSaldo());
    }

    public Optional<BigDecimal> obterSaldo(String numeroCartao) {
        log.debug("Consulta de saldo para o cartão: {}", numeroCartao);
        return repository.findById(numeroCartao).map(Cartao::getSaldo);
    }
}