# Order Service

## Descrição

Este é um projeto **Spring Boot** modularizado para gerenciamento de pedidos, incluindo processamento, recebimento, envio e consulta. Os módulos interagem **exclusivamente** por meio de **Apache Kafka**, garantindo escalabilidade e desacoplamento.

## Módulos

O projeto é dividido nos seguintes módulos:

- **`common`**: Contém classes compartilhadas, como DTOs, entidades, enums e configurações comuns utilizadas por todos os outros módulos.
- **`order-receiver`**: Responsável por receber novos pedidos via Kafka e publicá-los para processamento.
- **`order-processor`**: Processa os pedidos recebidos e publica eventos sobre o status do processamento.
- **`order-sender`**: Envia pedidos processados para sistemas externos via Kafka.
- **`order-query`**: Único módulo que expõe APIs REST, permitindo consultas sobre o status dos pedidos com base nos eventos consumidos do Kafka.

## Interação entre os módulos

A comunicação entre os módulos ocorre **exclusivamente** via **Apache Kafka**, utilizando os seguintes tópicos:

- **`order-receiver`**
    - **Consome:** `order-received-topic` - Para receber novos pedidos.
    - **Publica:** `order-processing-topic` - Quando um pedido foi recebido e está pronto para processamento.

- **`order-processor`**
    - **Consome:** `order-processing-topic` - Para iniciar o processamento do pedido.
    - **Publica:** `order-send-topic` - Quando um pedido foi processado com sucesso.

- **`order-sender`**
    - **Consome:** `order-send-topic` - Para enviar o pedido processado para um sistema externo.
    - **Publica:** `order-sent-topic` - Quando o pedido foi enviado com sucesso.

- **`order-query`**
    - Para consultas dos pedidos via API REST.

## Requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- **Java 17**
- **Maven 3.x**
- **Docker** (para rodar containers)
- **Kafka** (pode ser executado via Docker Compose)

## Como rodar o projeto

### Via Maven

1. Clone o repositório:
   ```sh
   git clone <url-do-repositorio>
   cd order
   ```
2. Compile e instale as dependências:
   ```sh
   mvn clean install
   ```
3. Execute todos os módulos:
   ```sh
   mvn spring-boot:run -pl order-receiver &
   mvn spring-boot:run -pl order-processor &
   mvn spring-boot:run -pl order-sender &
   mvn spring-boot:run -pl order-query
   ```

### Via Docker

1. Certifique-se de que o **Docker** está instalado e em execução.
2. Execute os containers definidos no `docker-compose.yml`:
   ```sh
   docker-compose up -d
   ```

### Testando a aplicação

Após subir todos os módulos, envie uma mensagem no tópico **`order-received-topic`** com o seguinte JSON de exemplo:

```json
{
  "externalOrderId": "ORD123401",
  "products": [
    {
      "sku": "PROD001",
      "price": 19.99,
      "quantity": 2
    },
    {
      "sku": "PROD002",
      "price": 49.90,
      "quantity": 1
    },
    {
      "sku": "PROD003",
      "price": 99.50,
      "quantity": 3
    },
    {
      "sku": "PROD004",
      "price": 5.75,
      "quantity": 5
    }
  ]
}
```

Isso pode ser feito via **kafka-console-producer**:
```sh
kafka-console-producer --broker-list localhost:9092 --topic order-received-topic
```
E cole o JSON acima.

## Configuração

As configurações do projeto estão no arquivo `application.yml` de cada módulo. Configurações sensíveis podem ser definidas via variáveis de ambiente.

## APIs

O projeto expõe APIs **apenas no módulo `order-query`** para consulta de pedidos. Consulte o `Swagger` para detalhes:

```sh
http://localhost:8084/swagger-ui.html
```

## Tecnologias utilizadas

- **Spring Boot 3**
- **Spring Cloud**
- **Spring Data JPA**
- **Kafka** (para eventos assíncronos)
- **PostgreSQL** (banco de dados)
- **Docker & Docker Compose**
- **Lombok** (para redução de boilerplate)

## Contribuição

Pull requests são bem-vindos. Para grandes mudanças, abra um issue primeiro para discutir o que deseja alterar.

## Licença

Este projeto está sob a MIT License - veja o arquivo [LICENSE](LICENSE) para detalhes.