package com.levi.teste_intuitiveCare.parser;

import com.levi.teste_intuitiveCare.dto.DespesaDTO;
import com.levi.teste_intuitiveCare.parser.repository.DespesaParserRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class TxtDespesaParser implements DespesaParserRepository {

    @Override
    public void parse(InputStream in, Consumer<DespesaDTO> consumer) {

        try (BufferedReader br =
                     new BufferedReader(new InputStreamReader(in, StandardCharsets.ISO_8859_1))) {

            String linha;
            boolean primeira = true;

            while ((linha = br.readLine()) != null) {

                if (primeira) {
                    primeira = false;
                    continue;
                }

                String[] partes = linha.split(";");

                if (partes.length < 4) continue;

                String descricao = partes[1].trim().toUpperCase();

                if (!(descricao.contains("DESPESA")
                        && (descricao.contains("EVENTO") || descricao.contains("SINISTRO")))) {
                    continue;
                }

                String valorStr = partes[3].trim();
                if (valorStr.isBlank()) continue;

                valorStr = valorStr.replace(".", "").replace(",", ".");

                BigDecimal valor;
                try {
                    valor = new BigDecimal(valorStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                DespesaDTO d = new DespesaDTO();
                d.setCnpj(partes[0].trim());
                d.setRazaoSocial(partes[1].trim());
                d.setValorDespesas(valor);

                consumer.accept(d);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar TXT ANS", e);
        }
    }
}