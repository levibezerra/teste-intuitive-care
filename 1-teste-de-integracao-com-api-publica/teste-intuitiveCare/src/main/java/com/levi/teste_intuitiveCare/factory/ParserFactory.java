package com.levi.teste_intuitiveCare.factory;

import com.levi.teste_intuitiveCare.parser.CsvDespesaParser;
import com.levi.teste_intuitiveCare.parser.TxtDespesaParser;
import com.levi.teste_intuitiveCare.parser.XlsxDespesaParser;
import com.levi.teste_intuitiveCare.parser.repository.DespesaParserRepository;

import java.io.File;

public class ParserFactory {

    public static DespesaParserRepository get(String nomeArquivo) {

        String nome = nomeArquivo.toLowerCase();

        if (nome.endsWith(".csv")) return new CsvDespesaParser();
        if (nome.endsWith(".txt")) return new TxtDespesaParser();
        if (nome.endsWith(".xlsx")) return new XlsxDespesaParser();

        return null;
    }
}