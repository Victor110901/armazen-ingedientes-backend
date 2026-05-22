package com.vega.armazem.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vega.armazem.dto.response.HistoricoMovimentacaoResponse;
import com.vega.armazem.entity.Compartimento;
import com.vega.armazem.entity.HistoricoMovimentacao;
import com.vega.armazem.entity.Ingrediente;
import com.vega.armazem.enums.TipoIngrediente;
import com.vega.armazem.enums.TipoOperacao;
import com.vega.armazem.exception.BusinessException;
import com.vega.armazem.repository.HistoricoMovimentacaoRepository;

@Service
public class HistoricoMovimentacaoService {

    private final HistoricoMovimentacaoRepository historicoMovimentacaoRepository;

    public HistoricoMovimentacaoService(HistoricoMovimentacaoRepository historicoMovimentacaoRepository) {
        this.historicoMovimentacaoRepository = historicoMovimentacaoRepository;
    }
    
    @Transactional(readOnly = true)
    public List<HistoricoMovimentacaoResponse> listar(String sortBy, String order) {
        Sort sort = criarOrdenacao(sortBy, order);

        return historicoMovimentacaoRepository.findAll(sort)
                .stream()
                .map(HistoricoMovimentacaoResponse::fromEntity)
                .toList();
    }

    private Sort criarOrdenacao(String sortBy, String order) {
        Sort.Direction direction = resolverDirecao(order);

        if (sortBy == null || sortBy.isBlank() || sortBy.equalsIgnoreCase("date")) {
            return Sort.by(direction, "dataHora");
        }

        if (sortBy.equalsIgnoreCase("compartimento")) {
            return Sort.by(direction, "compartimento.codigo")
                    .and(Sort.by(direction, "dataHora"));
        }

        throw new BusinessException(
                "Parâmetro sortBy inválido. Valores aceitos: date ou compartimento."
        );
    }

    private Sort.Direction resolverDirecao(String order) {
        if (order == null || order.isBlank() || order.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }

        if (order.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }

        throw new BusinessException(
                "Parâmetro order inválido. Valores aceitos: asc ou desc."
        );
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