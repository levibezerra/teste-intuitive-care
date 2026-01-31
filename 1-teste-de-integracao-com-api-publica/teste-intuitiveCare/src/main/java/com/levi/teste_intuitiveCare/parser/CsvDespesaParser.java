package com.levi.teste_intuitiveCare.parser;

import com.levi.teste_intuitiveCare.dto.DespesaDTO;
import com.levi.teste_intuitiveCare.parser.repository.DespesaParserRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class CsvDespesaParser implements DespesaParserRepository {

    @Override
    public void parse(InputStream in, Consumer<DespesaDTO> consumer) {

        try (Reader reader = new InputStreamReader(in, StandardCharsets.ISO_8859_1)) {

            CSVParser parser = CSVFormat.DEFAULT
                    .withDelimiter(';')
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
                    .parse(reader);

            System.out.println("Processando CSV ANS");
            System.out.println("Headers encontrados: " + parser.getHeaderMap().keySet());

            for (CSVRecord r : parser) {

                String descricao = getCampo(r, "DESCRICAO");
                if (descricao == null) continue;

                String desc = descricao.toUpperCase();

                if (!(desc.contains("DESPESA")
                        && (desc.contains("EVENTO") || desc.contains("SINISTRO")))) {
                    continue;
                }

                String valor = getCampo(r, "VL_SALDO_FINAL");
                if (valor == null || valor.isBlank()) continue;

                valor = valor.replace(".", "").replace(",", ".");

                DespesaDTO d = new DespesaDTO();
                d.setCnpj(getCampo(r, "REG_ANS"));
                d.setRazaoSocial(descricao);
                d.setValorDespesas(new BigDecimal(valor));

                consumer.accept(d);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar CSV ANS", e);
        }
    }

    private String getCampo(CSVRecord r, String... nomes) {
        for (String nome : nomes) {
            if (r.isMapped(nome)) {
                return r.get(nome);
            }
        }
        return null;
    }
}