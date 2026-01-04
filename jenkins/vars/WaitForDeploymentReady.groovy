/*
 * Wait For Deployment Ready - VERSÃO FINAL
 *
 * Aguarda que o deployment esteja completamente pronto
 * Usa kubectl rollout status para verificar
 *
 * CORRIGIDO: Suporta Windows E Unix (SEM FOR LOOPS)
 *
 * Timeout: 5 minutos
 *
 * Uso:
 *   WaitForDeploymentReady('lendings-q', 'canary', 'dev')
 */

import org.jenkinsPipeline.Constants

def call(String serviceName, String deploymentType, String namespace)
{
    /*
     * deploymentType pode ser:
     * - 'canary' para deployment lendings-q-canary
     * - 'blue' ou 'green' para Blue/Green
     * - ou o deployment name direto
     */

    def deploymentName = deploymentType.contains('-') ? deploymentType : "${serviceName}-${deploymentType}"

    echo """
        ==========================================
        WAITING FOR DEPLOYMENT
        ==========================================
        Deployment: ${deploymentName}
        Namespace: ${namespace}
        Timeout: 5 minutes
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
                    echo "Waiting for deployment ${deploymentName} to be ready..."

                    timeout(time: 5, unit: 'MINUTES')
                            {
                                if (isUnix())
                                {
                                    sh """
                        kubectl rollout status deployment/${deploymentName} \
                            -n ${namespace} \
                            --timeout=5m
                    """
                                }
                                else
                                {
                                    bat """
                        kubectl rollout status deployment/${deploymentName} ^
                            -n ${namespace} ^
                            --timeout=5m
                    """
                                }
                            }

                    echo "✅ Deployment ${deploymentName} is ready!"

                    // Verificar status do deployment
                    echo "VERIFICATION: Deployment status"

                    if (isUnix())
                    {
                        sh """
                    kubectl get deployment ${deploymentName} -n ${namespace}
                """
                    }
                    else
                    {
                        bat """
                    kubectl get deployment ${deploymentName} -n ${namespace}
                """
                    }

                    // Listar pods
                    echo "VERIFICATION: Pods status"

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

        echo "✅ Deployment ${deploymentName} verification completed"
    }
    catch (Exception e)
    {
        echo "❌ Error waiting for deployment: ${e.getMessage()}"
        throw e
    }
}