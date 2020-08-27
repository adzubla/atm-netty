
# Requisitos

Requisitos para fazer o build:

- Java 11.0.8
- Maven 3.6.3

Nos ambiente de desenvolvimento Linux, macOS e Cygwin, use o [sdkman](https://sdkman.io/) para instalar esses produtos.

###### Procedimento de instalação

1. Instalar o sdkman:

    `curl -s "https://get.sdkman.io" | bash`

2. Abrir outro terminal e instalar Java e Maven:
    
    `sdk install java 11.0.8.hs-adpt`
    
    `sdk install maven 3.6.3`


# Build

Para executar o build

    mvn clean install


## Profiles Maven

### Profile "default"

O profile "default" (quando nenhum profile é definido ao executar o Maven) não cria nenhuma imagem e é adequado
para ser usado durante o desenvolvimento para execução local.

A execução de cada serviço deve ser feita através do comando

    mvn spring-boot:run


### Profile build-image

Cria imagem OCI através de um plugin nativo do Spring Boot baseado em Cloud Native Buildpacks (usando o Paketo Java buildpack)

Para criar a imagem:

    mvn clean install -P build-image

Essa imagem pode ser usada no docker e docker-compose.


### Profile kubernetes

Acrescenta dependencia do *Spring Boot Cloud* para rodar em ambientes baseados em Kubernetes (como o OpenShift)

A imagem deve ser criada com o comando a seguir:

    mvn clean install -P build-image,kubernetes


# Execução local

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

Executar o dummy-responder

    ./responder.sh

Executar o servidor

    ./server.sh

Abrir um cliente

    ./client.sh

Para fazer upload de uma nova configuração (arquivo registry.txt)

    ./upload.sh

Para terminar o servidor

    ./shutdown.sh

# Debug

O script `server.sh` já habilita a porta 5005 para debug remoto a partir de um IDE. 

# Monitoração

Mostrar conexões ativas

    curl -s -X GET http://localhost:8081/connection/

Mostrar informações de uma conexão

    curl -s -X GET http://localhost:8081/connection/{ID}

Remover uma conexão

    curl -s -X DELETE http://localhost:8081/connection/{ID}

Ver os ids registrados

    curl -s -X GET http://localhost:8081/registry/

Ver as regras de roteamento

    curl -s -X GET http://localhost:8081/routingrules/


# Execução com Docker

Instalar docker e docker-compose (no Ubuntu)

    sudo apt install docker.io docker-compose
    sudo systemctl enable --now docker
    sudo usermod -aG docker $USER
    
    (Efetuar Logout e Login)

Gerar as imagens

    mvn clean install -P build-image

Iniciar os serviços no docker (mq, spring-boot-admin-server, dummy-responder e atm-server)

    docker-compose up

Executar o client local, acessando os serviços no docker

    ./client.sh

Para terminar todos os serviços

    docker-compose down
