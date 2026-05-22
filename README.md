# Armazém de Ingredientes API

API REST para controle de armazenamento e movimentação de ingredientes em uma fábrica de alimentos.

O projeto foi construído com Spring Boot e organiza o domínio em torno de cinco compartimentos físicos de estoque. Cada compartimento possui regras próprias de ocupação, capacidade e troca de tipo de ingrediente. Toda entrada e saída de estoque fica registrada em histórico, permitindo rastreabilidade básica das operações.

## Sumário

- [Visão geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Regras de negócio](#regras-de-negócio)
- [Como executar](#como-executar)
- [Executando com PostgreSQL](#executando-com-postgresql)
- [Testes](#testes)
- [Documentação da API](#documentação-da-api)
- [Endpoints](#endpoints)
- [Exemplos de uso](#exemplos-de-uso)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Banco de dados e migrations](#banco-de-dados-e-migrations)
- [Tratamento de erros](#tratamento-de-erros)
- [Decisões técnicas](#decisões-técnicas)

## Visão geral

A aplicação resolve um cenário de estoque com regras um pouco mais restritivas do que um CRUD simples:

- existem cinco compartimentos fixos, identificados de `C1` a `C5`;
- cada compartimento pode armazenar somente um tipo de ingrediente por vez;
- as capacidades máximas dependem do tipo do ingrediente;
- movimentações de entrada e saída atualizam o saldo do ingrediente e do compartimento;
- toda movimentação gera histórico automaticamente;
- a troca de tipo em um compartimento vazio só é permitida no dia seguinte, caso ele tenha armazenado outro tipo no mesmo dia.

A API foi pensada para ser simples de executar localmente e também próxima de um ambiente real: por padrão roda com H2 em memória, mas já possui profile para PostgreSQL e migrations versionadas com Flyway.

## Tecnologias

- Java 17
- Spring Boot 3.5.14
- Spring Web
- Spring Data JPA
- Bean Validation
- Flyway
- H2 Database
- PostgreSQL
- Docker Compose
- Maven Wrapper
- JUnit 5
- Mockito
- MockMvc
- Springdoc OpenAPI / Swagger UI

## Regras de negócio

### Tipos de ingrediente

| Tipo | Capacidade máxima por compartimento | Unidade |
| --- | ---: | --- |
| `SECO` | 600 | kg |
| `LIQUIDO` | 500 | L |
| `REFRIGERADO` | 400 | kg |

### Compartimentos

O sistema inicia com cinco compartimentos:

| ID inicial | Código |
| ---: | --- |
| 1 | C1 |
| 2 | C2 |
| 3 | C3 |
| 4 | C4 |
| 5 | C5 |

Esses registros são criados pelas migrations do Flyway. Há também um inicializador defensivo na aplicação para garantir os compartimentos caso o seed esteja habilitado.

### Validações principais

- Não é permitido cadastrar ou movimentar quantidade menor ou igual a zero.
- Um compartimento não pode receber ingrediente de tipo diferente do tipo que já está armazenando.
- A quantidade informada não pode exceder a capacidade máxima do tipo.
- A soma entre o estoque atual do compartimento e a nova entrada não pode ultrapassar a capacidade máxima do tipo.
- Não é permitido registrar saída maior que a quantidade disponível do ingrediente.
- Ao esvaziar um compartimento, ele mantém o último tipo armazenado e a última data de armazenamento. Se a troca de tipo ocorrer no mesmo dia, a operação é bloqueada.
- Campos obrigatórios são validados na camada de entrada da API.

## Como executar

### Pré-requisitos

- Java 17 ou superior
- Docker e Docker Compose, apenas se quiser rodar com PostgreSQL
- Nenhuma instalação local de Maven é necessária, pois o projeto usa Maven Wrapper

### Executar com H2 em memória

Este é o caminho mais rápido para avaliação.

No Windows:

```bash
.\mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

A aplicação sobe em:

```text
http://localhost:8080
```

O profile padrão é `local-h2`, definido em `src/main/resources/application.yml`.

### H2 Console

Com o profile local, o console do H2 fica disponível em:

```text
http://localhost:8080/h2-console
```

Use os dados abaixo:

```text
JDBC URL: jdbc:h2:mem:armazemdb
User: sa
Password: deixe em branco
```

## Executando com PostgreSQL

Suba o banco:

```bash
docker compose up -d
```

O `docker-compose.yml` cria um PostgreSQL com:

```text
Host: localhost
Porta: 5434
Database: armazem_ingredientes
User: postgres
Password: postgres
```

Execute a aplicação com o profile `local-postgres`.

No Windows PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="local-postgres"
.\mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
SPRING_PROFILES_ACTIVE=local-postgres ./mvnw spring-boot:run
```

As migrations serão aplicadas automaticamente pelo Flyway durante a inicialização.

## Testes

Para executar a suíte completa:

No Windows:

```bash
.\mvnw.cmd test
```

No Linux/macOS:

```bash
./mvnw test
```

Os testes usam o profile `test`, com H2 em memória e banco isolado por contexto. A suíte cobre:

- carga do contexto Spring;
- regras de serviço para ingredientes;
- cadastro e consulta de ingredientes via API;
- movimentações de entrada e saída;
- histórico de movimentações;
- consultas de estoque e compartimentos disponíveis.

## Documentação da API

Com a aplicação em execução, a documentação OpenAPI pode ser acessada em:

```text
http://localhost:8080/swagger-ui.html
```

O JSON OpenAPI fica em:

```text
http://localhost:8080/v3/api-docs
```

## Endpoints

### Saúde da aplicação

| Método | Rota | Descrição |
| --- | --- | --- |
| `GET` | `/health` | Verifica se a aplicação está ativa |

### Ingredientes

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/ingredientes` | Cadastra um ingrediente e registra a primeira entrada |
| `GET` | `/ingredientes` | Lista todos os ingredientes cadastrados |
| `GET` | `/ingredientes/{id}` | Busca um ingrediente por ID |
| `POST` | `/ingredientes/{id}/entrada` | Registra entrada adicional de estoque |
| `POST` | `/ingredientes/{id}/saida` | Registra saída de estoque |
| `GET` | `/ingredientes/volume` | Consulta o volume total agrupado por tipo |

### Compartimentos

| Método | Rota | Descrição |
| --- | --- | --- |
| `GET` | `/compartimentos/disponiveis` | Lista compartimentos aptos para armazenar determinada quantidade e tipo |
| `GET` | `/compartimentos/disponiveis-para-venda` | Lista compartimentos com estoque disponível para venda por tipo |

### Histórico

| Método | Rota | Descrição |
| --- | --- | --- |
| `GET` | `/historico` | Lista o histórico de movimentações |

Parâmetros aceitos em `/historico`:

| Parâmetro | Valores | Padrão |
| --- | --- | --- |
| `sortBy` | `date`, `compartimento` | `date` |
| `order` | `asc`, `desc` | `desc` |

## Exemplos de uso

Os exemplos abaixo usam `curl`. Caso esteja no PowerShell e tenha conflito com alias, use `curl.exe`.

### Health check

```bash
curl http://localhost:8080/health
```

Resposta esperada:

```json
{
  "status": "UP",
  "application": "armazem-backend",
  "timestamp": "2026-05-22T17:00:00-03:00"
}
```

### Cadastrar ingrediente

```bash
curl -X POST http://localhost:8080/ingredientes \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Farinha de Trigo",
    "tipo": "SECO",
    "quantidade": 400,
    "compartimentoId": 1,
    "responsavel": "pedro"
  }'
```

Resposta:

```json
{
  "id": 1,
  "nome": "Farinha de Trigo",
  "tipo": "SECO",
  "quantidade": 400,
  "unidadeMedida": "kg",
  "compartimentoId": 1,
  "compartimentoCodigo": "C1",
  "responsavelUltimaMovimentacao": "pedro",
  "criadoEm": "2026-05-22T17:00:00-03:00",
  "atualizadoEm": null
}
```

### Registrar entrada

```bash
curl -X POST http://localhost:8080/ingredientes/1/entrada \
  -H "Content-Type: application/json" \
  -d '{
    "quantidade": 100,
    "responsavel": "maria"
  }'
```

### Registrar saída

```bash
curl -X POST http://localhost:8080/ingredientes/1/saida \
  -H "Content-Type: application/json" \
  -d '{
    "quantidade": 50,
    "responsavel": "joao"
  }'
```

### Consultar volume total por tipo

```bash
curl http://localhost:8080/ingredientes/volume
```

Resposta:

```json
[
  {
    "type": "SECO",
    "totalQuantity": 450
  }
]
```

### Consultar compartimentos disponíveis para armazenamento

```bash
curl "http://localhost:8080/compartimentos/disponiveis?tipo=SECO&quantidade=100"
```

Resposta:

```json
[
  {
    "id": 1,
    "codigo": "C1",
    "tipoAtual": "SECO",
    "ultimoTipoArmazenado": "SECO",
    "quantidadeAtual": 450,
    "capacidadeMaxima": 600,
    "espacoDisponivel": 150,
    "ultimaDataArmazenamento": "2026-05-22",
    "disponivel": true,
    "motivo": "Compartimento disponível para armazenamento."
  }
]
```

### Consultar histórico

```bash
curl "http://localhost:8080/historico?sortBy=date&order=asc"
```

Resposta:

```json
[
  {
    "id": 1,
    "dataHora": "2026-05-22T17:00:00-03:00",
    "tipoOperacao": "ENTRADA",
    "quantidade": 400,
    "tipoIngrediente": "SECO",
    "nomeIngrediente": "Farinha de Trigo",
    "responsavel": "pedro",
    "compartimentoId": 1,
    "compartimentoCodigo": "C1",
    "ingredienteId": 1
  }
]
```

## Estrutura do projeto

```text
src
├── main
│   ├── java/com/vega/armazem
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   │   ├── request
│   │   │   └── response
│   │   ├── entity
│   │   ├── enums
│   │   ├── exception
│   │   ├── repository
│   │   └── service
│   └── resources
│       ├── db/migration
│       ├── application.yml
│       ├── application-local-h2.yml
│       ├── application-local-postgres.yml
│       └── application-prod.yml
└── test
    ├── java/com/vega/armazem
    │   ├── integration
    │   └── service
    └── resources
        └── application-test.yml
```

### Camadas

- `controller`: expõe a API HTTP e delega a regra de negócio.
- `service`: concentra regras de domínio e transações.
- `repository`: acesso a dados via Spring Data JPA.
- `entity`: modelo persistido.
- `dto`: contratos de entrada e saída da API.
- `exception`: padronização das respostas de erro.
- `config`: CORS, OpenAPI e inicialização defensiva dos compartimentos.

## Banco de dados e migrations

O banco é versionado por Flyway em `src/main/resources/db/migration`.

| Migration | Responsabilidade |
| --- | --- |
| `V1__criar_tabelas_iniciais.sql` | Cria tabelas, chaves estrangeiras e índices |
| `V2__inserir_compartimentos_iniciais.sql` | Insere os compartimentos `C1` a `C5` |

O Hibernate está configurado com:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Ou seja: a aplicação valida o modelo contra o schema existente, mas quem cria e evolui o banco é o Flyway. Essa separação evita que a aplicação altere estrutura de banco implicitamente.

## Tratamento de erros

As exceções são tratadas por um `@RestControllerAdvice`, com resposta padronizada.

Exemplo de erro de validação:

```json
{
  "status": 400,
  "error": "Erro de validação",
  "message": "Um ou mais campos estão inválidos.",
  "path": "/ingredientes",
  "fieldErrors": [
    {
      "field": "nome",
      "message": "O nome do ingrediente é obrigatório."
    }
  ]
}
```

Exemplo de regra de negócio:

```json
{
  "status": 400,
  "error": "Regra de negócio violada",
  "message": "O compartimento C1 não possui espaço suficiente. Espaço disponível: 200.000 kg.",
  "path": "/ingredientes",
  "fieldErrors": null
}
```

## Profiles

| Profile | Uso | Banco |
| --- | --- | --- |
| `local-h2` | Execução local rápida | H2 em memória |
| `local-postgres` | Execução local próxima de produção | PostgreSQL via Docker |
| `test` | Testes automatizados | H2 em memória isolado |
| `prod` | Ambiente externo | PostgreSQL via variáveis de ambiente |

Variáveis esperadas no profile `prod`:

```text
DATABASE_URL
DATABASE_USERNAME
DATABASE_PASSWORD
CORS_ALLOWED_ORIGINS
PORT
```

## Decisões técnicas

- **Flyway como fonte da verdade do schema**: o Hibernate apenas valida. Isso reduz surpresa entre ambiente local, testes e produção.
- **Regras no service, não no controller**: controllers ficam pequenos e a regra de negócio permanece testável sem depender de HTTP.
- **Histórico como efeito transacional da movimentação**: entrada e saída de estoque são persistidas junto com o registro histórico, mantendo consistência.
- **DTOs explícitos**: a API não expõe entidades JPA diretamente.
- **H2 em modo PostgreSQL nos testes e no profile local**: facilita desenvolvimento rápido sem perder compatibilidade básica com o banco alvo.
- **OpenAPI habilitado em ambiente local**: simplifica avaliação manual da API.
- **CORS configurável**: origens locais por padrão e variável de ambiente para produção.

## Observações para avaliação

O projeto está pronto para ser avaliado pelo fluxo abaixo:

```bash
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

Depois acesse:

```text
http://localhost:8080/swagger-ui.html
```

Se preferir validar com PostgreSQL, execute primeiro:

```bash
docker compose up -d
```

e rode a aplicação com `SPRING_PROFILES_ACTIVE=local-postgres`.
