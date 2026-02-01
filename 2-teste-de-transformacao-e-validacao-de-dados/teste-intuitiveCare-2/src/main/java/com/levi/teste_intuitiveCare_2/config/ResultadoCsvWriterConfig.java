package com.levi.teste_intuitiveCare_2.config;

import com.levi.teste_intuitiveCare_2.model.ResultadoAgregadoModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
public class ResultadoCsvWriterConfig {

    public void write(Path path, List<ResultadoAgregadoModel> dados) {

        log.info("Writer: registros para escrita: {}", dados.size());
        log.info("Writer: arquivo destino: {}", path.toAbsolutePath());

        try {
            Files.createDirectories(path.getParent());

            try (BufferedWriter w = Files.newBufferedWriter(path)) {

                w.write("RegistroANS;RazaoSocial;Modalidade;UF;TotalDespesas;MediaTrimestral;DesvioPadrao\n");

                for (ResultadoAgregadoModel r : dados) {
                    w.write(String.format(
                            "%s;%s;%s;%s;%.2f;%.2f;%.2f\n",
                            r.getRegistroANS(),
                            r.getRazaoSocial(),
                            r.getModalidade(),
                            r.getUf(),
                            r.getTotalDespesas(),
                            r.getMediaTrimestral(),
                            r.getDesvioPadrao()
                    ));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao escrever CSV final", e);
        }
    }
}