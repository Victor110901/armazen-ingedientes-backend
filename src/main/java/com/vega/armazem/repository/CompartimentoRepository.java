package com.vega.armazem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vega.armazem.entity.Compartimento;

public interface CompartimentoRepository extends JpaRepository<Compartimento, Long> {

    Optional<Compartimento> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}