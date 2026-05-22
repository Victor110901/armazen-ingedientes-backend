package com.vega.armazem.service;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import com.vega.armazem.dto.request.CriarIngredienteRequest;
import com.vega.armazem.enums.TipoIngrediente;

class CriarIngredienteRequestTestBuilder {

    private String nome;
    private TipoIngrediente tipo;
    private BigDecimal quantidade;
    private Long compartimentoId;
    private String responsavel;

    CriarIngredienteRequestTestBuilder nome(String nome) {
        this.nome = nome;
        return this;
    }

    CriarIngredienteRequestTestBuilder tipo(TipoIngrediente tipo) {
        this.tipo = tipo;
        return this;
    }

    CriarIngredienteRequestTestBuilder quantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
        return this;
    }

    CriarIngredienteRequestTestBuilder compartimentoId(Long compartimentoId) {
        this.compartimentoId = compartimentoId;
        return this;
    }

    CriarIngredienteRequestTestBuilder responsavel(String responsavel) {
        this.responsavel = responsavel;
        return this;
    }

    CriarIngredienteRequest build() {
        CriarIngredienteRequest request = new CriarIngredienteRequest();

        setField(request, "nome", nome);
        setField(request, "tipo", tipo);
        setField(request, "quantidade", quantidade);
        setField(request, "compartimentoId", compartimentoId);
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