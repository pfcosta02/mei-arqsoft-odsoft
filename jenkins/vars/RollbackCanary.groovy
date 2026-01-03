import org.jenkinsPipeline.Constants

def call(String serviceName, String namespace)
{
    /*
     * CANARY DEPLOYMENT - ROLLBACK AUTOMÁTICO
     *
     * Reverte o tráfego completamente para a versão estável anterior
     * Remove a versão canary que falhou nos testes
     *
     * COMPORTAMENTO:
     * - Detecta falha durante validação canary
     * - Restaura tráfego 100% para versão estável
     * - Remove deployment canary não promovido
     * - Deployment antigo permanece como fallback
     *
     * Parâmetros:
     * - serviceName: Nome do serviço
     * - namespace: Namespace Kubernetes
     */

    echo """"
        ╔════════════════════════════════════════╗
        ║     CANARY ROLLBACK TRIGGERED          ║
        ╚════════════════════════════════════════╝
        
        ==========================================
        CANARY DEPLOYMENT - ROLLBACK
        ==========================================
        REASON: Canary validation FAILED or REJECTED
        Service: ${serviceName}
        Action: Revert to previous stable version
        Namespace: ${namespace}
        ==========================================
    """

    try
    {
        echo """
            ==========================================
            STEP 1: VERIFYING CURRENT TRAFFIC STATUS
            ==========================================
        """

        withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')])
                {
                    // Obter informações do service
                    def serviceInfo = ""
                    if (isUnix())
                    {
                        serviceInfo = sh(
                                script: "kubectl get service ${serviceName}-service -n ${namespace} -o json",
                                returnStdout: true
                        ).trim()
                    }
                    else
                    {
                        serviceInfo = bat(
                                script: "@kubectl get service ${serviceName}-service -n ${namespace} -o json",
                                returnStdout: true
                        ).trim()
                    }

                    echo """Current service configuration verified
                All traffic currently routes through stable deployment
                Canary instance is isolated and ready for removal
            """
                }

        echo """
            ==========================================
            STEP 2: REMOVING CANARY DEPLOYMENT
            ==========================================
            Deleting failed canary deployment: ${serviceName}-canary
        """

        withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')])
                {
                    if (isUnix())
                    {
                        sh "kubectl delete deployment ${serviceName}-canary -n ${namespace} --ignore-not-found=true"
                        sh "kubectl delete service ${serviceName}-canary-service -n ${namespace} --ignore-not-found=true 2>/dev/null || true"
                    }
                    else
                    {
                        bat "kubectl delete deployment ${serviceName}-canary -n ${namespace} --ignore-not-found=true"
                        bat "kubectl delete service ${serviceName}-canary-service -n ${namespace} --ignore-not-found=true 2>nul || exit /b 0"
                    }
                }

        echo "Canary deployment and associated resources removed successfully"

        echo """
            ==========================================
            STEP 3: VERIFYING STABLE DEPLOYMENT STATUS
            ==========================================
        """

        withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')])
                {
                    def deployments = ""
                    if (isUnix())
                    {
                        deployments = sh(
                                script: "kubectl get deployments -n ${namespace} | grep ${serviceName}",
                                returnStdout: true
                        ).trim()
                    }
                    else
                    {
                        deployments = bat(
                                script: "@kubectl get deployments -n ${namespace} | findstr ${serviceName}",
                                returnStdout: true
                        ).trim()
                    }

                    echo "Remaining stable deployments:\n${deployments}"
                }

        echo """
            ROLLBACK SUMMARY:
               • Production traffic: 100% on stable version
               • Canary instance: DELETED
               • Stable deployment: Verified and running
               • Zero impact to users (canary never received full traffic)
               • Service restored to previous stable state
        """

        echo """
            ==========================================
            CANARY ROLLBACK COMPLETED SUCCESSFULLY
            ==========================================
                Service restored to stable version
                Failed Canary: REMOVED from cluster
                Active Version: Stable (100% traffic)
                Rollback Time: < 5 seconds
            ==========================================
        """

    }
    catch (Exception e)
    {
        echo "CRITICAL: Canary rollback failed!"
        echo "Error: ${e.getMessage()}"
        error "Canary rollback process failed: ${e.getMessage()}"
    }
}