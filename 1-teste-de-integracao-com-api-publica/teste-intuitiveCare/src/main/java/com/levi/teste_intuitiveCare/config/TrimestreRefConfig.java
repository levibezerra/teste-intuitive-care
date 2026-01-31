package com.levi.teste_intuitiveCare.config;

public record TrimestreRefConfig(int ano, int trimestre)
        implements Comparable<TrimestreRefConfig> {

    @Override
    public int compareTo(TrimestreRefConfig o) {
        int c = Integer.compare(o.ano, this.ano);
        return c != 0
                ? c
                : Integer.compare(o.trimestre, this.trimestre);
    }

    public String sufixoZip() {
        return trimestre + "T" + ano;
    }

    public int getAno() {
        return ano;
    }

    public int getTrimestre() {
        return trimestre;
    }

    @Override
    public String toString() {
        return trimestre + "T" + ano;
    }
}