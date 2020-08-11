
# Iniciar Minikube

    minikube start
    
    eval $(minikube docker-env)

    minikube dashboard &

Configuração Para Spring Boot

    kubectl apply -f minikube/config-reader.yaml

Configuração para o Spring Boot Admin Server

    kubectl create clusterrolebinding admin-default --clusterrole=cluster-admin --serviceaccount=default:default

# Deploy do MQ

    kubectl apply -f minikube/mq-config.yaml
    kubectl apply -f minikube/mq-deployment.yaml

# Criação das imagens

    mvn clean install -P build-image,kubernetes
    
# Deploy

    kubectl apply -f minikube/dummy-responder-deploy.yaml
    
    kubectl apply -f minikube/atm-server-deploy.yaml

# Execução dos clientes

Usar a opção `-k` para os clientes se conectarem aos pods em execução no Kubernetes

    ./client.sh -k [ATM_ID]
    
    ./upload.sh -k [ATM_ID]
