package com.levi.teste_intuitiveCare_2.repository.validator;

import com.levi.teste_intuitiveCare_2.model.DespesaConsolidadaModel;
import com.levi.teste_intuitiveCare_2.repository.DespesaValidatorRepository;
import org.springframework.stereotype.Component;

@Component
public class CnpjValidator implements DespesaValidatorRepository {

    @Override
    public boolean isValid(DespesaConsolidadaModel d) {

        if (d.getCnpj() == null || d.getCnpj().isBlank()) {
            return false;
        }

        String cnpj = d.getCnpj().replaceAll("\\D", "");

        return !cnpj.isBlank();
    }
}