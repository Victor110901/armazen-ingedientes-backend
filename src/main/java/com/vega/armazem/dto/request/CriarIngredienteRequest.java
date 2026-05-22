package com.vega.armazem.dto.request;

import java.math.BigDecimal;

import com.vega.armazem.enums.TipoIngrediente;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CriarIngredienteRequest {

    @NotBlank(message = "O nome do ingrediente é obrigatório.")
    @Size(max = 120, message = "O nome do ingrediente deve ter no máximo 120 caracteres.")
    private String nome;

    @NotNull(message = "O tipo do ingrediente é obrigatório.")
    private TipoIngrediente tipo;

    @NotNull(message = "A quantidade é obrigatória.")
    @DecimalMin(value = "0.001", message = "A quantidade deve ser maior que zero.")
    private BigDecimal quantidade;

    @NotNull(message = "O compartimento é obrigatório.")
    private Long compartimentoId;

    @NotBlank(message = "O responsável pela movimentação é obrigatório.")
    @Size(max = 100, message = "O responsável deve ter no máximo 100 caracteres.")
    private String responsavel;

    public String getNome() {
        return nome;
    }

    public TipoIngrediente getTipo() {
        return tipo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public Long getCompartimentoId() {
        return compartimentoId;
    }

    public String getResponsavel() {
        return responsavel;
    }
}