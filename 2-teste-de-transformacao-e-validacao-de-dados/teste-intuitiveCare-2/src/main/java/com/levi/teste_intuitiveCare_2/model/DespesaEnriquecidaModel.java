package com.levi.teste_intuitiveCare_2.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class DespesaEnriquecidaModel {

    private String cnpj;
    private String razaoSocial;
    private String uf;
    private int ano;
    private int trimestre;
    private BigDecimal valorDespesas;
    private String registroANS;
    private String modalidade;
}