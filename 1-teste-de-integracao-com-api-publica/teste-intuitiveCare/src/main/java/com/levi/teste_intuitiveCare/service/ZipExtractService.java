package com.levi.teste_intuitiveCare.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipExtractService {

    public List<File> extract(InputStream zipStream) {

        List<File> arquivos = new ArrayList<>();

        try {
            Path destino = Files.createTempDirectory("ans_zip_");

            try (ZipInputStream zis = new ZipInputStream(zipStream)) {
                ZipEntry entry;

                while ((entry = zis.getNextEntry()) != null) {

                    if (entry.isDirectory()) continue;

                    Path file = destino.resolve(entry.getName());
                    Files.createDirectories(file.getParent());

                    Files.copy(zis, file, StandardCopyOption.REPLACE_EXISTING);
                    arquivos.add(file.toFile());
                }
            }

            return arquivos;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao extrair ZIP", e);
        }
    }
}