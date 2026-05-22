package com.vega.armazem.integration;

import static org.hamcrest.Matchers.hasItem;
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
class ConsultaEstoqueIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        cadastrarIngrediente("""
                {
                  "nome": "Farinha de Trigo",
                  "tipo": "SECO",
                  "quantidade": 400,
                  "compartimentoId": 1,
                  "responsavel": "pedro"
                }
                """);

        cadastrarIngrediente("""
                {
                  "nome": "Leite",
                  "tipo": "LIQUIDO",
                  "quantidade": 200,
                  "compartimentoId": 2,
                  "responsavel": "ana"
                }
                """);

        cadastrarIngrediente("""
                {
                  "nome": "Frango Resfriado",
                  "tipo": "REFRIGERADO",
                  "quantidade": 150,
                  "compartimentoId": 3,
                  "responsavel": "joao"
                }
                """);
    }

    @Test
    void deveConsultarVolumeTotalPorTipo() throws Exception {
        mockMvc.perform(get("/ingredientes/volume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].type", hasItem("SECO")))
                .andExpect(jsonPath("$[*].type", hasItem("LIQUIDO")))
                .andExpect(jsonPath("$[*].type", hasItem("REFRIGERADO")));
    }

    @Test
    void deveConsultarCompartimentosDisponiveisParaArmazenamento() throws Exception {
        mockMvc.perform(get("/compartimentos/disponiveis")
                        .param("quantidade", "100")
                        .param("tipo", "SECO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].codigo", hasItem("C1")))
                .andExpect(jsonPath("$[*].codigo", hasItem("C4")))
                .andExpect(jsonPath("$[*].codigo", hasItem("C5")));
    }

    @Test
    void deveConsultarCompartimentosDisponiveisParaVenda() throws Exception {
        mockMvc.perform(get("/compartimentos/disponiveis-para-venda")
                        .param("tipo", "SECO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].codigo", is("C1")))
                .andExpect(jsonPath("$[0].quantidadeAtual", is(400.0)));
    }

    private void cadastrarIngrediente(String body) throws Exception {
        mockMvc.perform(post("/ingredientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }
}