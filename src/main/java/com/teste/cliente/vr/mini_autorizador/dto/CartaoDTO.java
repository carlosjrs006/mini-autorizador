package com.teste.cliente.vr.mini_autorizador.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartaoDTO {

    @NotBlank(message = "O número do cartão é obrigatório")
    private String numeroCartao;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
}