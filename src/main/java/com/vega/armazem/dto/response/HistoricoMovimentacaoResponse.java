package com.vega.armazem.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.vega.armazem.entity.HistoricoMovimentacao;
import com.vega.armazem.enums.TipoIngrediente;
import com.vega.armazem.enums.TipoOperacao;

public class HistoricoMovimentacaoResponse {

    private Long id;
    private OffsetDateTime dataHora;
    private TipoOperacao tipoOperacao;
    private BigDecimal quantidade;
    private TipoIngrediente tipoIngrediente;
    private String nomeIngrediente;
    private String responsavel;
    private Long compartimentoId;
    private String compartimentoCodigo;
    private Long ingredienteId;

    public HistoricoMovimentacaoResponse(
            Long id,
            OffsetDateTime dataHora,
            TipoOperacao tipoOperacao,
            BigDecimal quantidade,
            TipoIngrediente tipoIngrediente,
            String nomeIngrediente,
            String responsavel,
            Long compartimentoId,
            String compartimentoCodigo,
            Long ingredienteId
    ) {
        this.id = id;
        this.dataHora = dataHora;
        this.tipoOperacao = tipoOperacao;
        this.quantidade = quantidade;
        this.tipoIngrediente = tipoIngrediente;
        this.nomeIngrediente = nomeIngrediente;
        this.responsavel = responsavel;
        this.compartimentoId = compartimentoId;
        this.compartimentoCodigo = compartimentoCodigo;
        this.ingredienteId = ingredienteId;
    }

    public static HistoricoMovimentacaoResponse fromEntity(HistoricoMovimentacao historico) {
        Long ingredienteId = historico.getIngrediente() != null
                ? historico.getIngrediente().getId()
                : null;

        return new HistoricoMovimentacaoResponse(
                historico.getId(),
                historico.getDataHora(),
                historico.getTipoOperacao(),
                historico.getQuantidade(),
                historico.getTipoIngrediente(),
                historico.getNomeIngrediente(),
                historico.getResponsavel(),
                historico.getCompartimento().getId(),
                historico.getCompartimento().getCodigo(),
                ingredienteId
        );
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

    public Long getCompartimentoId() {
        return compartimentoId;
    }

    public String getCompartimentoCodigo() {
        return compartimentoCodigo;
    }

    public Long getIngredienteId() {
        return ingredienteId;
    }
}