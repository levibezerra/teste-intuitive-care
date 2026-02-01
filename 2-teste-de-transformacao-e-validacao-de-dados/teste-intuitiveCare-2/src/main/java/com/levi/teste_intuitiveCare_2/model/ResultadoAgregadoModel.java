package com.levi.teste_intuitiveCare_2.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class ResultadoAgregadoModel {

    private String registroANS;
    private String razaoSocial;
    private String modalidade;
    private String uf;
    private BigDecimal totalDespesas;
    private BigDecimal mediaTrimestral;
    private BigDecimal desvioPadrao;
}