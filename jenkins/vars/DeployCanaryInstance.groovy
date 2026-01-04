import org.jenkinsPipeline.Constants

def call(String serviceName, String dockerImage, String namespace, Integer canaryPercentage) {

    echo """
        ==========================================
        CANARY DEPLOYMENT - CREATE / UPDATE
        ==========================================
        Service: ${serviceName}
        Image:   ${dockerImage}
        Traffic: ${canaryPercentage}% (teórico)
        Ns:      ${namespace}
        ==========================================
    """

    withCredentials([file(
            credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace],
            variable: 'KUBECONFIG'
    )]) {

        // 1) Criar ou atualizar deployment canary
        def canaryExists = sh(
                script: "kubectl get deployment ${serviceName}-canary -n ${namespace} 2>/dev/null",
                returnStatus: true
        ) == 0

        if (canaryExists) {
            echo "Canary deployment already exists. Updating image..."
            sh """
                kubectl set image deployment/${serviceName}-canary \
                    ${serviceName}=${dockerImage} \
                    -n ${namespace} \
                    --record
            """
        } else {
            echo "Canary deployment does not exist. Creating..."
            sh """
                kubectl create deployment ${serviceName}-canary \
                    --image=${dockerImage} \
                    --replicas=1 \
                    -n ${namespace}

                kubectl label deployment ${serviceName}-canary \
                    app=${serviceName}-canary version=canary \
                    -n ${namespace} --overwrite
            """
        }

        // 2) Service canary para testes
        def svcStatus = sh(
                script: "kubectl get service ${serviceName}-canary-service -n ${namespace} 2>/dev/null",
                returnStatus: true
        )
        if (svcStatus != 0) {
            echo "Creating canary service ${serviceName}-canary-service..."
            sh """
                kubectl expose deployment ${serviceName}-canary \
                    --name=${serviceName}-canary-service \
                    --port=8882 --target-port=8882 \
                    -n ${namespace} --type=ClusterIP
            """
        }

        // 3) Esperar rollout
        sh """
            kubectl rollout status deployment/${serviceName}-canary \
                -n ${namespace} --timeout=5m
        """

        echo "Canary deployment is ready."
        sh """
            echo "Canary pods:"
            kubectl get pods -n ${namespace} -l app=${serviceName}-canary -o wide
        """
    }
}
