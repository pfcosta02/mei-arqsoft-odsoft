import org.jenkinsPipeline.Constants

def call(String serviceName, String namespace, int port) {

    def url = "http://${serviceName}.${namespace}.svc.cluster.local:${port}/actuator/health"

    echo """
        ==========================================
        IN-CLUSTER HEALTH CHECK (Swagger UI)
        ==========================================
        Service:   ${serviceName}
        Namespace: ${namespace}
        URL:       ${url}
        ==========================================
    """

    withCredentials([
            file(
                    credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace],
                    variable: 'KUBECONFIG'
            )
    ]) {
        try {
            if (isUnix()) {
                sh """
                    set -e
                    # criar pod temporário com curl dentro do cluster
                    kubectl run curl-smoke --rm -i --restart=Never --image=curlimages/curl -n ${namespace} --request-timeout=150s -- \
                      sh -c 'echo "Calling ${url} ..." && curl -f -s -o /dev/null "${url}"'
                """
            } else {
                bat """
                    echo Creating in-cluster curl pod for health test...
                    kubectl run curl-smoke --rm -i --restart=Never --image=curlimages/curl -n ${namespace} --request-timeout=150s -- ^
                      sh -c "echo Calling ${url} ... && curl -f -s -o /dev/null \\"${url}\\""
                    if %ERRORLEVEL% NEQ 0 (
                        echo Health test FAILED for ${url}
                        exit /b 1
                    )
                """
            }

            echo "✅ Health test PASSED for ${url}"
        } catch (Exception e) {
            echo "❌ Health test FAILED for ${url}: ${e.getMessage()}"
            throw e
        }
    }
}
