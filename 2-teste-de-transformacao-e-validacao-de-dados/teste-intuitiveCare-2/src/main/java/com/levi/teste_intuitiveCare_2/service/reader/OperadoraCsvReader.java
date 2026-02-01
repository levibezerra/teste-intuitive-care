package com.levi.teste_intuitiveCare_2.service.reader;

import com.levi.teste_intuitiveCare_2.model.OperadoraCadastroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OperadoraCsvReader {

    public Map<String, OperadoraCadastroModel> read(InputStream inputStream) {

        log.info("Iniciando leitura do cadastro de operadoras ANS");

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(inputStream))) {

            Map<String, OperadoraCadastroModel> mapa = reader.lines()
                    .skip(1)
                    .map(this::parse)
                    .filter(o -> o.getRegistroANS() != null && !o.getRegistroANS().isBlank())
                    .collect(Collectors.toMap(
                            OperadoraCadastroModel::getRegistroANS, // usa código ANS
                            Function.identity(),
                            (a, b) -> a
                    ));

            log.info("OperadoraCsvReader: operadoras carregadas: {}", mapa.size());
            return mapa;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler cadastro ANS", e);
        }
    }

    private OperadoraCadastroModel parse(String line) {

        String[] c = line.replace("\"", "").split(";");

        OperadoraCadastroModel o = new OperadoraCadastroModel();
        o.setRegistroANS(c[0].trim());
        o.setCnpj(c[1].trim());
        o.setRazaoSocial(c[2].trim());
        o.setModalidade(c[4].trim());
        o.setUf(c[10].trim());

        return o;
    }
}