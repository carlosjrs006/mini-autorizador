package com.teste.cliente.vr.mini_autorizador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TransacaoDTO {

    @NotBlank(message = "O número do cartão é obrigatório")
    @Pattern(regexp = "^\\d{16}$", message = "Número do cartão inválido")
    private String numeroCartao;

    @NotBlank(message = "A senha do cartão é obrigatória")
    @Pattern(regexp = "^\\d{4}$", message = "Senha deve conter 4 dígitos")
    private String senhaCartao;

    @NotNull(message = "O valor da transação é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private BigDecimal valor;
}