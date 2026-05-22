package com.vega.armazem.enums;

import java.math.BigDecimal;

public enum TipoIngrediente {

    SECO(new BigDecimal("600.000"), "kg"),
    LIQUIDO(new BigDecimal("500.000"), "L"),
    REFRIGERADO(new BigDecimal("400.000"), "kg");

    private final BigDecimal capacidadeMaxima;
    private final String unidadeMedida;

    TipoIngrediente(BigDecimal capacidadeMaxima, String unidadeMedida) {
        this.capacidadeMaxima = capacidadeMaxima;
        this.unidadeMedida = unidadeMedida;
    }

    public BigDecimal getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }
}