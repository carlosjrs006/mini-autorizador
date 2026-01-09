package com.teste.cliente.vr.mini_autorizador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^\\d{16}$", message = "O número do cartão deve conter 16 dígitos numéricos")
    private String numeroCartao;

    @NotBlank(message = "A senha é obrigatória")
    @Pattern(regexp = "^\\d{4}$", message = "A senha deve conter exatamente 4 dígitos numéricos")
    private String senha;
}