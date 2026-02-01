package com.levi.teste_intuitiveCare_2.repository;

import com.levi.teste_intuitiveCare_2.model.DespesaConsolidadaModel;
import org.springframework.stereotype.Repository;

@Repository
public interface DespesaValidatorRepository {

    boolean isValid(DespesaConsolidadaModel despesa);
}