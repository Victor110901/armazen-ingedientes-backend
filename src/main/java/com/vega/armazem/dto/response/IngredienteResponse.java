package com.vega.armazem.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.vega.armazem.entity.Ingrediente;
import com.vega.armazem.enums.TipoIngrediente;

public class IngredienteResponse {

    private Long id;
    private String nome;
    private TipoIngrediente tipo;
    private BigDecimal quantidade;
    private String unidadeMedida;
    private Long compartimentoId;
    private String compartimentoCodigo;
    private String responsavelUltimaMovimentacao;
    private OffsetDateTime criadoEm;
    private OffsetDateTime atualizadoEm;

    public IngredienteResponse(
            Long id,
            String nome,
            TipoIngrediente tipo,
            BigDecimal quantidade,
            String unidadeMedida,
            Long compartimentoId,
            String compartimentoCodigo,
            String responsavelUltimaMovimentacao,
            OffsetDateTime criadoEm,
            OffsetDateTime atualizadoEm
    ) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.compartimentoId = compartimentoId;
        this.compartimentoCodigo = compartimentoCodigo;
        this.responsavelUltimaMovimentacao = responsavelUltimaMovimentacao;
        this.criadoEm = criadoEm;
        this.atualizadoEm = atualizadoEm;
    }

    public static IngredienteResponse fromEntity(Ingrediente ingrediente) {
        return new IngredienteResponse(
                ingrediente.getId(),
                ingrediente.getNome(),
                ingrediente.getTipo(),
                ingrediente.getQuantidade(),
                ingrediente.getTipo().getUnidadeMedida(),
                ingrediente.getCompartimento().getId(),
                ingrediente.getCompartimento().getCodigo(),
                ingrediente.getResponsavelUltimaMovimentacao(),
                ingrediente.getCriadoEm(),
                ingrediente.getAtualizadoEm()
        );
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoIngrediente getTipo() {
        return tipo;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public String getUnidadeMedida() {
        return unidadeMedida;
    }

    public Long getCompartimentoId() {
        return compartimentoId;
    }

    public String getCompartimentoCodigo() {
        return compartimentoCodigo;
    }

    public String getResponsavelUltimaMovimentacao() {
        return responsavelUltimaMovimentacao;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }
}