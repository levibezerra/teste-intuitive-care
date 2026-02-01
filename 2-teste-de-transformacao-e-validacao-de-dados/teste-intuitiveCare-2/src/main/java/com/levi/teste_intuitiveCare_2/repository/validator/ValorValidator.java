package com.levi.teste_intuitiveCare_2.repository.validator;

import com.levi.teste_intuitiveCare_2.model.DespesaConsolidadaModel;
import com.levi.teste_intuitiveCare_2.repository.DespesaValidatorRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ValorValidator implements DespesaValidatorRepository {

    @Override
    public boolean isValid(DespesaConsolidadaModel d) {
        return d.getValorDespesas() != null
                && d.getValorDespesas().compareTo(BigDecimal.ZERO) > 0;
    }
}