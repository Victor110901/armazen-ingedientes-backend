package com.vega.armazem.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vega.armazem.dto.request.CriarIngredienteRequest;
import com.vega.armazem.dto.request.MovimentarIngredienteRequest;
import com.vega.armazem.dto.response.IngredienteResponse;
import com.vega.armazem.dto.response.VolumePorTipoResponse;
import com.vega.armazem.entity.Compartimento;
import com.vega.armazem.entity.Ingrediente;
import com.vega.armazem.enums.TipoIngrediente;
import com.vega.armazem.exception.BusinessException;
import com.vega.armazem.exception.ResourceNotFoundException;
import com.vega.armazem.repository.CompartimentoRepository;
import com.vega.armazem.repository.IngredienteRepository;

@Service
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final CompartimentoRepository compartimentoRepository;
    private final HistoricoMovimentacaoService historicoMovimentacaoService;

    public IngredienteService(
            IngredienteRepository ingredienteRepository,
            CompartimentoRepository compartimentoRepository,
            HistoricoMovimentacaoService historicoMovimentacaoService
    ) {
        this.ingredienteRepository = ingredienteRepository;
        this.compartimentoRepository = compartimentoRepository;
        this.historicoMovimentacaoService = historicoMovimentacaoService;
    }

    @Transactional
    public IngredienteResponse criar(CriarIngredienteRequest request) {
        Compartimento compartimento = buscarCompartimentoPorId(request.getCompartimentoId());

        validarEntradaNoCompartimento(
                compartimento,
                request.getTipo(),
                request.getQuantidade()
        );

        Ingrediente ingrediente = new Ingrediente(
                request.getNome().trim(),
                request.getTipo(),
                request.getQuantidade(),
                compartimento,
                request.getResponsavel().trim()
        );

        compartimento.adicionarQuantidade(request.getTipo(), request.getQuantidade());

        Ingrediente ingredienteSalvo = ingredienteRepository.save(ingrediente);

        historicoMovimentacaoService.registrarEntrada(
                request.getQuantidade(),
                request.getTipo(),
                request.getNome().trim(),
                request.getResponsavel().trim(),
                compartimento,
                ingredienteSalvo
        );

        return IngredienteResponse.fromEntity(ingredienteSalvo);
    }

    @Transactional(readOnly = true)
    public List<IngredienteResponse> listarTodos() {
        return ingredienteRepository.findAll()
                .stream()
                .map(IngredienteResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public IngredienteResponse buscarPorId(Long id) {
        Ingrediente ingrediente = buscarIngredienteEntidadePorId(id);

        return IngredienteResponse.fromEntity(ingrediente);
    }

    private Compartimento buscarCompartimentoPorId(Long compartimentoId) {
        return compartimentoRepository.findById(compartimentoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Compartimento não encontrado com o ID: " + compartimentoId
                ));
    }
    
    @Transactional
    public IngredienteResponse adicionarEntrada(Long ingredienteId, MovimentarIngredienteRequest request) {
        Ingrediente ingrediente = buscarIngredienteEntidadePorId(ingredienteId);
        Compartimento compartimento = ingrediente.getCompartimento();

        validarEntradaNoCompartimento(
                compartimento,
                ingrediente.getTipo(),
                request.getQuantidade()
        );

        ingrediente.adicionarQuantidade(
                request.getQuantidade(),
                request.getResponsavel().trim()
        );

        compartimento.adicionarQuantidade(
                ingrediente.getTipo(),
                request.getQuantidade()
        );

        Ingrediente ingredienteSalvo = ingredienteRepository.save(ingrediente);

        historicoMovimentacaoService.registrarEntrada(
                request.getQuantidade(),
                ingrediente.getTipo(),
                ingrediente.getNome(),
                request.getResponsavel().trim(),
                compartimento,
                ingredienteSalvo
        );

        return IngredienteResponse.fromEntity(ingredienteSalvo);
    }

    @Transactional
    public IngredienteResponse registrarSaida(Long ingredienteId, MovimentarIngredienteRequest request) {
        Ingrediente ingrediente = buscarIngredienteEntidadePorId(ingredienteId);
        Compartimento compartimento = ingrediente.getCompartimento();

        validarQuantidadePositiva(request.getQuantidade());
        validarQuantidadeDisponivelParaSaida(ingrediente, request.getQuantidade());

        ingrediente.retirarQuantidade(
                request.getQuantidade(),
                request.getResponsavel().trim()
        );

        compartimento.retirarQuantidade(request.getQuantidade());

        Ingrediente ingredienteSalvo = ingredienteRepository.save(ingrediente);

        historicoMovimentacaoService.registrarSaida(
                request.getQuantidade(),
                ingrediente.getTipo(),
                ingrediente.getNome(),
                request.getResponsavel().trim(),
                compartimento,
                ingredienteSalvo
        );

        return IngredienteResponse.fromEntity(ingredienteSalvo);
    }
    
    private Ingrediente buscarIngredienteEntidadePorId(Long id) {
        return ingredienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ingrediente não encontrado com o ID: " + id
                ));
    }
    
    @Transactional(readOnly = true)
    public List<VolumePorTipoResponse> calcularVolumePorTipo() {
        return ingredienteRepository.calcularVolumePorTipo();
    }

    private void validarQuantidadeDisponivelParaSaida(
            Ingrediente ingrediente,
            BigDecimal quantidade
    ) {
        if (ingrediente.getQuantidade().compareTo(quantidade) < 0) {
            throw new BusinessException(
                    "Não é possível retirar "
                            + quantidade
                            + " "
                            + ingrediente.getTipo().getUnidadeMedida()
                            + " do ingrediente "
                            + ingrediente.getNome()
                            + ". Quantidade disponível: "
                            + ingrediente.getQuantidade()
                            + " "
                            + ingrediente.getTipo().getUnidadeMedida()
                            + "."
            );
        }
    }

    private void validarEntradaNoCompartimento(
            Compartimento compartimento,
            TipoIngrediente tipoIngrediente,
            BigDecimal quantidade
    ) {
        validarQuantidadePositiva(quantidade);
        validarCompatibilidadeDeTipo(compartimento, tipoIngrediente);
        validarCapacidadeMaximaDoTipo(tipoIngrediente, quantidade);
        validarEspacoDisponivel(compartimento, tipoIngrediente, quantidade);
        validarTrocaDeTipoNoMesmoDia(compartimento, tipoIngrediente);
    }

    private void validarQuantidadePositiva(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("A quantidade deve ser maior que zero.");
        }
    }

    private void validarCompatibilidadeDeTipo(
            Compartimento compartimento,
            TipoIngrediente tipoIngrediente
    ) {
        if (
                compartimento.getTipoAtual() != null
                        && compartimento.getTipoAtual() != tipoIngrediente
        ) {
            throw new BusinessException(
                    "O compartimento " + compartimento.getCodigo()
                            + " já armazena ingredientes do tipo "
                            + compartimento.getTipoAtual()
                            + " e não pode receber ingredientes do tipo "
                            + tipoIngrediente
                            + "."
            );
        }
    }

    private void validarCapacidadeMaximaDoTipo(
            TipoIngrediente tipoIngrediente,
            BigDecimal quantidade
    ) {
        BigDecimal capacidadeMaxima = tipoIngrediente.getCapacidadeMaxima();

        if (quantidade.compareTo(capacidadeMaxima) > 0) {
            throw new BusinessException(
                    "A quantidade informada excede a capacidade máxima para ingredientes do tipo "
                            + tipoIngrediente
                            + ". Capacidade máxima: "
                            + capacidadeMaxima
                            + " "
                            + tipoIngrediente.getUnidadeMedida()
                            + "."
            );
        }
    }

    private void validarEspacoDisponivel(
            Compartimento compartimento,
            TipoIngrediente tipoIngrediente,
            BigDecimal quantidade
    ) {
        BigDecimal capacidadeMaxima = tipoIngrediente.getCapacidadeMaxima();
        BigDecimal quantidadeFinal = compartimento.getQuantidadeAtual().add(quantidade);

        if (quantidadeFinal.compareTo(capacidadeMaxima) > 0) {
            BigDecimal espacoDisponivel = capacidadeMaxima.subtract(compartimento.getQuantidadeAtual());

            throw new BusinessException(
                    "O compartimento " + compartimento.getCodigo()
                            + " não possui espaço suficiente. Espaço disponível: "
                            + espacoDisponivel
                            + " "
                            + tipoIngrediente.getUnidadeMedida()
                            + "."
            );
        }
    }

    private void validarTrocaDeTipoNoMesmoDia(
            Compartimento compartimento,
            TipoIngrediente tipoIngrediente
    ) {
        if (!compartimento.estaVazio()) {
            return;
        }

        if (compartimento.getUltimoTipoArmazenado() == null) {
            return;
        }

        if (compartimento.getUltimoTipoArmazenado() == tipoIngrediente) {
            return;
        }

        LocalDate ultimaDataArmazenamento = compartimento.getUltimaDataArmazenamento();

        if (ultimaDataArmazenamento != null && ultimaDataArmazenamento.isEqual(LocalDate.now())) {
            throw new BusinessException(
                    "O compartimento " + compartimento.getCodigo()
                            + " armazenou ingredientes do tipo "
                            + compartimento.getUltimoTipoArmazenado()
                            + " hoje e só poderá receber outro tipo amanhã."
            );
        }
    }
}