#  Tecnologias e Ferramentas Utilizadas

- ### **Java 17**
- ### **Spring Boot 4.0.2**
- ### **Spring Web MVC**
- ### **Spring WebFlux (WebClient)**
- ### **Java HttpClient (`java.net.http`)**
- ### **Apache Commons CSV**
- ### **Apache POI (poi-ooxml)**
- ### **ZIP e Streams Java (`java.util.zip`)**
- ### **Lombok**
- ### **Maven**


---

# Como Executar o Projeto

## Pré-requisitos:

## Antes de executar o projeto, é necessário ter instalado:

- ### Java JDK 17
- ### Maven 3.8+
- ### Git
- ### Acesso à internet(para consumo da API pública da ANS)

---

# Clonando o Repositório (Git)

```
git clone https://github.com/levibezerra/teste-intuitive-care.git
```

---

# Build do Projeto

## Para baixar as dependências e compilar o projeto:

```
mvn clean package
```

### Se tudo estiver correto, o build será concluído sem erros

---

# Execução do Teste de Integração com a ANS

## A execução da aplicação dispara automaticamente todas as etapas do teste, sem necessidade de configuração adicional.

```
mvn spring-boot:run
```

---

# O Que Acontece Durante a Execução

## Durante a execução, o sistema realiza automaticamente:

- ### Conexão com a API pública da ANS

- ### Identificação dos últimos 3 trimestres disponíveis

- ### Download dos arquivos ZIP correspondentes

- ### Extração automática dos arquivos

- ### Processamento de arquivos nos formatos: CSV, TXT, XLSX

- ### Filtragem de Despesas com Eventos / Sinistros

- ### Consolidação dos dados válidos

- ### Geração do CSV consolidado

- ### Compactação do CSV em um arquivo ZIP

## Exemplo de logs no console:

```
Trimestre disponível: 3T2025
Trimestre disponível: 2T2025
Trimestre disponível: 1T2025
Processando CSV ANS
Gerando CSV consolidado...
Arquivo consolidado gerado com sucesso
```

---

# Arquivos Gerados

## Ao final da execução, os arquivos estarão disponíveis em:

```
data/
└── output/
├── consolidado_despesas.csv
└── consolidado_despesas.zip
```
---

## Estrutura do CSV


```
CNPJ,RazaoSocial,Trimestre,Ano,ValorDespesas
```
---

# Teste Técnico – Integração com API Pública (ANS)

### Este repositório contém a solução desenvolvida para o **processo seletivo**, com foco em **integração com API pública** e processamento de dados de **Demonstrações Contábeis da ANS (Agência Nacional de Saúde Suplementar)**.

### A implementação prioriza **robustez**, **baixo consumo de memória**, **escalabilidade** e **fidelidade aos dados oficiais**, mesmo diante de arquivos grandes e inconsistências nos dados de origem.

---

# Estratégia de Processamento dos Arquivos (Trade-off Técnico)

## - Decisão adotada

### O processamento dos arquivos foi realizado de forma **incremental (streaming)**, evitando o carregamento de todos os dados em memória simultaneamente.

---

# Justificativa técnica

## Os arquivos disponibilizados pela ANS:

- ### Possuem **grande volume de dados**
- ### Variam em **tamanho e estrutura** entre trimestres
- ### São distribuídos em arquivos ZIP contendo múltiplos arquivos internos

## O carregamento completo em memória poderia causar:

- ### Alto consumo de memória
- ### Risco de `OutOfMemoryError`
- ### Baixa escalabilidade com a adição de novos períodos

---

# Como foi implementado

- ### Cada arquivo ZIP é **baixado individualmente**
- ### Os arquivos internos são **extraídos e processados um por vez**
- ### O parser consome o `InputStream` **linha a linha**
- ### Cada despesa válida é enviada imediatamente para consolidação via  
  `Consumer<DespesaDTO>`

---

# Benefícios da abordagem

- ### Baixo uso de memória
- ### Maior robustez frente a arquivos grandes ou inesperados
- ### Continuidade do processamento mesmo se um arquivo falhar

---

# Análise Crítica e Tratamento de Inconsistências

### Durante o processamento e consolidação dos dados, foram identificadas inconsistências comuns nos arquivos da ANS. As decisões abaixo visam preservar a **integridade**, **transparência** e **rastreabilidade** das informações.

---

# CNPJs duplicados com razões sociais diferentes

## **Situação encontrada**  

- ### Um mesmo CNPJ pode aparecer associado a diferentes descrições ou razões sociais.
- ### **Tratamento adotado**  
- ### Os registros **não são deduplicados nem corrigidos automaticamente**.

## **Justificativa**

- ### A ANS utiliza descrições contábeis que nem sempre representam a razão social oficial
- ### Não há garantia de que uma das descrições esteja correta
- ### Correções automáticas poderiam introduzir erro de negócio

## **Decisão final**  

- ### Todos os registros são mantidos conforme recebidos, preservando a fidelidade aos dados oficiais.

---

# Valores zerados ou negativos

## **Situação encontrada**  

- ### Alguns registros apresentam valores de despesa iguais a zero ou negativos.

## **Tratamento adotado**  

- ### Esses registros são **ignorados** na geração do CSV final.

## **Implementação no código**

```
if (d.getValorDespesas().compareTo(BigDecimal.ZERO) <= 0) continue;
```

## Justificativa

- ### Valores zerados ou negativos não representam despesas efetivas

- ### Evita distorções nos dados consolidados

- ### Facilita análises posteriores, focando apenas em despesas reais

---

# Trimestres com formatos de data inconsistentes

## Situação encontrada

- ### Os arquivos da ANS podem apresentar formatos variados de datas e nomenclaturas de trimestre.

## Tratamento adotado

- ### O trimestre não é inferido a partir do conteúdo do arquivo, mas sim do contexto da URL e da estrutura de diretórios.

## Implementação

- ### O trimestre é representado pela classe `TrimestreRef`

- ### O valor é padronizado no formato:

```
<trimestre>T<ano>
Exemplo: 3T2025
```

```
d.setAno(t.getAno());
d.setTrimestre(t.getTrimestre() + "T");
```

## Justificativa

- ### Garante consistência entre todos os registros

- ### Evita erros causados por dados mal formatados

- ### Mantém rastreabilidade clara do período de origem