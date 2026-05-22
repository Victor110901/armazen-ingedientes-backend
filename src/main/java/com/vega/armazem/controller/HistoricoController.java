package com.vega.armazem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vega.armazem.dto.response.HistoricoMovimentacaoResponse;
import com.vega.armazem.service.HistoricoMovimentacaoService;

@RestController
@RequestMapping("/historico")
public class HistoricoController {

    private final HistoricoMovimentacaoService historicoMovimentacaoService;

    public HistoricoController(HistoricoMovimentacaoService historicoMovimentacaoService) {
        this.historicoMovimentacaoService = historicoMovimentacaoService;
    }

    @GetMapping
    public ResponseEntity<List<HistoricoMovimentacaoResponse>> listar(
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String order
    ) {
        List<HistoricoMovimentacaoResponse> response =
                historicoMovimentacaoService.listar(sortBy, order);

        return ResponseEntity.ok(response);
    }
}