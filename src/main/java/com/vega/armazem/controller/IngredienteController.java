package com.vega.armazem.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vega.armazem.dto.request.CriarIngredienteRequest;
import com.vega.armazem.dto.request.MovimentarIngredienteRequest;
import com.vega.armazem.dto.response.IngredienteResponse;
import com.vega.armazem.dto.response.VolumePorTipoResponse;
import com.vega.armazem.service.IngredienteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @PostMapping
    public ResponseEntity<IngredienteResponse> criar(
            @Valid @RequestBody CriarIngredienteRequest request
    ) {
        IngredienteResponse response = ingredienteService.criar(request);

        URI location = URI.create("/ingredientes/" + response.getId());

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<IngredienteResponse>> listarTodos() {
        List<IngredienteResponse> response = ingredienteService.listarTodos();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/volume")
    public ResponseEntity<List<VolumePorTipoResponse>> calcularVolumePorTipo() {
        List<VolumePorTipoResponse> response = ingredienteService.calcularVolumePorTipo();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredienteResponse> buscarPorId(@PathVariable Long id) {
        IngredienteResponse response = ingredienteService.buscarPorId(id);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/entrada")
    public ResponseEntity<IngredienteResponse> adicionarEntrada(
            @PathVariable Long id,
            @Valid @RequestBody MovimentarIngredienteRequest request
    ) {
        IngredienteResponse response = ingredienteService.adicionarEntrada(id, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/saida")
    public ResponseEntity<IngredienteResponse> registrarSaida(
            @PathVariable Long id,
            @Valid @RequestBody MovimentarIngredienteRequest request
    ) {
        IngredienteResponse response = ingredienteService.registrarSaida(id, request);

        return ResponseEntity.ok(response);
    }
}