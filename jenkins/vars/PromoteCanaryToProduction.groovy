import org.jenkinsPipeline.Constants

def call(String serviceName, String namespace)
{
    /*
     * Promote Canary to Production
     *
     * Aumenta o tráfego da instância canary para 100%
     * Promove a nova versão como estável
     * Remove o deployment anterior
     *
     * Parâmetros:
     * - serviceName: Nome do serviço (ex: 'lendings-c')
     * - namespace: Namespace K8s (ex: 'dev')
     */

    echo """
        ==========================================
        PROMOTING CANARY TO PRODUCTION
        ==========================================
        Service: ${serviceName}
        Target: 100% traffic
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
                    // STEP 1: Obter imagem canary
                    echo "STEP 1: Getting canary image..."

                    def canaryImage = sh(
                            script: """
                    kubectl get deployment ${serviceName}-canary -n ${namespace} \
                        -o jsonpath='{.spec.template.spec.containers[0].image}'
                """,
                            returnStdout: true
                    ).trim()

                    if (!canaryImage)
                    {
                        error "❌ Could not get canary image!"
                    }

                    echo "Canary image: ${canaryImage}"

                    // STEP 2: Atualizar deployment estável com imagem canary
                    echo "STEP 2: Updating stable deployment with canary image..."

                    sh """
                kubectl set image deployment/${serviceName} \
                    ${serviceName}=${canaryImage} \
                    -n ${namespace} \
                    --record
            """

                    echo "✅ Stable deployment updated"

                    // STEP 3: Aguardar rollout
                    echo "STEP 3: Waiting for stable deployment rollout (max 5 minutes)..."

                    sh """
                kubectl rollout status deployment/${serviceName} \
                    -n ${namespace} \
                    --timeout=5m
            """

                    echo "✅ Stable deployment rolled out successfully"

                    // STEP 4: Verificar replicas estáveis
                    echo "STEP 4: Verifying stable deployment..."

                    def stableReplicas = sh(
                            script: """
                    kubectl get deployment ${serviceName} -n ${namespace} \
                        -o jsonpath='{.status.readyReplicas}'
                """,
                            returnStdout: true
                    ).trim()

                    echo "Stable deployment ready replicas: ${stableReplicas}"

                    // STEP 5: Remover deployment canary
                    echo "STEP 5: Removing canary deployment..."

                    sh """
                kubectl delete deployment ${serviceName}-canary -n ${namespace} --ignore-not-found=true
                kubectl delete service ${serviceName}-canary-service -n ${namespace} --ignore-not-found=true 2>/dev/null || true
            """

                    echo "✅ Canary deployment removed"

                    // STEP 6: Verificar histórico
                    echo "STEP 6: Deployment history..."

                    sh """
                echo "Recent revisions:"
                kubectl rollout history deployment/${serviceName} -n ${namespace} | head -5
            """

                    // STEP 7: Health check final
                    echo "STEP 7: Final health check..."

                    def stablePod = sh(
                            script: """
                    kubectl get pods -n ${namespace} \
                        -l app=${serviceName} \
                        -o jsonpath='{.items[0].metadata.name}'
                """,
                            returnStdout: true
                    ).trim()

                    if (stablePod)
                    {
                        sh """
                    kubectl port-forward pods/${stablePod} 8080:8080 -n ${namespace} &
                    PF_PID=\$!
                    sleep 2
                    curl -f http://localhost:8080/actuator/health || exit 1
                    kill \$PF_PID 2>/dev/null || true
                    wait \$PF_PID 2>/dev/null || true
                """
                    }
                }

        echo """
            ==========================================
            ✅ CANARY PROMOTION COMPLETED SUCCESSFULLY
            ==========================================
            Service: ${serviceName}
            New Version: Promoted to 100% traffic
            Previous Version: Removed from cluster
            Namespace: ${namespace}
            ==========================================
        """
    }
    catch (Exception e)
    {
        echo "❌ Error promoting canary: ${e.getMessage()}"
        throw e
    }
}