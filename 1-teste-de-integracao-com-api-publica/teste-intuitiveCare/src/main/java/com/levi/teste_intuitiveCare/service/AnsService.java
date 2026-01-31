package com.levi.teste_intuitiveCare.service;

import com.levi.teste_intuitiveCare.config.AnsApiClientConfig;
import com.levi.teste_intuitiveCare.config.ArquivoAnsRefConfig;
import com.levi.teste_intuitiveCare.config.TrimestreRefConfig;
import com.levi.teste_intuitiveCare.factory.ParserFactory;
import com.levi.teste_intuitiveCare.parser.repository.DespesaParserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnsService {

    private final AnsApiClientConfig apiClient;
    private final ZipExtractService zipService;
    private final ConsolidacaoService consolidacaoService;

    public void executar() {

        List<TrimestreRefConfig> trimestres =
                apiClient.buscarUltimosTrimestres(3);

        for (TrimestreRefConfig t : trimestres) {

            List<ArquivoAnsRefConfig> arquivos =
                    apiClient.listarArquivos(t);

            for (ArquivoAnsRefConfig arq : arquivos) {

                try (InputStream zip = apiClient.baixarArquivo(arq)) {

                    List<File> extraidos =
                            zipService.extract(zip);

                    for (File f : extraidos) {

                        DespesaParserRepository parser =
                                ParserFactory.get(f.getName());

                        if (parser == null) continue;

                        try (InputStream in = new FileInputStream(f)) {
                            consolidacaoService.processarStream(
                                    in,
                                    parser,
                                    t
                            );
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Erro no trimestre " + t);
                    e.printStackTrace();
                }
            }
        }
        consolidacaoService.gerarCsvFinal();
    }
}