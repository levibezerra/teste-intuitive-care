package com.levi.teste_intuitiveCare.service;

import com.levi.teste_intuitiveCare.config.TrimestreRefConfig;
import com.levi.teste_intuitiveCare.dto.DespesaDTO;
import com.levi.teste_intuitiveCare.parser.repository.DespesaParserRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ConsolidacaoService {

    private final List<DespesaDTO> dados = new ArrayList<>();

    public void processarStream(
            InputStream in,
            DespesaParserRepository parser,
            TrimestreRefConfig t) {

        try {
            parser.parse(in, d -> {
                System.out.println("Despesa capturada: " + d.getRazaoSocial()
                        + " | Valor: " + d.getValorDespesas());
                d.setAno(t.getAno());
                d.setTrimestre(t.getTrimestre() + "T");
                dados.add(d);
            });
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar stream da ANS", e);
        }
    }

    public void gerarCsvFinal() {
        try {
            Path csv = Paths.get("data/output/consolidado_despesas.csv");
            Path zip = Paths.get("data/output/consolidado_despesas.zip");
            Files.createDirectories(csv.getParent());

            System.out.println("Gerando CSV consolidado...");
            System.out.println("Total de registros consolidados: " + dados.size());

            try (BufferedWriter w = Files.newBufferedWriter(csv)) {

                w.write("CNPJ,RazaoSocial,Trimestre,Ano,ValorDespesas");
                w.newLine();

                for (DespesaDTO d : dados) {

                    if (d.getValorDespesas() == null) continue;
                    if (d.getValorDespesas().compareTo(BigDecimal.ZERO) <= 0) continue;

                    w.write(String.join(",",
                            safe(d.getCnpj()),
                            safe(d.getRazaoSocial()),
                            safe(d.getTrimestre()),
                            String.valueOf(d.getAno()),
                            d.getValorDespesas().toPlainString()
                    ));
                    w.newLine();
                }
            }

            try (ZipOutputStream zos =
                         new ZipOutputStream(Files.newOutputStream(zip))) {

                ZipEntry entry = new ZipEntry(csv.getFileName().toString());
                zos.putNextEntry(entry);
                Files.copy(csv, zos);
                zos.closeEntry();
            }

            System.out.println("Arquivo consolidado gerado com sucesso:");
            System.out.println(csv.toAbsolutePath());
            System.out.println(zip.toAbsolutePath());

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar CSV consolidado", e);
        }
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace(",", " ").replace("\"", "");
    }
}