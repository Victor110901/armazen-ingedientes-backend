package com.vega.armazem.dto.response;

import java.math.BigDecimal;

import com.vega.armazem.enums.TipoIngrediente;

public class VolumePorTipoResponse {

    private TipoIngrediente type;
    private BigDecimal totalQuantity;

    public VolumePorTipoResponse(TipoIngrediente type, BigDecimal totalQuantity) {
        this.type = type;
        this.totalQuantity = totalQuantity;
    }

    public TipoIngrediente getType() {
        return type;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }
}