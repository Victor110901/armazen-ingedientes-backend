package com.vega.armazem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.vega.armazem.dto.response.VolumePorTipoResponse;
import com.vega.armazem.entity.Compartimento;
import com.vega.armazem.entity.Ingrediente;
import com.vega.armazem.enums.TipoIngrediente;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    List<Ingrediente> findByTipo(TipoIngrediente tipo);

    List<Ingrediente> findByCompartimento(Compartimento compartimento);
    
    @Query("""
            SELECT new com.vega.armazem.dto.response.VolumePorTipoResponse(
                i.tipo,
                COALESCE(SUM(i.quantidade), 0)
            )
            FROM Ingrediente i
            GROUP BY i.tipo
            """)
    List<VolumePorTipoResponse> calcularVolumePorTipo();
}