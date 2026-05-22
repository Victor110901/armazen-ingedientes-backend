package com.vega.armazem.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.vega.armazem.enums.TipoIngrediente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "compartimentos")
public class Compartimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 2)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_atual", length = 20)
    private TipoIngrediente tipoAtual;

    @Column(name = "ultimo_tipo_armazenado", length = 20)
    @Enumerated(EnumType.STRING)
    private TipoIngrediente ultimoTipoArmazenado;

    @Column(name = "quantidade_atual", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidadeAtual = BigDecimal.ZERO;

    @Column(name = "ultima_data_armazenamento")
    private LocalDate ultimaDataArmazenamento;

    @Column(name = "criado_em", nullable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "atualizado_em")
    private OffsetDateTime atualizadoEm;

    @Version
    private Long versao;

    protected Compartimento() {
    }

    public Compartimento(String codigo) {
        this.codigo = codigo;
        this.quantidadeAtual = BigDecimal.ZERO;
        this.criadoEm = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoIngrediente getTipoAtual() {
        return tipoAtual;
    }

    public TipoIngrediente getUltimoTipoArmazenado() {
        return ultimoTipoArmazenado;
    }

    public BigDecimal getQuantidadeAtual() {
        return quantidadeAtual;
    }

    public LocalDate getUltimaDataArmazenamento() {
        return ultimaDataArmazenamento;
    }

    public OffsetDateTime getCriadoEm() {
        return criadoEm;
    }

    public OffsetDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public Long getVersao() {
        return versao;
    }

    public boolean estaVazio() {
        return quantidadeAtual.compareTo(BigDecimal.ZERO) == 0;
    }

    public void adicionarQuantidade(TipoIngrediente tipo, BigDecimal quantidade) {
        this.tipoAtual = tipo;
        this.ultimoTipoArmazenado = tipo;
        this.quantidadeAtual = this.quantidadeAtual.add(quantidade);
        this.ultimaDataArmazenamento = LocalDate.now();
        this.atualizadoEm = OffsetDateTime.now();
    }

    public void retirarQuantidade(BigDecimal quantidade) {
        this.quantidadeAtual = this.quantidadeAtual.subtract(quantidade);

        if (this.quantidadeAtual.compareTo(BigDecimal.ZERO) == 0) {
            this.tipoAtual = null;
        }

        this.atualizadoEm = OffsetDateTime.now();
    }
}