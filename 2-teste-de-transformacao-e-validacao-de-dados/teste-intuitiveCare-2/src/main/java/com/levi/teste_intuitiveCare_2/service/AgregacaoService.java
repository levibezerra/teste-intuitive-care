package com.levi.teste_intuitiveCare_2.service;

import com.levi.teste_intuitiveCare_2.model.DespesaEnriquecidaModel;
import com.levi.teste_intuitiveCare_2.model.ResultadoAgregadoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AgregacaoService {

    public List<ResultadoAgregadoModel> agregar(List<DespesaEnriquecidaModel> dados) {

        log.info("Agregação: entrada: {}", dados.size());

        Map<String, List<DespesaEnriquecidaModel>> grupos =
                dados.stream()
                        .collect(Collectors.groupingBy(
                                d -> d.getRegistroANS() + "|" + d.getUf()
                        ));

        List<ResultadoAgregadoModel> resultado = grupos.entrySet().stream()
                .map(e -> calcular(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ResultadoAgregadoModel::getTotalDespesas).reversed())
                .collect(Collectors.toList());

        log.info("Agregação: saída: {}", resultado.size());
        return resultado;
    }

    private ResultadoAgregadoModel calcular(String chave, List<DespesaEnriquecidaModel> lista) {

        DespesaEnriquecidaModel ref = lista.get(0);

        BigDecimal total = lista.stream()
                .map(DespesaEnriquecidaModel::getValorDespesas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal media = total.divide(
                BigDecimal.valueOf(lista.size()),
                2,
                RoundingMode.HALF_UP
        );

        BigDecimal variancia = lista.stream()
                .map(d -> {
                    BigDecimal diff = d.getValorDespesas().subtract(media);
                    return diff.multiply(diff);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(lista.size()),
                        6,
                        RoundingMode.HALF_UP
                );

        BigDecimal desvioPadrao = BigDecimal.valueOf(
                Math.sqrt(variancia.doubleValue())
        ).setScale(2, RoundingMode.HALF_UP);

        ResultadoAgregadoModel r = new ResultadoAgregadoModel();
        r.setRegistroANS(ref.getRegistroANS());
        r.setRazaoSocial(ref.getRazaoSocial());
        r.setModalidade(ref.getModalidade());
        r.setUf(ref.getUf());
        r.setTotalDespesas(total);
        r.setMediaTrimestral(media);
        r.setDesvioPadrao(desvioPadrao);

        return r;
    }
}