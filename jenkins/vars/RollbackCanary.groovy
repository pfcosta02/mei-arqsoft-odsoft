import org.jenkinsPipeline.Constants

def call(String serviceName, String namespace) {

    echo """
        ==========================================
        CANARY ROLLBACK
        ==========================================
        Service: ${serviceName}
        Namespace: ${namespace}
        ==========================================
    """

    withCredentials([file(
            credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace],
            variable: 'KUBECONFIG'
    )]) {

        // Não mexe no deployment estável, só limpa o canary
        sh """
            kubectl delete deployment ${serviceName}-canary -n ${namespace} --ignore-not-found=true
            kubectl delete service ${serviceName}-canary-service -n ${namespace} --ignore-not-found=true
        """

        echo "Canary resources deleted, traffic stays 100% on stable."
    }
}
