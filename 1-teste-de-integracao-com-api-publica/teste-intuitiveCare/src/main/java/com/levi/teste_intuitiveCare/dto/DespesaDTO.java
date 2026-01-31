package com.levi.teste_intuitiveCare.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class DespesaDTO {

    private String cnpj;
    private String razaoSocial;
    private int ano;
    private String trimestre;
    private BigDecimal valorDespesas;
}