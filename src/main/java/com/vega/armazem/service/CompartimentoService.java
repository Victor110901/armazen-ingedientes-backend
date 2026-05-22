package com.vega.armazem.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vega.armazem.dto.response.CompartimentoDisponivelResponse;
import com.vega.armazem.entity.Compartimento;
import com.vega.armazem.enums.TipoIngrediente;
import com.vega.armazem.exception.BusinessException;
import com.vega.armazem.repository.CompartimentoRepository;

@Service
public class CompartimentoService {

    private final CompartimentoRepository compartimentoRepository;

    public CompartimentoService(CompartimentoRepository compartimentoRepository) {
        this.compartimentoRepository = compartimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<CompartimentoDisponivelResponse> buscarDisponiveisParaArmazenamento(
            BigDecimal quantidade,
            TipoIngrediente tipo
    ) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("A quantidade deve ser maior que zero.");
        }

        return compartimentoRepository.findAll()
                .stream()
                .filter(compartimento -> podeArmazenar(compartimento, tipo, quantidade))
                .map(compartimento -> {
                    BigDecimal espacoDisponivel = calcularEspacoDisponivel(compartimento, tipo);

                    return CompartimentoDisponivelResponse.disponivel(
                            compartimento,
                            tipo,
                            espacoDisponivel
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompartimentoDisponivelResponse> buscarDisponiveisParaVenda(TipoIngrediente tipo) {
        return compartimentoRepository.findAll()
                .stream()
                .filter(compartimento -> compartimento.getTipoAtual() == tipo)
                .filter(compartimento -> compartimento.getQuantidadeAtual().compareTo(BigDecimal.ZERO) > 0)
                .map(compartimento -> {
                    BigDecimal espacoDisponivel = calcularEspacoDisponivel(compartimento, tipo);

                    return CompartimentoDisponivelResponse.disponivel(
                            compartimento,
                            tipo,
                            espacoDisponivel
                    );
                })
                .toList();
    }

    private boolean podeArmazenar(
            Compartimento compartimento,
            TipoIngrediente tipo,
            BigDecimal quantidade
    ) {
        if (compartimento.getTipoAtual() != null && compartimento.getTipoAtual() != tipo) {
            return false;
        }

        if (estaBloqueadoParaTrocaDeTipoHoje(compartimento, tipo)) {
            return false;
        }

        BigDecimal espacoDisponivel = calcularEspacoDisponivel(compartimento, tipo);

        return espacoDisponivel.compareTo(quantidade) >= 0;
    }

    private boolean estaBloqueadoParaTrocaDeTipoHoje(
            Compartimento compartimento,
            TipoIngrediente tipo
    ) {
        if (!compartimento.estaVazio()) {
            return false;
        }

        if (compartimento.getUltimoTipoArmazenado() == null) {
            return false;
        }

        if (compartimento.getUltimoTipoArmazenado() == tipo) {
            return false;
        }

        LocalDate ultimaData = compartimento.getUltimaDataArmazenamento();

        return ultimaData != null && ultimaData.isEqual(LocalDate.now());
    }

    private BigDecimal calcularEspacoDisponivel(
            Compartimento compartimento,
            TipoIngrediente tipo
    ) {
        return tipo.getCapacidadeMaxima().subtract(compartimento.getQuantidadeAtual());
    }
}