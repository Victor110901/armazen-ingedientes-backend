package com.vega.armazem.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class IngredienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCadastrarIngredienteComSucesso() throws Exception {
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
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/ingredientes/1"))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Farinha de Trigo")))
                .andExpect(jsonPath("$.tipo", is("SECO")))
                .andExpect(jsonPath("$.quantidade").value(is(400.0), Double.class))
                .andExpect(jsonPath("$.unidadeMedida", is("kg")))
                .andExpect(jsonPath("$.compartimentoId", is(1)))
                .andExpect(jsonPath("$.compartimentoCodigo", is("C1")))
                .andExpect(jsonPath("$.responsavelUltimaMovimentacao", is("pedro")));
    }

    @Test
    void deveListarIngredientesCadastrados() throws Exception {
        cadastrarIngredienteFarinha();

        mockMvc.perform(get("/ingredientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome", is("Farinha de Trigo")))
                .andExpect(jsonPath("$[0].tipo", is("SECO")))
                .andExpect(jsonPath("$[0].compartimentoCodigo", is("C1")));
    }

    @Test
    void deveBuscarIngredientePorId() throws Exception {
        cadastrarIngredienteFarinha();

        mockMvc.perform(get("/ingredientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.nome", is("Farinha de Trigo")))
                .andExpect(jsonPath("$.tipo", is("SECO")));
    }

    @Test
    void deveRetornarErroQuandoCampoObrigatorioNaoForInformado() throws Exception {
        String body = """
                {
                  "tipo": "SECO",
                  "quantidade": 400,
                  "compartimentoId": 1,
                  "responsavel": "pedro"
                }
                """;

        mockMvc.perform(post("/ingredientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Erro de validação")))
                .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                .andExpect(jsonPath("$.fieldErrors[0].field", is("nome")));
    }

    @Test
    void deveRetornarErroQuandoCapacidadeForExcedida() throws Exception {
        String body = """
                {
                  "nome": "Açúcar",
                  "tipo": "SECO",
                  "quantidade": 700,
                  "compartimentoId": 1,
                  "responsavel": "maria"
                }
                """;

        mockMvc.perform(post("/ingredientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Regra de negócio violada")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("excede a capacidade máxima")));
    }

    private void cadastrarIngredienteFarinha() throws Exception {
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
}
