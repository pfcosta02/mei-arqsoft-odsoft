/*
 * Deploy Canary Instance
 *
 * Cria um deployment canary isolado com a nova imagem
 * Aguarda que fique ready
 *
 * CORRIGIDO: Suporta Windows E Unix (patch JSON corrigido)
 *
 * Uso:
 *   DeployCanaryInstance('lendings-q', 'diogomanuel31/lendings-q:0.0.1', 'dev', 10)
 */

import org.jenkinsPipeline.Constants

def call(String serviceName, String dockerImage, String namespace, Integer canaryPercentage)
{
    echo """
        ==========================================
        DEPLOYING CANARY INSTANCE
        ==========================================
        Service: ${serviceName}
        Image: ${dockerImage}
        Initial Traffic: ${canaryPercentage}%
        Namespace: ${namespace}
        OS: ${isUnix() ? 'Unix/Linux' : 'Windows'}
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
                    // STEP 1: Garantir que o namespace existe
                    echo "STEP 1: Ensuring namespace exists..."

                    if (isUnix())
                    {
                        sh """
                    kubectl create namespace ${namespace} --dry-run=client -o yaml | kubectl apply -f -
                """
                    }
                    else
                    {
                        bat """
                    kubectl create namespace ${namespace} --dry-run=client -o yaml | kubectl apply -f -
                """
                    }

                    echo "Namespace ${namespace} ready"

                    // STEP 2: Verificar se canary já existe
                    echo "STEP 2: Checking if canary deployment exists..."

                    def canaryExists
                    if (isUnix())
                    {
                        canaryExists = sh(
                                script: "kubectl get deployment ${serviceName}-canary -n ${namespace} 2>/dev/null",
                                returnStatus: true
                        ) == 0
                    }
                    else
                    {
                        canaryExists = bat(
                                script: "kubectl get deployment ${serviceName}-canary -n ${namespace} >nul 2>&1",
                                returnStatus: true
                        ) == 0
                    }

                    if (canaryExists)
                    {
                        echo "Canary deployment already exists. Updating image..."

                        if (isUnix())
                        {
                            sh """
                        kubectl set image deployment/${serviceName}-canary \
                            ${serviceName}=${dockerImage} \
                            -n ${namespace} \
                            --record
                    """
                        }
                        else
                        {
                            bat """
                        kubectl set image deployment/${serviceName}-canary ^
                            ${serviceName}=${dockerImage} ^
                            -n ${namespace} ^
                            --record
                    """
                        }
                    }
                    else
                    {
                        echo "Creating new canary deployment..."

                        if (isUnix())
                        {
                            sh """
                        kubectl create deployment ${serviceName}-canary \
                            --image=${dockerImage} \
                            --replicas=1 \
                            -n ${namespace}
                    """
                        }
                        else
                        {
                            bat """
                        kubectl create deployment ${serviceName}-canary ^
                            --image=${dockerImage} ^
                            --replicas=1 ^
                            -n ${namespace}
                    """
                        }
                    }

                    echo "Canary deployment created/updated successfully"

                    // STEP 3: Adicionar labels via patch (CORRIGIDO PARA WINDOWS)
                    echo "STEP 3: Adding canary labels..."

                    if (isUnix())
                    {
                        sh """
                    kubectl patch deployment ${serviceName}-canary -n ${namespace} \
                        -p '{"spec":{"template":{"metadata":{"labels":{"version":"canary"}}}}}'
                """
                    }
                    else
                    {
                        // WINDOWS: Usar arquivo temporário em vez de passar JSON na linha de comando
                        def patchJson = '''{"spec":{"template":{"metadata":{"labels":{"version":"canary"}}}}}'''
                        writeFile file: "patch-canary.json", text: patchJson

                        bat """
                    kubectl patch deployment ${serviceName}-canary -n ${namespace} --patch-file patch-canary.json
                """
                    }

                    echo "Labels added successfully"

                    // STEP 4: Aguardar que o canary fique ready
                    echo "STEP 4: Waiting for canary deployment to be ready (max 5 minutes)..."

                    if (isUnix())
                    {
                        sh """
                    kubectl rollout status deployment/${serviceName}-canary \
                        -n ${namespace} \
                        --timeout=5m
                """
                    }
                    else
                    {
                        bat """
                    kubectl rollout status deployment/${serviceName}-canary ^
                        -n ${namespace} ^
                        --timeout=5m
                """
                    }

                    // STEP 5: Verificar replicas
                    echo "STEP 5: Verifying canary replica status..."

                    def readyReplicas
                    if (isUnix())
                    {
                        readyReplicas = sh(
                                script: """
                        kubectl get deployment ${serviceName}-canary -n ${namespace} \
                            -o jsonpath='{.status.readyReplicas}'
                    """,
                                returnStdout: true
                        ).trim()
                    }
                    else
                    {
                        readyReplicas = bat(
                                script: """
                        @for /f %%%%i in ('kubectl get deployment ${serviceName}-canary -n ${namespace} -o jsonpath="{.status.readyReplicas}"') do @echo %%%%i
                    """,
                                returnStdout: true
                        ).trim()
                    }

                    echo "Canary deployment ready. Ready replicas: ${readyReplicas}"

                    // STEP 6: Listar pods canary
                    echo "STEP 6: Canary pods:"

                    if (isUnix())
                    {
                        sh """
                    kubectl get pods -n ${namespace} -l app=${serviceName}-canary -o wide
                """
                    }
                    else
                    {
                        bat """
                    kubectl get pods -n ${namespace} -l app=${serviceName}-canary -o wide
                """
                    }
                }

        echo "✅ Canary deployment completed successfully"
    }
    catch (Exception e)
    {
        echo "❌ Error deploying canary instance: ${e.getMessage()}"
        throw e
    }
}