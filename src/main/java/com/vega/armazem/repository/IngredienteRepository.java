package com.vega.armazem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vega.armazem.entity.Compartimento;
import com.vega.armazem.entity.Ingrediente;
import com.vega.armazem.enums.TipoIngrediente;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    List<Ingrediente> findByTipo(TipoIngrediente tipo);

    List<Ingrediente> findByCompartimento(Compartimento compartimento);
}