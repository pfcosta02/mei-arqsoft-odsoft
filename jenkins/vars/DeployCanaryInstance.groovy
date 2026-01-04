/*
 * Deploy Canary Instance - VERSÃO FINAL
 *
 * Cria um deployment canary isolado com a nova imagem
 * CORRIGIDO: Remove for loops complexos do Windows que causam problemas
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
                            -n ${namespace}
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

                    if (isUnix())
                    {
                        sh """
                    echo "Canary deployment status:"
                    kubectl get deployment ${serviceName}-canary -n ${namespace}
                """
                    }
                    else
                    {
                        // WINDOWS: Usar simples comando sem for loop
                        bat """
                    echo Canary deployment status:
                    kubectl get deployment ${serviceName}-canary -n ${namespace}
                """
                    }

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

                    // STEP 7: Verificar se pod está ready
                    echo "STEP 7: Checking pod readiness..."

                    if (isUnix())
                    {
                        sh """
                    # Aguardar que pelo menos um pod esteja ready
                    for i in {1..30}; do
                        READY=\$(kubectl get deployment ${serviceName}-canary -n ${namespace} -o jsonpath='{.status.readyReplicas}')
                        if [ "\${READY}" == "1" ]; then
                            echo "✅ Canary pod is ready"
                            exit 0
                        fi
                        sleep 2
                    done
                    echo "❌ Timeout waiting for canary pod"
                    exit 1
                """
                    }
                    else
                    {
                        // WINDOWS: Simples verificação (sem for loop)
                        bat """
                    echo Waiting for canary pod readiness...
                    timeout /t 10 /nobreak
                    kubectl get pods -n ${namespace} -l app=${serviceName}-canary
                    echo Canary pod should be running
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