package com.vega.armazem.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MovimentarIngredienteRequest {

    @NotNull(message = "A quantidade é obrigatória.")
    @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero.")
    private BigDecimal quantidade;

    @NotBlank(message = "O responsável pela movimentação é obrigatório.")
    @Size(max = 100, message = "O responsável deve ter no máximo 100 caracteres.")
    private String responsavel;

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public String getResponsavel() {
        return responsavel;
    }
}