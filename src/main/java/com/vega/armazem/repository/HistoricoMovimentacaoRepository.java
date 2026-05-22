package com.vega.armazem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vega.armazem.entity.HistoricoMovimentacao;

public interface HistoricoMovimentacaoRepository extends JpaRepository<HistoricoMovimentacao, Long> {
}