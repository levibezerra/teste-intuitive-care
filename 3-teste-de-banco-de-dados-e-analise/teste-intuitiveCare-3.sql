CREATE TABLE despesas_consolidadas (
    id BIGSERIAL PRIMARY KEY,
    cnpj VARCHAR(20),
    razao_social TEXT,
    trimestre VARCHAR(2),
    ano INT,
    valor_despesas NUMERIC(18,2)
);

SELECT * FROM despesas_consolidadas LIMIT 5;

CREATE TABLE operadoras_cadastro (
    registro_ans VARCHAR(10) PRIMARY KEY,
    cnpj VARCHAR(20),
    razao_social TEXT,
    modalidade TEXT,
    uf CHAR(2)
);

CREATE INDEX idx_operadoras_cnpj ON operadoras_cadastro (cnpj);
CREATE INDEX idx_operadoras_uf ON operadoras_cadastro (uf);

SELECT * FROM operadoras_cadastro

CREATE TABLE operadoras_cadastro_staging (
    registro_ans VARCHAR(10),
    cnpj VARCHAR(20),
    razao_social TEXT,
    nome_fantasia TEXT,
    modalidade TEXT,
    logradouro TEXT,
    numero TEXT,
    complemento TEXT,
    bairro TEXT,
    cidade TEXT,
    uf CHAR(2),
    cep TEXT,
    ddd TEXT,
    telefone TEXT,
    fax TEXT,
    email TEXT,
    representante TEXT,
    cargo_representante TEXT,
    regiao_comercializacao INT,
    data_registro_ans DATE
);

INSERT INTO operadoras_cadastro (
    registro_ans,
    cnpj,
    razao_social,
    modalidade,
    uf
)
SELECT
    registro_ans,
    cnpj,
    razao_social,
    modalidade,
    uf
FROM operadoras_cadastro_staging
WHERE registro_ans IS NOT NULL;

DROP TABLE operadoras_cadastro_staging;

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

SELECT * FROM despesas_agregadas

CREATE TABLE despesas_agregadas_staging (
    registro_ans TEXT,
    razao_social TEXT,
    modalidade TEXT,
    uf CHAR(2),
    total_despesas TEXT,
    media_trimestral TEXT,
    desvio_padrao TEXT
);

DROP TABLE despesas_agregadas_staging;

INSERT INTO despesas_agregadas (
    registro_ans,
    razao_social,
    modalidade,
    uf,
    total_despesas,
    media_trimestral,
    desvio_padrao
)
SELECT
    registro_ans,
    razao_social,
    modalidade,
    uf,
    REPLACE(total_despesas, ',', '.')::NUMERIC(18,2),
    REPLACE(media_trimestral, ',', '.')::NUMERIC(18,2),
    REPLACE(desvio_padrao, ',', '.')::NUMERIC(18,2)
FROM despesas_agregadas_staging;

QUERY 1

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

Nome das 5 operadoras com maior crescimento percentual de despesas

SELECT
    registro_ans,
    razao_social,
    uf,
    total_despesas,
    media_trimestral,
    desvio_padrao
FROM despesas_agregadas
WHERE registro_ans IN (
    '417173',
    '423815',
    '421642',
    '324523',
    '414905'
)
ORDER BY total_despesas DESC;

QUERY 2

SELECT
    uf,
    SUM(total_despesas) AS total_despesas_uf,
    AVG(total_despesas) AS media_despesas_por_operadora
FROM despesas_agregadas
GROUP BY uf
ORDER BY total_despesas_uf DESC
LIMIT 5;

QUERY 3

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