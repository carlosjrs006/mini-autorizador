package com.teste.cliente.vr.mini_autorizador.automatizado;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teste.cliente.vr.mini_autorizador.domain.Cartao;
import com.teste.cliente.vr.mini_autorizador.dto.CartaoDTO;
import com.teste.cliente.vr.mini_autorizador.dto.TransacaoDTO;
import com.teste.cliente.vr.mini_autorizador.repository.CartaoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MiniAutorizadorApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartaoRepository cartaoRepository;

    private final String NUMERO_CARTAO = "6549873025634501";
    private final String SENHA_CARTAO = "1234";

    @BeforeAll
    static void setup(@Autowired CartaoRepository repository) {
        repository.deleteAll();
    }

    @Test
    @Order(1)
    void deveCriarNovoCartaoComSucesso() throws Exception {
        CartaoDTO dto = new CartaoDTO(NUMERO_CARTAO, SENHA_CARTAO);

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    @Order(2)
    void deveVerificarSaldoDoCartaoRecemCriado() throws Exception {
        // Garantir que o cartão existe para este teste específico
        cartaoRepository.save(new Cartao(NUMERO_CARTAO, SENHA_CARTAO));

        mockMvc.perform(get("/cartoes/" + NUMERO_CARTAO))
                .andExpect(status().isOk())
                .andExpect(content().string("500.00"));
    }
    @Test
    @Order(3)
    void deveRealizarTransacoesAteSaldoInsuficiente() throws Exception {
        TransacaoDTO transacao = new TransacaoDTO(NUMERO_CARTAO, SENHA_CARTAO, new BigDecimal("250.00"));

        // Primeira transação de 250 (sobra 250)
        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacao)))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));

        // Segunda transação de 250 (sobra 0)
        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacao)))
                .andExpect(status().isCreated())
                .andExpect(content().string("OK"));

        // Terceira transação deve falhar por saldo insuficiente
        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacao)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SALDO_INSUFICIENTE"));
    }

    @Test
    @Order(4)
    void deveFalharAoRealizarTransacaoComSenhaInvalida() throws Exception {
        TransacaoDTO transacaoInvalida = new TransacaoDTO(NUMERO_CARTAO, "9999", new BigDecimal("10.00"));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacaoInvalida)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("SENHA_INVALIDA"));
    }

    @Test
    @Order(5)
    void deveFalharAoRealizarTransacaoComCartaoInexistente() throws Exception {
        TransacaoDTO transacaoInexistente = new TransacaoDTO("0000000000000000", "1234", new BigDecimal("10.00"));

        mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transacaoInexistente)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("CARTAO_INEXISTENTE"));
    }

    @Test
    @Order(6)
    void deveGarantirConsistenciaEmTransacoesSimultaneas() throws Exception {
        // 1. Criar um cartão com saldo inicial de 500
        String numero = "1111222233334444";
        String senha = "1234";
        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CartaoDTO(numero, senha))))
                .andExpect(status().isCreated());

        // 2. Definir o valor da transação como 500 (todo o saldo)
        TransacaoDTO transacao = new TransacaoDTO(numero, senha, new BigDecimal("500.00"));
        String jsonTransacao = objectMapper.writeValueAsString(transacao);

        // 3. Disparar 2 requisições ao mesmo tempo usando threads paralelas
        CompletableFuture<MvcResult> req1 = CompletableFuture.supplyAsync(() -> {
            try {
                return mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTransacao)).andReturn();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        CompletableFuture<MvcResult> req2 = CompletableFuture.supplyAsync(() -> {
            try {
                return mockMvc.perform(post("/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonTransacao)).andReturn();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Aguarda ambas terminarem
        CompletableFuture.allOf(req1, req2).join();

        int status1 = req1.get().getResponse().getStatus();
        int status2 = req2.get().getResponse().getStatus();

        // 4. VALIDAÇÃO: Uma deve ter sucesso (201) e a outra deve falhar por saldo (422)
        boolean umaSucedidaEUmaFalha = (status1 == 201 && status2 == 422) || (status1 == 422 && status2 == 201);

        assertTrue(umaSucedidaEUmaFalha, "Apenas uma das transações deveria ter sido aprovada.");

        // 5. Verificar que o saldo final é exatamente 0.00
        mockMvc.perform(get("/cartoes/" + numero))
                .andExpect(status().isOk())
                .andExpect(content().string("0.00"));
    }
}