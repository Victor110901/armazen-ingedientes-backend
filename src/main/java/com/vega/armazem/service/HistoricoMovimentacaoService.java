package com.vega.armazem.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vega.armazem.entity.Compartimento;
import com.vega.armazem.entity.HistoricoMovimentacao;
import com.vega.armazem.entity.Ingrediente;
import com.vega.armazem.enums.TipoIngrediente;
import com.vega.armazem.enums.TipoOperacao;
import com.vega.armazem.repository.HistoricoMovimentacaoRepository;

@Service
public class HistoricoMovimentacaoService {

    private final HistoricoMovimentacaoRepository historicoMovimentacaoRepository;

    public HistoricoMovimentacaoService(HistoricoMovimentacaoRepository historicoMovimentacaoRepository) {
        this.historicoMovimentacaoRepository = historicoMovimentacaoRepository;
    }

    @Transactional
    public HistoricoMovimentacao registrarEntrada(
            BigDecimal quantidade,
            TipoIngrediente tipoIngrediente,
            String nomeIngrediente,
            String responsavel,
            Compartimento compartimento,
            Ingrediente ingrediente
    ) {
        HistoricoMovimentacao historico = new HistoricoMovimentacao(
                TipoOperacao.ENTRADA,
                quantidade,
                tipoIngrediente,
                nomeIngrediente,
                responsavel,
                compartimento,
                ingrediente
        );

        return historicoMovimentacaoRepository.save(historico);
    }

    @Transactional
    public HistoricoMovimentacao registrarSaida(
            BigDecimal quantidade,
            TipoIngrediente tipoIngrediente,
            String nomeIngrediente,
            String responsavel,
            Compartimento compartimento,
            Ingrediente ingrediente
    ) {
        HistoricoMovimentacao historico = new HistoricoMovimentacao(
                TipoOperacao.SAIDA,
                quantidade,
                tipoIngrediente,
                nomeIngrediente,
                responsavel,
                compartimento,
                ingrediente
        );

        return historicoMovimentacaoRepository.save(historico);
    }
}