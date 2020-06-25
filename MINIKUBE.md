
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

    ./client.sh --server.host=172.17.0.3 --server.port=30992
    
    ./upload.sh --ibm.mq.connName="172.17.0.3(32014)"