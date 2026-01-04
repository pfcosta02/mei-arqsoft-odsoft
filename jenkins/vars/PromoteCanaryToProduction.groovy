/*
 * Promote Canary to Production - VERSÃO FINAL
 *
 * Aumenta o tráfego da instância canary para 100%
 * Promove a nova versão como estável
 * Remove o deployment anterior
 *
 * CORRIGIDO: Suporta Windows E Unix (SEM FOR LOOPS)
 *
 * Uso:
 *   PromoteCanaryToProduction('lendings-q', 'dev')
 */

import org.jenkinsPipeline.Constants

def call(String serviceName, String namespace)
{
    echo """
        ==========================================
        PROMOTING CANARY TO PRODUCTION
        ==========================================
        Service: ${serviceName}
        Target: 100% traffic
        Namespace: ${namespace}
        OS: ${isUnix() ? 'Unix/Linux' : 'Windows'}
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
                    // STEP 1: Obter imagem canary (SEM FOR LOOP)
                    echo "STEP 1: Getting canary image..."

                    def canaryImage
                    if (isUnix())
                    {
                        canaryImage = sh(
                                script: """
                        kubectl get deployment ${serviceName}-canary -n ${namespace} \
                            -o jsonpath='{.spec.template.spec.containers[0].image}'
                    """,
                                returnStdout: true
                        ).trim()
                    }
                    else
                    {
                        // WINDOWS: Sem for loop, usar jsonpath direto
                        canaryImage = bat(
                                script: """
                        @kubectl get deployment ${serviceName}-canary -n ${namespace} ^
                            -o jsonpath="{.spec.template.spec.containers[0].image}"
                    """,
                                returnStdout: true
                        ).trim()
                    }

                    if (!canaryImage || canaryImage.isEmpty())
                    {
                        error "❌ Could not get canary image!"
                    }

                    echo "Canary image: ${canaryImage}"

                    // STEP 2: Atualizar deployment estável com imagem canary
                    echo "STEP 2: Updating stable deployment with canary image..."

                    if (isUnix())
                    {
                        sh """
                    kubectl set image deployment/${serviceName} \
                        ${serviceName}=${canaryImage} \
                        -n ${namespace} \
                        --record
                """
                    }
                    else
                    {
                        bat """
                    kubectl set image deployment/${serviceName} ^
                        ${serviceName}=${canaryImage} ^
                        -n ${namespace}
                """
                    }

                    echo "✅ Stable deployment updated"

                    // STEP 3: Aguardar rollout
                    echo "STEP 3: Waiting for stable deployment rollout (max 5 minutes)..."

                    if (isUnix())
                    {
                        sh """
                    kubectl rollout status deployment/${serviceName} \
                        -n ${namespace} \
                        --timeout=5m
                """
                    }
                    else
                    {
                        bat """
                    kubectl rollout status deployment/${serviceName} ^
                        -n ${namespace} ^
                        --timeout=5m
                """
                    }

                    echo "✅ Stable deployment rolled out successfully"

                    // STEP 4: Verificar replicas estáveis (SEM FOR LOOP)
                    echo "STEP 4: Verifying stable deployment..."

                    def stableReplicas
                    if (isUnix())
                    {
                        stableReplicas = sh(
                                script: """
                        kubectl get deployment ${serviceName} -n ${namespace} \
                            -o jsonpath='{.status.readyReplicas}'
                    """,
                                returnStdout: true
                        ).trim()
                    }
                    else
                    {
                        // WINDOWS: Sem for loop, usar jsonpath direto
                        stableReplicas = bat(
                                script: """
                        @kubectl get deployment ${serviceName} -n ${namespace} ^
                            -o jsonpath="{.status.readyReplicas}"
                    """,
                                returnStdout: true
                        ).trim()
                    }

                    echo "Stable deployment ready replicas: ${stableReplicas}"

                    // STEP 5: Remover deployment canary
                    echo "STEP 5: Removing canary deployment..."

                    if (isUnix())
                    {
                        sh """
                    kubectl delete deployment ${serviceName}-canary -n ${namespace} --ignore-not-found=true
                    kubectl delete service ${serviceName}-canary-service -n ${namespace} --ignore-not-found=true 2>/dev/null || true
                """
                    }
                    else
                    {
                        bat """
                    kubectl delete deployment ${serviceName}-canary -n ${namespace} --ignore-not-found=true
                    kubectl delete service ${serviceName}-canary-service -n ${namespace} --ignore-not-found=true 2>nul || exit /b 0
                """
                    }

                    echo "✅ Canary deployment removed"

                    // STEP 6: Verificar histórico
                    echo "STEP 6: Deployment history..."

                    if (isUnix())
                    {
                        sh """
                    echo "Recent revisions:"
                    kubectl rollout history deployment/${serviceName} -n ${namespace} | head -5
                """
                    }
                    else
                    {
                        bat """
                    echo Recent revisions:
                    kubectl rollout history deployment/${serviceName} -n ${namespace}
                """
                    }

                    // STEP 7: Verificar pods ativos
                    echo "STEP 7: Verifying active pods..."

                    if (isUnix())
                    {
                        sh """
                    kubectl get pods -n ${namespace} -l app=${serviceName} -o wide
                """
                    }
                    else
                    {
                        bat """
                    kubectl get pods -n ${namespace} -l app=${serviceName} -o wide
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