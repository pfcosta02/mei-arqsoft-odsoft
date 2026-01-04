import org.jenkinsPipeline.Constants

def call(String serviceName, String namespace) {

    echo """
        ==========================================
        PROMOTING CANARY TO 100% TRAFFIC
        ==========================================
        Service: ${serviceName}
        Namespace: ${namespace}
        ==========================================
    """

    withCredentials([file(
            credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace],
            variable: 'KUBECONFIG'
    )]) {

        // 1) Obter imagem da canary
        def canaryImage = sh(
                script: """
                kubectl get deployment ${serviceName}-canary -n ${namespace} \\
                    -o jsonpath='{.spec.template.spec.containers[0].image}'
            """,
                returnStdout: true
        ).trim()

        if (!canaryImage) {
            error "Could not read canary image!"
        }

        echo "Using canary image: ${canaryImage}"

        // 2) Atualizar deployment estável
        sh """
            kubectl set image deployment/${serviceName} \\
                ${serviceName}=${canaryImage} \\
                -n ${namespace} --record
        """

        // 3) Esperar rollout do estável
        sh """
            kubectl rollout status deployment/${serviceName} \\
                -n ${namespace} --timeout=5m
        """

        // 4) Remover canary
        sh """
            kubectl delete deployment ${serviceName}-canary -n ${namespace} --ignore-not-found=true
            kubectl delete service ${serviceName}-canary-service -n ${namespace} --ignore-not-found=true
        """

        echo "Canary promoted and old canary resources removed."
    }
}
