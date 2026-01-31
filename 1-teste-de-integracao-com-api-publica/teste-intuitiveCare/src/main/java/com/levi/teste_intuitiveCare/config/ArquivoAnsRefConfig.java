package com.levi.teste_intuitiveCare.config;

public class ArquivoAnsRefConfig {

    private final String nomeArquivo;
    private final String url;

    public ArquivoAnsRefConfig(String nomeArquivo, String url) {
        this.nomeArquivo = nomeArquivo;
        this.url = url;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public String getUrl() {
        return url;
    }

    public String getExtensao() {
        int i = nomeArquivo.lastIndexOf('.');
        return i > 0 ? nomeArquivo.substring(i + 1).toLowerCase() : "";
    }

    @Override
    public String toString() {
        return nomeArquivo;
    }
}