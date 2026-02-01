package com.levi.teste_intuitiveCare_2.config;

import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class AnsCadastroApiClient {

    private static final String CSV_URL =
            "https://dadosabertos.ans.gov.br/FTP/PDA/operadoras_de_plano_de_saude_ativas/Relatorio_cadop.csv";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public InputStream baixarCsvOperadoras() {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CSV_URL))
                    .GET()
                    .build();

            HttpResponse<InputStream> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro ao baixar CSV da ANS. HTTP: " + response.statusCode());
            }

            return response.body();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao acessar CSV da ANS", e);
        }
    }
}