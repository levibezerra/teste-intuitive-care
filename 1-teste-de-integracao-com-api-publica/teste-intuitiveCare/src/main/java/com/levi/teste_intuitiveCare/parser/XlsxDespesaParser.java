package com.levi.teste_intuitiveCare.parser;

import com.levi.teste_intuitiveCare.dto.DespesaDTO;
import com.levi.teste_intuitiveCare.parser.repository.DespesaParserRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.function.Consumer;

public class XlsxDespesaParser implements DespesaParserRepository {

    @Override
    public void parse(InputStream in, Consumer<DespesaDTO> consumer) {

        try (Workbook workbook = new XSSFWorkbook(in)) {

            Sheet sheet = workbook.getSheetAt(0);
            boolean primeira = true;

            for (Row row : sheet) {

                if (primeira) {
                    primeira = false;
                    continue;
                }

                if (row.getCell(0) == null || row.getCell(1) == null) continue;

                String descricao = row.getCell(1).getStringCellValue().toUpperCase();

                if (!(descricao.contains("DESPESA")
                        && (descricao.contains("EVENTO") || descricao.contains("SINISTRO")))) {
                    continue;
                }

                if (row.getCell(3) == null) continue;

                BigDecimal valor;
                try {
                    valor = BigDecimal.valueOf(row.getCell(3).getNumericCellValue());
                } catch (Exception e) {
                    continue;
                }

                DespesaDTO d = new DespesaDTO();
                d.setCnpj(row.getCell(0).getStringCellValue());
                d.setRazaoSocial(row.getCell(1).getStringCellValue());
                d.setValorDespesas(valor);

                consumer.accept(d);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar XLSX ANS", e);
        }
    }
}