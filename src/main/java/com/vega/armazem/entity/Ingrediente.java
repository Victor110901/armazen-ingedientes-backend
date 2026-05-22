package com.vega.armazem.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.vega.armazem.enums.TipoIngrediente;

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
@Table(name = "ingredientes")
public class Ingrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoIngrediente tipo;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compartimento_id", nullable = false)
    private Compartimento compartimento;

    @Column(name = "responsavel_ultima_movimentacao", nullable = false, length = 100)
    private String responsavelUltimaMovimentacao;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em")
    private OffsetDateTime atualizadoEm;

    protected Ingrediente() {
    }

    public Ingrediente(
            String nome,
            TipoIngrediente tipo,
            BigDecimal quantidade,
            Compartimento compartimento,
            String responsavelUltimaMovimentacao
    ) {
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.compartimento = compartimento;
        this.responsavelUltimaMovimentacao = responsavelUltimaMovimentacao;
        this.criadoEm = OffsetDateTime.now();
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

    public Compartimento getCompartimento() {
        return compartimento;
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

    public void adicionarQuantidade(BigDecimal quantidade, String responsavel) {
        this.quantidade = this.quantidade.add(quantidade);
        this.responsavelUltimaMovimentacao = responsavel;
        this.atualizadoEm = OffsetDateTime.now();
    }

    public void retirarQuantidade(BigDecimal quantidade, String responsavel) {
        this.quantidade = this.quantidade.subtract(quantidade);
        this.responsavelUltimaMovimentacao = responsavel;
        this.atualizadoEm = OffsetDateTime.now();
    }
}