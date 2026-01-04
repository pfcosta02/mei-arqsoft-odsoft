import org.jenkinsPipeline.Constants
//import org.jenkinsPipeline.KubernetesHelper

def call(String serviceName, String dockerImage, String namespace, Integer canaryPercentage)
{
    /*
     * Deploy Canary Instance
     *
     * Cria um deployment canary paralelo com 1 replica
     * Totalmente isolado da versão estável
     *
     * Parâmetros:
     * - serviceName: Nome do serviço (ex: 'lendings-c')
     * - dockerImage: Imagem completa (ex: 'registry.io/lendings-c:1.2.3')
     * - namespace: Namespace K8s (ex: 'dev')
     * - canaryPercentage: % tráfego inicial (ex: 10)
     */

    echo """
        ==========================================
        DEPLOYING CANARY INSTANCE
        ==========================================
        Service: ${serviceName}
        Image: ${dockerImage}
        Initial Traffic: ${canaryPercentage}%
        Namespace: ${namespace}
        ==========================================
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
                    // STEP 1: Verificar se deployment canary já existe
                    def canaryExists = sh(
                            script: "kubectl get deployment ${serviceName}-canary -n ${namespace} 2>/dev/null",
                            returnStatus: true
                    ) == 0

                    if (canaryExists)
                    {
                        echo "Canary deployment already exists. Updating image..."

                        // Apenas atualizar image
                        sh """
                    kubectl set image deployment/${serviceName}-canary \
                        ${serviceName}=${dockerImage} \
                        -n ${namespace} \
                        --record
                """
                    }
                    else
                    {
                        echo "Canary deployment does not exist. Creating..."

                        // Criar novo deployment canary
                        sh """
                    kubectl create deployment ${serviceName}-canary \
                        --image=${dockerImage} \
                        --replicas=1 \
                        -n ${namespace}
                    
                    # Adicionar labels para identificação
                    kubectl patch deployment ${serviceName}-canary -n ${namespace} \
                        -p '{"spec":{"template":{"metadata":{"labels":{"version":"canary"}}}}}'
                """
                    }

                    echo "Canary deployment created/updated successfully"

                    // STEP 2: Aguardar que o canary fique ready
                    echo "Waiting for canary deployment to be ready (max 5 minutes)..."

                    sh """
                kubectl rollout status deployment/${serviceName}-canary \
                    -n ${namespace} \
                    --timeout=5m
            """

                    // STEP 3: Verificar replicas
                    def readyReplicas = sh(
                            script: """
                    kubectl get deployment ${serviceName}-canary -n ${namespace} \
                        -o jsonpath='{.status.readyReplicas}'
                """,
                            returnStdout: true
                    ).trim()

                    echo "Canary deployment ready. Ready replicas: ${readyReplicas}"

                    // STEP 4: Listar pods canary
                    sh """
                echo "Canary pods:"
                kubectl get pods -n ${namespace} -l app=${serviceName}-canary -o wide
            """
                }

        echo "✅ Canary deployment completed successfully"
    }
    catch (Exception e)
    {
        echo "❌ Error deploying canary instance: ${e.getMessage()}"
        throw e
    }
}