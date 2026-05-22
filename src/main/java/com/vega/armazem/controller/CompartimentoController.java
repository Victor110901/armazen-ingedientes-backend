package com.vega.armazem.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vega.armazem.dto.response.CompartimentoDisponivelResponse;
import com.vega.armazem.enums.TipoIngrediente;
import com.vega.armazem.service.CompartimentoService;

@RestController
@RequestMapping("/compartimentos")
public class CompartimentoController {

    private final CompartimentoService compartimentoService;

    public CompartimentoController(CompartimentoService compartimentoService) {
        this.compartimentoService = compartimentoService;
    }

    @GetMapping("/disponiveis")
    public ResponseEntity<List<CompartimentoDisponivelResponse>> buscarDisponiveisParaArmazenamento(
            @RequestParam BigDecimal quantidade,
            @RequestParam TipoIngrediente tipo
    ) {
        List<CompartimentoDisponivelResponse> response =
                compartimentoService.buscarDisponiveisParaArmazenamento(quantidade, tipo);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/disponiveis-para-venda")
    public ResponseEntity<List<CompartimentoDisponivelResponse>> buscarDisponiveisParaVenda(
            @RequestParam TipoIngrediente tipo
    ) {
        List<CompartimentoDisponivelResponse> response =
                compartimentoService.buscarDisponiveisParaVenda(tipo);

        return ResponseEntity.ok(response);
    }
}