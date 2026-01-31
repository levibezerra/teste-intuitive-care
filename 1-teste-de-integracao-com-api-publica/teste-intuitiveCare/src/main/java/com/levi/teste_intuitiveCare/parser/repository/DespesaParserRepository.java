package com.levi.teste_intuitiveCare.parser.repository;

import com.levi.teste_intuitiveCare.dto.DespesaDTO;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.InputStream;
import java.util.function.Consumer;

@Repository
public interface DespesaParserRepository {
    void parse(InputStream in, Consumer<DespesaDTO> consumer);
}