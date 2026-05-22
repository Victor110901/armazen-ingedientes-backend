package com.vega.armazem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vega.armazem.dto.request.CriarIngredienteRequest;
import com.vega.armazem.dto.request.MovimentarIngredienteRequest;
import com.vega.armazem.dto.response.IngredienteResponse;
import com.vega.armazem.entity.Compartimento;
import com.vega.armazem.entity.Ingrediente;
import com.vega.armazem.enums.TipoIngrediente;
import com.vega.armazem.exception.BusinessException;
import com.vega.armazem.exception.ResourceNotFoundException;
import com.vega.armazem.repository.CompartimentoRepository;
import com.vega.armazem.repository.IngredienteRepository;

@ExtendWith(MockitoExtension.class)
class IngredienteServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private CompartimentoRepository compartimentoRepository;

    @Mock
    private HistoricoMovimentacaoService historicoMovimentacaoService;

    @InjectMocks
    private IngredienteService ingredienteService;

    private Compartimento compartimentoC1;

    @BeforeEach
    void setUp() {
        compartimentoC1 = new Compartimento("C1");
    }

    @Test
    void deveCadastrarIngredienteComSucesso() {
        CriarIngredienteRequest request = criarRequestIngrediente(
                "Farinha de Trigo",
                TipoIngrediente.SECO,
                new BigDecimal("400.000"),
                1L,
                "pedro"
        );

        when(compartimentoRepository.findById(1L)).thenReturn(Optional.of(compartimentoC1));

        when(ingredienteRepository.save(any(Ingrediente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IngredienteResponse response = ingredienteService.criar(request);

        assertThat(response.getNome()).isEqualTo("Farinha de Trigo");
        assertThat(response.getTipo()).isEqualTo(TipoIngrediente.SECO);
        assertThat(response.getQuantidade()).isEqualByComparingTo("400.000");
        assertThat(compartimentoC1.getQuantidadeAtual()).isEqualByComparingTo("400.000");
        assertThat(compartimentoC1.getTipoAtual()).isEqualTo(TipoIngrediente.SECO);

        verify(ingredienteRepository).save(any(Ingrediente.class));
        verify(historicoMovimentacaoService).registrarEntrada(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    void deveLancarErroQuandoCompartimentoNaoExistir() {
        CriarIngredienteRequest request = criarRequestIngrediente(
                "Farinha de Trigo",
                TipoIngrediente.SECO,
                new BigDecimal("400.000"),
                99L,
                "pedro"
        );

        when(compartimentoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredienteService.criar(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Compartimento não encontrado");
    }

    @Test
    void deveLancarErroQuandoQuantidadeExcederCapacidadeMaximaDoTipo() {
        CriarIngredienteRequest request = criarRequestIngrediente(
                "Farinha de Trigo",
                TipoIngrediente.SECO,
                new BigDecimal("700.000"),
                1L,
                "pedro"
        );

        when(compartimentoRepository.findById(1L)).thenReturn(Optional.of(compartimentoC1));

        assertThatThrownBy(() -> ingredienteService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("excede a capacidade máxima");
    }

    @Test
    void deveLancarErroQuandoCompartimentoJaArmazenaOutroTipo() {
        compartimentoC1.adicionarQuantidade(TipoIngrediente.SECO, new BigDecimal("300.000"));

        CriarIngredienteRequest request = criarRequestIngrediente(
                "Leite",
                TipoIngrediente.LIQUIDO,
                new BigDecimal("100.000"),
                1L,
                "ana"
        );

        when(compartimentoRepository.findById(1L)).thenReturn(Optional.of(compartimentoC1));

        assertThatThrownBy(() -> ingredienteService.criar(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("já armazena ingredientes do tipo SECO");
    }

    @Test
    void deveAdicionarEntradaComSucesso() {
        Ingrediente ingrediente = criarIngredienteExistente(
                "Farinha de Trigo",
                TipoIngrediente.SECO,
                new BigDecimal("400.000"),
                compartimentoC1,
                "pedro"
        );

        compartimentoC1.adicionarQuantidade(TipoIngrediente.SECO, new BigDecimal("400.000"));

        MovimentarIngredienteRequest request = criarRequestMovimentacao(
                new BigDecimal("100.000"),
                "maria"
        );

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.save(any(Ingrediente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IngredienteResponse response = ingredienteService.adicionarEntrada(1L, request);

        assertThat(response.getQuantidade()).isEqualByComparingTo("500.000");
        assertThat(compartimentoC1.getQuantidadeAtual()).isEqualByComparingTo("500.000");

        verify(historicoMovimentacaoService).registrarEntrada(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    void deveRegistrarSaidaComSucesso() {
        Ingrediente ingrediente = criarIngredienteExistente(
                "Farinha de Trigo",
                TipoIngrediente.SECO,
                new BigDecimal("400.000"),
                compartimentoC1,
                "pedro"
        );

        compartimentoC1.adicionarQuantidade(TipoIngrediente.SECO, new BigDecimal("400.000"));

        MovimentarIngredienteRequest request = criarRequestMovimentacao(
                new BigDecimal("150.000"),
                "joao"
        );

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.save(any(Ingrediente.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IngredienteResponse response = ingredienteService.registrarSaida(1L, request);

        assertThat(response.getQuantidade()).isEqualByComparingTo("250.000");
        assertThat(compartimentoC1.getQuantidadeAtual()).isEqualByComparingTo("250.000");

        verify(historicoMovimentacaoService).registrarSaida(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    void deveLancarErroQuandoSaidaForMaiorQueQuantidadeDisponivel() {
        Ingrediente ingrediente = criarIngredienteExistente(
                "Farinha de Trigo",
                TipoIngrediente.SECO,
                new BigDecimal("100.000"),
                compartimentoC1,
                "pedro"
        );

        compartimentoC1.adicionarQuantidade(TipoIngrediente.SECO, new BigDecimal("100.000"));

        MovimentarIngredienteRequest request = criarRequestMovimentacao(
                new BigDecimal("200.000"),
                "joao"
        );

        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));

        assertThatThrownBy(() -> ingredienteService.registrarSaida(1L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Não é possível retirar");
    }

    private CriarIngredienteRequest criarRequestIngrediente(
            String nome,
            TipoIngrediente tipo,
            BigDecimal quantidade,
            Long compartimentoId,
            String responsavel
    ) {
        return new CriarIngredienteRequestTestBuilder()
                .nome(nome)
                .tipo(tipo)
                .quantidade(quantidade)
                .compartimentoId(compartimentoId)
                .responsavel(responsavel)
                .build();
    }

    private MovimentarIngredienteRequest criarRequestMovimentacao(
            BigDecimal quantidade,
            String responsavel
    ) {
        return new MovimentarIngredienteRequestTestBuilder()
                .quantidade(quantidade)
                .responsavel(responsavel)
                .build();
    }

    private Ingrediente criarIngredienteExistente(
            String nome,
            TipoIngrediente tipo,
            BigDecimal quantidade,
            Compartimento compartimento,
            String responsavel
    ) {
        return new Ingrediente(
                nome,
                tipo,
                quantidade,
                compartimento,
                responsavel
        );
    }
}