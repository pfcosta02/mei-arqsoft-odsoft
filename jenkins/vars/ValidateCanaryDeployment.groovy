import org.jenkinsPipeline.Constants
import org.jenkinsPipeline.DeploymentValidator

def call(String serviceName, String namespace)
{
    /*
     * Validate Canary Deployment
     *
     * Executa smoke tests na instância canary
     * Valida:
     * - Health checks (liveness)
     * - Readiness checks
     * - Conectividade básica
     * - Endpoints críticos
     *
     * Parâmetros:
     * - serviceName: Nome do serviço (ex: 'lendings-c')
     * - namespace: Namespace K8s (ex: 'dev')
     */

    echo """
        ==========================================
        VALIDATING CANARY INSTANCE
        ==========================================
        Service: ${serviceName}
        Namespace: ${namespace}
    """

    try
    {
        withCredentials([
            file(
                credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace],
                variable: 'KUBECONFIG'
            )
        ])
        {
            // STEP 1: Obter nome do pod canary
            echo "STEP 1: Getting canary pod..."

            def canaryPod = sh(
                script: """
                    kubectl get pods -n ${namespace} \
                        -l app=${serviceName}-canary \
                        -o jsonpath='{.items[0].metadata.name}' \
                        --sort-by=.metadata.creationTimestamp | tail -1
                """,
                returnStdout: true
            ).trim()

            if (!canaryPod)
            {
                error "❌ No canary pod found!"
            }

            echo "Found canary pod: ${canaryPod}"

            // STEP 2: Health check via port-forward
            echo "STEP 2: Testing health checks via port-forward..."

            sh """
                # Iniciar port-forward em background
                kubectl port-forward pods/${canaryPod} 8080:8080 -n ${namespace} &
                PF_PID=\$!

                # Aguardar port-forward estabelecer
                sleep 3

                # Testar health endpoints
                echo "Testing /actuator/health/liveness..."
                curl -f http://localhost:8080/actuator/health/liveness || exit 1

                echo "Testing /actuator/health/readiness..."
                curl -f http://localhost:8080/actuator/health/readiness || exit 1

                echo "Testing /actuator/health..."
                curl -f http://localhost:8080/actuator/health || exit 1

                # Terminar port-forward
                kill \$PF_PID 2>/dev/null || true
                wait \$PF_PID 2>/dev/null || true
            """

            // STEP 3: Verificar logs
            echo "STEP 3: Checking pod logs for errors..."

            def hasErrors = sh(
                script: """
                    kubectl logs pods/${canaryPod} -n ${namespace} \
                        | grep -i "error\\|exception\\|fatal" | wc -l
                """,
                returnStdout: true
            ).trim().toInteger()

            if (hasErrors > 0)
            {
                echo "⚠️  Warning: Found ${hasErrors} error lines in logs"
                sh "kubectl logs pods/${canaryPod} -n ${namespace} | tail -20"
            }
            else
            {
                echo "✅ No errors found in logs"
            }

            // STEP 4: Verificar resource usage
            echo "STEP 4: Checking resource usage..."

            sh """
                echo "CPU and Memory usage:"
                kubectl top pod ${canaryPod} -n ${namespace} || echo "Metrics not available"
            """

            // STEP 5: Comparar com versão estável
            echo "STEP 5: Comparing with stable deployment..."

            def stablePod = sh(
                script: """
                    kubectl get pods -n ${namespace} \
                        -l app=${serviceName} \
                        -o jsonpath='{.items[0].metadata.name}' \
                        --sort-by=.metadata.creationTimestamp | tail -1
                """,
                returnStdout: true
            ).trim()

            if (stablePod && stablePod != canaryPod)
            {
                sh """
                    echo "Canary vs Stable comparison:"
                    echo "Canary pod: ${canaryPod}"
                    echo "Stable pod: ${stablePod}"
                    echo ""
                    echo "Canary metrics:"
                    kubectl top pod ${canaryPod} -n ${namespace} || true
                    echo "Stable metrics:"
                    kubectl top pod ${stablePod} -n ${namespace} || true
                """
            }
        }

        echo "✅ Canary validation completed successfully"
    }
    catch (Exception e)
    {
        echo "❌ Canary validation FAILED: ${e.getMessage()}"
        throw e
    }
}