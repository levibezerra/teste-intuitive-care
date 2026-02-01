package com.levi.teste_intuitiveCare_2.service;

import com.levi.teste_intuitiveCare_2.model.DespesaConsolidadaModel;
import com.levi.teste_intuitiveCare_2.repository.DespesaValidatorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DespesaValidationService {

    private final List<DespesaValidatorRepository> validators;

    public DespesaValidationService(List<DespesaValidatorRepository> validators) {
        this.validators = validators;
    }

    public List<DespesaConsolidadaModel> validar(List<DespesaConsolidadaModel> dados) {

        log.info("Validator: entrada: {}", dados.size());

        List<DespesaConsolidadaModel> validos = dados.stream()
                .filter(d -> validators.stream().allMatch(v -> v.isValid(d)))
                .toList();

        log.info("Validator: saída: {}", validos.size());
        return validos;
    }
}