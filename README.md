
# Requisitos

Requisitos para fazer o build:

- Java 11.0.7
- Maven 3.6.3

Nos ambiente de desenvolvimento Linux, macOS e Cygwin, use o [sdkman](https://sdkman.io/) para instalar esses produtos.

Procedimento de instalação:

1. Instalar o sdkman

    `curl -s "https://get.sdkman.io" | bash`

2. Abrir outro terminal e instalar Java e Maven:
    
    `sdk install java 11.0.7.hs-adpt`
    
    `sdk install maven 3.6.3`


# Build

Para executar o build

    mvn clean install


# Executar

### Iniciar IBM MQ

    docker run \
      --env LICENSE=accept \
      --env MQ_QMGR_NAME=QM1 \
      --publish 1414:1414 \
      --publish 9443:9443 \
      --detach \
      ibmcom/mq

    https://localhost:9443/ibmmq/console

    User: admin
    Password: passw0rd

### Iniciar aplicação

Executar o servidor

    ./server.sh

Abrir um cliente

    ./client.sh
