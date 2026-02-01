package com.levi.teste_intuitiveCare_2.repository.validator;

import com.levi.teste_intuitiveCare_2.model.DespesaConsolidadaModel;
import com.levi.teste_intuitiveCare_2.repository.DespesaValidatorRepository;
import org.springframework.stereotype.Component;

@Component
public class RazaoSocialValidator implements DespesaValidatorRepository {

    @Override
    public boolean isValid(DespesaConsolidadaModel d) {
        return d.getRazaoSocial() != null && !d.getRazaoSocial().isBlank();
    }
}