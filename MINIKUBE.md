
# Iniciar Minikube

    minikube start
    
    eval $(minikube docker-env)

    minikube dashboard &

    kubectl apply -f minikube/config-reader.yaml

# Deploy do MQ

    kubectl apply -f minikube/mq-config.yaml
    kubectl apply -f minikube/mq-deployment.yaml

# Criação das imagens

    mvn clean install -P docker
    
# Deploy

    kubectl apply -f minikube/responder-deploy.yaml
    
    kubectl apply -f minikube/atm-server-deploy.yaml

# Execução dos clientes

Usar a opção `-k` para os clientes se conectarem aos pods em execução no Kubernetes

    ./client.sh -k
    
    ./upload.sh -k
