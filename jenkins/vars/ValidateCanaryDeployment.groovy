import org.jenkinsPipeline.Constants

def call(String serviceName, String namespace) {

    echo """
        ==========================================
        CANARY VALIDATION (SMOKE TESTS)
        ==========================================
        Service: ${serviceName}-canary-service
        Namespace: ${namespace}
        ==========================================
    """

    withCredentials([file(
            credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace],
            variable: 'KUBECONFIG'
    )]) {

        // Simples curl interno ao cluster via port-forward
        def canaryPod = sh(
                script: """
                kubectl get pods -n ${namespace} \\
                    -l app=${serviceName}-canary \\
                    -o jsonpath='{.items[0].metadata.name}'
            """,
                returnStdout: true
        ).trim()

        if (!canaryPod) {
            error "No canary pod found!"
        }

        sh """
            kubectl port-forward -n ${namespace} pod/${canaryPod} 18082:8882 &
            PF_PID=\$!
            sleep 5
            curl -f http://localhost:18082/actuator/health || exit 1
            kill \$PF_PID 2>/dev/null || true
            wait \$PF_PID 2>/dev/null || true
        """
    }

    echo "Canary basic health check PASSED."
}
