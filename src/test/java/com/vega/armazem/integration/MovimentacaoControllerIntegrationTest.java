package com.vega.armazem.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MovimentacaoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        String body = """
                {
                  "nome": "Farinha de Trigo",
                  "tipo": "SECO",
                  "quantidade": 400,
                  "compartimentoId": 1,
                  "responsavel": "pedro"
                }
                """;

        mockMvc.perform(post("/ingredientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void deveAdicionarEntradaComSucesso() throws Exception {
        String body = """
                {
                  "quantidade": 100,
                  "responsavel": "maria"
                }
                """;

        mockMvc.perform(post("/ingredientes/1/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade", is(500.0)))
                .andExpect(jsonPath("$.responsavelUltimaMovimentacao", is("maria")));
    }

    @Test
    void deveRegistrarSaidaComSucesso() throws Exception {
        String body = """
                {
                  "quantidade": 150,
                  "responsavel": "joao"
                }
                """;

        mockMvc.perform(post("/ingredientes/1/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidade", is(250.0)))
                .andExpect(jsonPath("$.responsavelUltimaMovimentacao", is("joao")));
    }

    @Test
    void deveRetornarErroQuandoSaidaForMaiorQueQuantidadeDisponivel() throws Exception {
        String body = """
                {
                  "quantidade": 999,
                  "responsavel": "joao"
                }
                """;

        mockMvc.perform(post("/ingredientes/1/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Não é possível retirar")));
    }

    @Test
    void deveRegistrarHistoricoDasMovimentacoes() throws Exception {
        String entradaBody = """
                {
                  "quantidade": 100,
                  "responsavel": "maria"
                }
                """;

        String saidaBody = """
                {
                  "quantidade": 50,
                  "responsavel": "joao"
                }
                """;

        mockMvc.perform(post("/ingredientes/1/entrada")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(entradaBody))
                .andExpect(status().isOk());

        mockMvc.perform(post("/ingredientes/1/saida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saidaBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/historico?sortBy=date&order=asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].tipoOperacao", is("ENTRADA")))
                .andExpect(jsonPath("$[1].tipoOperacao", is("ENTRADA")))
                .andExpect(jsonPath("$[2].tipoOperacao", is("SAIDA")));
    }
}