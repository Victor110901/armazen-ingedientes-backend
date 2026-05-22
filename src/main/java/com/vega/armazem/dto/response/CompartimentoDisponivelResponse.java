package com.vega.armazem.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.vega.armazem.entity.Compartimento;
import com.vega.armazem.enums.TipoIngrediente;

public class CompartimentoDisponivelResponse {

    private Long id;
    private String codigo;
    private TipoIngrediente tipoAtual;
    private TipoIngrediente ultimoTipoArmazenado;
    private BigDecimal quantidadeAtual;
    private BigDecimal capacidadeMaxima;
    private BigDecimal espacoDisponivel;
    private LocalDate ultimaDataArmazenamento;
    private boolean disponivel;
    private String motivo;

    public CompartimentoDisponivelResponse(
            Long id,
            String codigo,
            TipoIngrediente tipoAtual,
            TipoIngrediente ultimoTipoArmazenado,
            BigDecimal quantidadeAtual,
            BigDecimal capacidadeMaxima,
            BigDecimal espacoDisponivel,
            LocalDate ultimaDataArmazenamento,
            boolean disponivel,
            String motivo
    ) {
        this.id = id;
        this.codigo = codigo;
        this.tipoAtual = tipoAtual;
        this.ultimoTipoArmazenado = ultimoTipoArmazenado;
        this.quantidadeAtual = quantidadeAtual;
        this.capacidadeMaxima = capacidadeMaxima;
        this.espacoDisponivel = espacoDisponivel;
        this.ultimaDataArmazenamento = ultimaDataArmazenamento;
        this.disponivel = disponivel;
        this.motivo = motivo;
    }

    public static CompartimentoDisponivelResponse disponivel(
            Compartimento compartimento,
            TipoIngrediente tipo,
            BigDecimal espacoDisponivel
    ) {
        return new CompartimentoDisponivelResponse(
                compartimento.getId(),
                compartimento.getCodigo(),
                compartimento.getTipoAtual(),
                compartimento.getUltimoTipoArmazenado(),
                compartimento.getQuantidadeAtual(),
                tipo.getCapacidadeMaxima(),
                espacoDisponivel,
                compartimento.getUltimaDataArmazenamento(),
                true,
                "Compartimento disponível para armazenamento."
        );
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

    public BigDecimal getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public BigDecimal getEspacoDisponivel() {
        return espacoDisponivel;
    }

    public LocalDate getUltimaDataArmazenamento() {
        return ultimaDataArmazenamento;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public String getMotivo() {
        return motivo;
    }
}