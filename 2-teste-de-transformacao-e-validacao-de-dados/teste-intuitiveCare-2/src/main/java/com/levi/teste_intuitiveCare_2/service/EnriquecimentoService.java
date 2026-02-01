package com.levi.teste_intuitiveCare_2.service;

import com.levi.teste_intuitiveCare_2.model.DespesaConsolidadaModel;
import com.levi.teste_intuitiveCare_2.model.DespesaEnriquecidaModel;
import com.levi.teste_intuitiveCare_2.model.OperadoraCadastroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EnriquecimentoService {

    public List<DespesaEnriquecidaModel> enriquecer(
            List<DespesaConsolidadaModel> despesas,
            Map<String, OperadoraCadastroModel> cadastro) {

        log.info("Enriquecimento: despesas recebidas: {}", despesas.size());
        log.info("Enriquecimento: cadastro disponível: {}", cadastro.size());

        List<DespesaEnriquecidaModel> enriquecido = despesas.stream()
                .map(d -> {

                    OperadoraCadastroModel o = cadastro.get(d.getCnpj());
                    if (o == null) {
                        log.debug("Enriquecimento → código ANS não encontrado: {}", d.getCnpj());
                        return null;
                    }

                    DespesaEnriquecidaModel e = new DespesaEnriquecidaModel();
                    e.setCnpj(o.getCnpj());
                    e.setRegistroANS(o.getRegistroANS());
                    e.setRazaoSocial(o.getRazaoSocial());
                    e.setModalidade(o.getModalidade());
                    e.setUf(o.getUf());
                    e.setAno(d.getAno());
                    e.setTrimestre(d.getTrimestre());
                    e.setValorDespesas(d.getValorDespesas());
                    return e;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Enriquecimento: registros enriquecidos: {}", enriquecido.size());
        return enriquecido;
    }
}