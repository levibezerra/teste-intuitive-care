package com.levi.teste_intuitiveCare_2.config;

import com.levi.teste_intuitiveCare_2.model.DespesaConsolidadaModel;
import com.levi.teste_intuitiveCare_2.model.DespesaEnriquecidaModel;
import com.levi.teste_intuitiveCare_2.model.OperadoraCadastroModel;
import com.levi.teste_intuitiveCare_2.model.ResultadoAgregadoModel;
import com.levi.teste_intuitiveCare_2.service.AgregacaoService;
import com.levi.teste_intuitiveCare_2.service.DespesaValidationService;
import com.levi.teste_intuitiveCare_2.service.EnriquecimentoService;
import com.levi.teste_intuitiveCare_2.service.ResultadoZipService;
import com.levi.teste_intuitiveCare_2.service.reader.DespesaCsvReader;
import com.levi.teste_intuitiveCare_2.service.reader.OperadoraCsvReader;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TransformacaoRunnerConfig implements CommandLineRunner {

    private final DespesaCsvReader reader;
    private final DespesaValidationService validator;
    private final OperadoraCsvReader cadastroReader;
    private final AnsCadastroApiClient cadastroApiClient;
    private final EnriquecimentoService enrich;
    private final AgregacaoService aggregate;
    private final ResultadoCsvWriterConfig writer;
    private final ResultadoZipService zipService;

    @Override
    public void run(String... args) {

        System.out.println("Iniciando Teste 2 – Transformação e Validação de Dados");

        List<DespesaConsolidadaModel> dados =
                reader.read(Path.of("data/input/consolidado_despesas.csv"));

        dados = validator.validar(dados);

        Map<String, OperadoraCadastroModel> cadastro;
        try (InputStream in = cadastroApiClient.baixarCsvOperadoras()) {
            cadastro = cadastroReader.read(in);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar/ler cadastro de operadoras", e);
        }

        List<DespesaEnriquecidaModel> enriquecido =
                enrich.enriquecer(dados, cadastro);

        List<ResultadoAgregadoModel> resultado =
                aggregate.agregar(enriquecido);

        Path csvFinal = Path.of("data/output/despesas_agregadas.csv");
        Path zipFinal = Path.of("data/output/Teste_Levi.zip");

        writer.write(csvFinal, resultado);
        zipService.zipar(csvFinal, zipFinal);

        System.out.println("Teste 2 finalizado com sucesso");
    }
}