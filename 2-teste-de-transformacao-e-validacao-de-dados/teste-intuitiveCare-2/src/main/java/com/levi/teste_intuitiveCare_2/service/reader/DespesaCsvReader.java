package com.levi.teste_intuitiveCare_2.service.reader;

import com.levi.teste_intuitiveCare_2.model.DespesaConsolidadaModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DespesaCsvReader {

    public List<DespesaConsolidadaModel> read(Path path) {

        log.info("Iniciando leitura do CSV de despesas: {}", path.toAbsolutePath());

        try (BufferedReader reader = Files.newBufferedReader(path)) {

            List<DespesaConsolidadaModel> lista = reader.lines()
                    .skip(1)
                    .filter(l -> l != null && !l.isBlank())
                    .map(this::parse)
                    .toList();

            log.info("DespesaCsvReader: registros lidos: {}", lista.size());
            return lista;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler CSV consolidado", e);
        }
    }

    private DespesaConsolidadaModel parse(String line) {

        String[] c = line.split(",");

        DespesaConsolidadaModel d = new DespesaConsolidadaModel();

        d.setCnpj(c[0].replaceAll("\\D", "").trim());
        d.setRazaoSocial(c[1].trim());
        d.setTrimestre(
                Integer.parseInt(c[2].replace("T", "").trim())
        );
        d.setAno(Integer.parseInt(c[3].trim()));
        d.setValorDespesas(new BigDecimal(c[4].trim()));

        return d;
    }
}