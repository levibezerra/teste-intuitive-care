#  Tecnologias e Ferramentas Utilizadas

- ### **Java 17**
- ### **Spring Boot 4.0.2**
- ### **Spring Web MVC**
- ### **Spring WebFlux (WebClient)**
- ### **Java HttpClient (`java.net.http`)**
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

### `git clone https://github.com/levibezerra/teste-intuitive-care.git`

---

# Build do Projeto

## Para baixar as dependências e compilar o projeto:

### `mvn clean package`

### Se tudo estiver correto, o build será concluído sem erros

---

# Antes de executar adicione na pasta data/input o arquivo:

### `consolidado_despesas.csv`

---

# Execução do Teste de Transformação e Validação de Dados

## A execução da aplicação dispara automaticamente todas as etapas do teste, sem necessidade de configuração adicional.

### `mvn spring-boot:run`

---

# Arquivos Gerados

## Ao final da execução, os arquivos estarão disponíveis em:

```
data/
└── input/
      ├── consolidado_despesas.csv
    output/
      ├── despesas_agregadas.csv
      └── Teste_Levi.zip
```
---

# Estrutura do CSV final


### `RegistroANS;RazaoSocial;Modalidade;UF;TotalDespesas;MediaTrimestral;DesvioPadrao`

---

# Transformação e Validação de Dados

---

## Objetivo

### O objetivo deste teste é processar e consolidar os dados de despesas das operadoras de saúde, aplicando três etapas principais:

- ### **Validação dos dados de entrada**
- ### **Enriquecimento dos dados** com informações cadastrais das operadoras
- ### **Agregação e cálculo de métricas estatísticas**

## Resultado final

- ### Arquivo CSV:  
### `despesas_agregadas.csv`
- ### Arquivo ZIP:  
### `Teste_Levi.zip`

### O CSV final é **ordenado pelo valor total de despesas (decrescente)** e contém apenas registros válidos e consistentes.

---

# Validação de Dados

## CSV consolidado gerado no Teste 1:  
### `consolidado_despesas.csv`

# Validações aplicadas

- ## **CNPJ válido**
    - ### Verificação de formato
    - ### Validação dos dígitos verificadores
- ## **Valores numéricos positivos**
    - ### Garante que `ValorDespesas > 0`
- ## **Razão Social preenchida**
    - ### Evita registros com campo vazio ou nulo

---

# Trade-off técnico – Tratamento de CNPJs inválidos

## **Decisão adotada**  

### Registros com **CNPJs inválidos são descartados** do processamento.

## **Prós**
- ### Mantém a integridade do *join* com o cadastro da ANS
- ### Evita erros no enriquecimento e na agregação

## **Contras**
- ### Possível perda de dados caso o CNPJ esteja apenas parcialmente incorreto

## **Justificativa**  
### Para o volume estimado (~45 mil registros), é mais seguro **descartar registros inválidos** do que tentar corrigir CNPJs automaticamente, evitando inconsistências e erros silenciosos nos dados finais.

---

# Enriquecimento de Dados

## CSV de cadastro das operadoras ativas (`Relatorio_cadop.csv`), obtido em:  

### `https://dadosabertos.ans.gov.br/FTP/PDA/operadoras_de_plano_de_saude_ativas/`

# Processamento

- ## Realizado *join* entre:
    - ### `consolidado_despesas.csv`
    - ### Cadastro ANS
- ## Chave de junção:
    - ### **6 primeiros dígitos do CNPJ (RegistroANS)**

# Colunas adicionadas ao CSV final

- ### `RegistroANS`
- ### `Modalidade`
- ### `UF`

---

# Trade-offs técnicos – Tratamento de falhas no enriquecimento

- ## **CNPJs sem correspondência no cadastro**
    - ### Registros descartados
- ## **CNPJs duplicados no cadastro**
    - ### Utilizado o **primeiro registro encontrado** (*policy: first-match wins*)

# **Justificativa**  
## A estratégia *first-match wins* garante:
- ### Processamento eficiente
- ### Evita sobreposição de dados conflitantes

## Registros sem correspondência no cadastro poderiam comprometer a agregação e a confiabilidade dos resultados, sendo descartados por segurança.

---

# Agregação de Dados

## Agrupamento

### Os dados enriquecidos são agrupados por:

- ### `RegistroANS`
- ### `UF`

# Cálculos realizados

- ## **TotalDespesas**
    - ### Soma das despesas consolidadas por operadora/UF
- ## **MediaTrimestral**
    - ### Média das despesas por trimestre
- ## **DesvioPadrao**
    - ### Medida de variação das despesas trimestrais, permitindo identificar oscilações relevantes

# Ordenação

- ## Ordenação **decrescente** por `TotalDespesas` (maior → menor)

---

# Trade-off técnico – Estratégia de agregação e ordenação

## **Decisão adotada**  

### Uso de **Streams do Java** para agrupamento, cálculos e ordenação *in-memory*.

## **Prós**

- ### Código simples e legível
- ### Fácil manutenção
- ### Totalmente adequado para ~45 mil registros

## **Contras**

- ### Poderia não escalar bem para milhões de registros

# **Justificativa**  

## Para o contexto do teste, a abordagem com streams oferece o melhor equilíbrio entre **clareza**, **segurança** e **desempenho suficiente**.

---