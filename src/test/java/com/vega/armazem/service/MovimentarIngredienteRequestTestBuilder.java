package com.vega.armazem.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import com.vega.armazem.dto.request.MovimentarIngredienteRequest;

class MovimentarIngredienteRequestTestBuilder {

    private BigDecimal quantidade;
    private String responsavel;

    MovimentarIngredienteRequestTestBuilder quantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
        return this;
    }

    MovimentarIngredienteRequestTestBuilder responsavel(String responsavel) {
        this.responsavel = responsavel;
        return this;
    }

    MovimentarIngredienteRequest build() {
        MovimentarIngredienteRequest request = new MovimentarIngredienteRequest();

        setField(request, "quantidade", quantidade);
        setField(request, "responsavel", responsavel);

        return request;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Erro ao montar objeto de teste.", exception);
        }
    }
}