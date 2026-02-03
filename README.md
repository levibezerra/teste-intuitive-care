# A documentação dos teste 1 e 2 estão nas suas respectivas pastas no arquivo README.md.

---

# Teste de Banco de Dados e Análise - PostgreSQL

## Introdução

## Foram desenvolvidos:
- ### Scripts DDL para criação das tabelas
- ### Estratégia de importação de dados com tratamento de inconsistências
- ### Queries analíticas para responder às perguntas propostas
- ### Análise detalhada dos trade-offs técnicos adotados

---

## Arquivos Utilizados

## Conforme solicitado, foram utilizados os seguintes arquivos CSV:

- ### `consolidado_despesas.csv`
- ### `despesas_agregadas.csv`
- ### CSV de dados cadastrais das operadoras

---

# Estruturação das Tabelas (DDL)

## Tabela de despesas consolidadas

```sql
CREATE TABLE despesas_consolidadas (
    id BIGSERIAL PRIMARY KEY,
    cnpj VARCHAR(20),
    razao_social TEXT,
    trimestre VARCHAR(2),
    ano INT,
    valor_despesas NUMERIC(18,2)
);
```

### Finalidade: armazenar o histórico trimestral de despesas por operadora.

---

# Tabela de cadastro das operadoras

```sql
CREATE TABLE operadoras_cadastro (
    registro_ans VARCHAR(10) PRIMARY KEY,
    cnpj VARCHAR(20),
    razao_social TEXT,
    modalidade TEXT,
    uf CHAR(2)
);

CREATE INDEX idx_operadoras_cnpj ON operadoras_cadastro (cnpj);
CREATE INDEX idx_operadoras_uf ON operadoras_cadastro (uf);
```
### Finalidade: manter os dados cadastrais oficiais das operadoras, facilitando consultas por CNPJ e UF.

---

# Tabela de despesas agregadas

```sql
CREATE TABLE despesas_agregadas (
    id BIGSERIAL PRIMARY KEY,
    registro_ans VARCHAR(10),
    razao_social TEXT,
    modalidade TEXT,
    uf CHAR(2),
    total_despesas NUMERIC(18,2),
    media_trimestral NUMERIC(18,2),
    desvio_padrao NUMERIC(18,2)
);

CREATE INDEX idx_agregadas_total ON despesas_agregadas (total_despesas DESC);
CREATE INDEX idx_agregadas_uf ON despesas_agregadas (uf);
```

### Finalidade: armazenar métricas agregadas para acelerar consultas analíticas.

---

# Trade-off Técnico — Normalização

## Abordagem escolhida: Modelo híbrido

### Tabelas normalizadas:
- #### `despesas_consolidadas`
- #### `operadoras_cadastro`
  
### Tabela agregada/desnormalizada:
- #### `despesas_agregadas`

# Justificativa

| Critério                  | Decisão                                             |
| ------------------------- | --------------------------------------------------- |
| Volume de dados           | Histórico grande favorece agregações pré-calculadas |
| Frequência de atualização | Baixa (dados históricos), permite ETL periódico     |
| Queries analíticas        | Ganho significativo de performance e simplicidade   |

### Essa abordagem equilibra integridade dos dados, performance e manutenibilidade.

---

# Trade-off Técnico — Tipos de Dados

## Valores monetários

| Tipo                  | Avaliação                          |
| --------------------- | ---------------------------------- |
| FLOAT                 | Impreciso para valores financeiros |
| INTEGER (centavos)    | Exige conversões constantes        |
| **NUMERIC / DECIMAL** | Alta precisão                      |

## Decisão: NUMERIC(18,2)
- ### Garante precisão em cálculos financeiros.

## Datas

| Tipo      | Avaliação                |
| --------- | ------------------------ |
| VARCHAR   | Sem validação            |
| TIMESTAMP | Informação desnecessária |
| **DATE**  | Simples e adequado       |

## Decisão: DATE

- ### Quando aplicável.

---

# Importação dos Dados (CSV)

## Estratégia adotada

- ### Uso de tabelas staging
- ### Conversões explícitas de tipo
- ### Remoção de tabelas temporárias após carga
- ### Tratamento de inconsistências antes da inserção final

## Exemplo de conversão de dados numéricos

- ### `REPLACE(total_despesas, ',', '.')::NUMERIC(18,2)`

# Análise Crítica das Inconsistências

| Problema encontrado          | Tratamento adotado             | Justificativa            |
| ---------------------------- | ------------------------------ | ------------------------ |
| Valores NULL em campos-chave | Registro descartado            | Evita inconsistência     |
| Strings em campos numéricos  | Conversão com `REPLACE` e CAST | Preserva dados válidos   |
| Tipos incompatíveis          | Conversão explícita            | Evita falhas de execução |

---

# Queries Analíticas

## Query 1 — Top 5 operadoras com maior crescimento percentual

### Solução adotada:

- #### Uso de ROW_NUMBER() para identificar o primeiro e último trimestre disponível de cada operadora
- #### Não assume presença em todos os períodos

```sql
WITH despesas_ordenadas AS (
    SELECT
        cnpj,
        ano,
        trimestre,
        SUM(valor_despesas) AS total,
        ROW_NUMBER() OVER (PARTITION BY cnpj ORDER BY ano, trimestre) AS rn_asc,
        ROW_NUMBER() OVER (PARTITION BY cnpj ORDER BY ano DESC, trimestre DESC) AS rn_desc
    FROM despesas_consolidadas
    GROUP BY cnpj, ano, trimestre
),
inicio_fim AS (
    SELECT
        i.cnpj,
        i.total AS total_inicio,
        f.total AS total_fim
    FROM despesas_ordenadas i
    JOIN despesas_ordenadas f
        ON i.cnpj = f.cnpj
    WHERE i.rn_asc = 1
      AND f.rn_desc = 1
)
SELECT
    cnpj,
    ROUND(((total_fim - total_inicio) / total_inicio) * 100, 2) AS crescimento_percentual
FROM inicio_fim
WHERE total_inicio > 0
ORDER BY crescimento_percentual DESC
LIMIT 5;
```

### Resultado

- #### 1- QUALICORP ADMINISTRADORA DE BENEFÍCIOS S.A. (SP) — 5656.31%
- #### 2- SAGRADA SAÚDE ASSISTÊNCIA MÉDICA LTDA (MG) — 3578.01%
- #### 3- EXCELÊNCIA PLANO DE SAÚDE S/A (ES) — 2219.11%
- #### 4- UNIMED PARAÍBA (PB) — 2057.72%
- #### 5- VITA ASSISTÊNCIA À SAÚDE LTDA (MG) — 1565.81%

---

## Query 2 — Distribuição de despesas por UF + média por operadora

```sql
SELECT
    uf,
    SUM(total_despesas) AS total_despesas_uf,
    AVG(total_despesas) AS media_despesas_por_operadora
FROM despesas_agregadas
GROUP BY uf
ORDER BY total_despesas_uf DESC
LIMIT 5;
```

### Resultado

- #### 1- Total: 197.475.237.478,19 | Média: 806.021.377,46
- #### 2- Total: 148.716.439.497,09 | Média: 2.323.694.367,14
- #### 3- Total: 80.059.140.345,91 | Média: 1.861.840.473,16
- #### 4- Total: 33.350.869.588,48 | Média: 326.969.309,69
- #### 5- Total: 30.839.877.505,46 | Média: 2.055.991.833,69

---

## Query 3 — Operadoras acima da média em pelo menos 2 trimestres

### Trade-off técnico:
 - #### Optou-se por CTEs por maior legibilidade e manutenção

```sql
WITH media_geral AS (
    SELECT AVG(valor_despesas) AS media
    FROM despesas_consolidadas
),
trimestres_acima_media AS (
    SELECT
        cnpj,
        ano,
        trimestre
    FROM despesas_consolidadas d
    CROSS JOIN media_geral m
    WHERE d.valor_despesas > m.media
    GROUP BY cnpj, ano, trimestre
),
contagem_trimestres AS (
    SELECT
        cnpj,
        COUNT(*) AS qtd_trimestres
    FROM trimestres_acima_media
    GROUP BY cnpj
)
SELECT
    COUNT(*) AS operadoras_acima_media
FROM contagem_trimestres
WHERE qtd_trimestres >= 2;
```

### Resultado

- #### 368 operadoras tiveram despesas acima da média geral em pelo menos 2 trimestres.
