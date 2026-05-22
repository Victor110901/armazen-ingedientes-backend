package com.vega.armazem.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.vega.armazem.enums.TipoIngrediente;
import com.vega.armazem.enums.TipoOperacao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "historico_movimentacoes")
public class HistoricoMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_hora", nullable = false)
    private OffsetDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_operacao", nullable = false, length = 20)
    private TipoOperacao tipoOperacao;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ingrediente", nullable = false, length = 20)
    private TipoIngrediente tipoIngrediente;

    @Column(name = "nome_ingrediente", nullable = false, length = 120)
    private String nomeIngrediente;

    @Column(nullable = false, length = 100)
    private String responsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartimento_id", nullable = false)
    private Compartimento compartimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingrediente_id")
    private Ingrediente ingrediente;

    protected HistoricoMovimentacao() {
    }

    public HistoricoMovimentacao(
            TipoOperacao tipoOperacao,
            BigDecimal quantidade,
            TipoIngrediente tipoIngrediente,
            String nomeIngrediente,
            String responsavel,
            Compartimento compartimento,
            Ingrediente ingrediente
    ) {
        this.dataHora = OffsetDateTime.now();
        this.tipoOperacao = tipoOperacao;
        this.quantidade = quantidade;
        this.tipoIngrediente = tipoIngrediente;
        this.nomeIngrediente = nomeIngrediente;
        this.responsavel = responsavel;
        this.compartimento = compartimento;
        this.ingrediente = ingrediente;
    }

    public Long getId() {
        return id;
    }

    public OffsetDateTime getDataHora() {
        return dataHora;
    }

    public TipoOperacao getTipoOperacao() {
        return tipoOperacao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public TipoIngrediente getTipoIngrediente() {
        return tipoIngrediente;
    }

    public String getNomeIngrediente() {
        return nomeIngrediente;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public Compartimento getCompartimento() {
        return compartimento;
    }

    public Ingrediente getIngrediente() {
        return ingrediente;
    }
}