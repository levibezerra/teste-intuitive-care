package com.levi.teste_intuitiveCare.config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Component
public class AnsApiClientConfig {

    private static final String BASE_URL =
            "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public List<TrimestreRefConfig> buscarUltimosTrimestres(int quantidade) {

        List<TrimestreRefConfig> encontrados = new ArrayList<>();
        int anoAtual = Year.now().getValue();

        for (int ano = anoAtual; ano >= anoAtual - 2; ano--) {
            for (int trimestre = 4; trimestre >= 1; trimestre--) {

                if (encontrados.size() >= quantidade) {
                    return encontrados;
                }

                String url = montarUrlZip(ano, trimestre);

                try {
                    if (arquivoExiste(url)) {
                        encontrados.add(new TrimestreRefConfig(ano, trimestre));
                        System.out.println("Trimestre disponível: " + trimestre + "T" + ano);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return encontrados;
    }

    public List<ArquivoAnsRefConfig> listarArquivos(TrimestreRefConfig ref) {

        String url = montarUrlZip(ref.getAno(), ref.getTrimestre());

        return List.of(new ArquivoAnsRefConfig(
                ref.getTrimestre() + "T" + ref.getAno() + ".zip",
                url
        ));
    }

    public InputStream baixarArquivo(ArquivoAnsRefConfig arq) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(arq.getUrl()))
                    .timeout(Duration.ofMinutes(5))
                    .GET()
                    .build();

            HttpResponse<InputStream> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro HTTP: " + response.statusCode());
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar arquivo ANS", e);
        }
    }

    private boolean arquivoExiste(String url)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("HEAD", HttpRequest.BodyPublishers.noBody())
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<Void> response =
                httpClient.send(request, HttpResponse.BodyHandlers.discarding());

        return response.statusCode() == 200;
    }

    private String montarUrlZip(int ano, int trimestre) {
        return String.format(
                "%s/%d/%dT%d.zip",
                BASE_URL,
                ano,
                trimestre,
                ano
        );
    }
}