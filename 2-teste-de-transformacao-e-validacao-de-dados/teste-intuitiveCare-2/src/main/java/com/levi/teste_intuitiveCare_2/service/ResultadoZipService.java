package com.levi.teste_intuitiveCare_2.service;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ResultadoZipService {

    public void zipar(Path arquivoCsv, Path zipDestino) {

        try {
            Files.createDirectories(zipDestino.getParent());

            try (ZipOutputStream zos =
                         new ZipOutputStream(Files.newOutputStream(zipDestino))) {

                ZipEntry entry = new ZipEntry(arquivoCsv.getFileName().toString());
                zos.putNextEntry(entry);

                Files.copy(arquivoCsv, zos);

                zos.closeEntry();
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar arquivo ZIP", e);
        }
    }
}