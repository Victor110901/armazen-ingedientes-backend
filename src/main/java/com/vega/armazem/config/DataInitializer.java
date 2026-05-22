package com.vega.armazem.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vega.armazem.entity.Compartimento;
import com.vega.armazem.repository.CompartimentoRepository;

@Configuration
public class DataInitializer {

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Bean
    CommandLineRunner inicializarCompartimentos(CompartimentoRepository compartimentoRepository) {
        return args -> {
            if (!seedEnabled) {
                return;
            }

            List<String> codigosCompartimentos = List.of("C1", "C2", "C3", "C4", "C5");

            for (String codigo : codigosCompartimentos) {
                if (!compartimentoRepository.existsByCodigo(codigo)) {
                    compartimentoRepository.save(new Compartimento(codigo));
                }
            }
        };
    }
}