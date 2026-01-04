/*
 * Ensure Stable Deployment Exists
 *
 * Garante que o deployment estável existe antes de fazer canary deploy
 * Se não existir, cria com a imagem fornecida
 *
 * Uso:
 *   EnsureStableDeploymentExists('lendings-q', 'diogomanuel31/lendings-q:1.0', 'dev')
 */

import org.jenkinsPipeline.Constants

def call(String serviceName, String dockerImage, String namespace)
{
    echo """
        ==========================================
        ENSURING STABLE DEPLOYMENT EXISTS
        ==========================================
        Service: ${serviceName}
        Image: ${dockerImage}
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
                    // STEP 1: Verificar se deployment estável existe
                    echo "STEP 1: Checking if stable deployment exists..."

                    def deploymentExists
                    if (isUnix())
                    {
                        deploymentExists = sh(
                                script: "kubectl get deployment ${serviceName} -n ${namespace} 2>/dev/null",
                                returnStatus: true
                        ) == 0
                    }
                    else
                    {
                        deploymentExists = bat(
                                script: "kubectl get deployment ${serviceName} -n ${namespace} >nul 2>&1",
                                returnStatus: true
                        ) == 0
                    }

                    if (deploymentExists)
                    {
                        echo "✅ Stable deployment ${serviceName} already exists"

                        // Mostrar informações do deployment
                        if (isUnix())
                        {
                            sh "kubectl get deployment ${serviceName} -n ${namespace} -o wide"
                        }
                        else
                        {
                            bat "kubectl get deployment ${serviceName} -n ${namespace} -o wide"
                        }
                    }
                    else
                    {
                        echo "STEP 2: Creating stable deployment ${serviceName}..."

                        // Ler o ficheiro YAML template
                        def yamlPath = "infra/${serviceName}-stable.yaml"

                        if (isUnix())
                        {
                            // Unix: usar sed para substituir placeholders
                            sh """
                        echo "Reading template from ${yamlPath}..."
                        sed -e 's|{{IMAGE_STABLE}}|${dockerImage}|g' \
                            ${yamlPath} | kubectl apply -n ${namespace} -f -
                    """
                        }
                        else
                        {
                            // Windows: ler, substituir e aplicar
                            try {
                                def yamlContent = readFile(yamlPath)
                                yamlContent = yamlContent.replace('{{IMAGE_STABLE}}', dockerImage)

                                // Escrever em ficheiro temporário
                                def tempFile = "temp-stable-deployment.yaml"
                                writeFile file: tempFile, text: yamlContent

                                bat """
                            echo Reading template from ${yamlPath}...
                            echo Applying deployment...
                            kubectl apply -f ${tempFile} -n ${namespace}
                            del ${tempFile}
                        """
                            } catch (Exception e) {
                                echo "⚠️  Could not read ${yamlPath}, creating deployment via kubectl..."

                                // Fallback: criar via kubectl direto (sem template)
                                bat """
                            echo Creating deployment via kubectl create...
                            kubectl create deployment ${serviceName} ^
                                --image=${dockerImage} ^
                                --replicas=2 ^
                                -n ${namespace}
                        """
                            }
                        }

                        echo "✅ Stable deployment ${serviceName} created"
                    }

                    // STEP 3: Garantir que Service existe
                    echo "STEP 3: Checking if service exists..."

                    def serviceExists
                    if (isUnix())
                    {
                        serviceExists = sh(
                                script: "kubectl get service ${serviceName}-service -n ${namespace} 2>/dev/null",
                                returnStatus: true
                        ) == 0
                    }
                    else
                    {
                        serviceExists = bat(
                                script: "kubectl get service ${serviceName}-service -n ${namespace} >nul 2>&1",
                                returnStatus: true
                        ) == 0
                    }

                    if (!serviceExists)
                    {
                        echo "STEP 4: Creating service ${serviceName}-service..."

                        // Ler o ficheiro YAML template
                        def serviceYamlPath = "infra/${serviceName}-service.yaml"

                        if (isUnix())
                        {
                            sh """
                        echo "Reading service template from ${serviceYamlPath}..."
                        kubectl apply -f ${serviceYamlPath} -n ${namespace}
                    """
                        }
                        else
                        {
                            try {
                                def serviceYaml = readFile(serviceYamlPath)
                                def tempServiceFile = "temp-service.yaml"
                                writeFile file: tempServiceFile, text: serviceYaml

                                bat """
                            echo Reading service template...
                            kubectl apply -f ${tempServiceFile} -n ${namespace}
                            del ${tempServiceFile}
                        """
                            } catch (Exception e) {
                                echo "⚠️  Could not read ${serviceYamlPath}, creating service via kubectl..."

                                // Fallback: criar service via kubectl
                                bat """
                            echo Creating service via kubectl expose...
                            kubectl expose deployment ${serviceName} ^
                                --name=${serviceName}-service ^
                                --port=80 ^
                                --target-port=8882 ^
                                --type=ClusterIP ^
                                -n ${namespace}
                        """
                            }
                        }

                        echo "✅ Service ${serviceName}-service created"
                    }
                    else
                    {
                        echo "✅ Service ${serviceName}-service already exists"
                    }

                    // STEP 5: Aguardar que deployment fique ready
                    echo "STEP 5: Waiting for deployment to be ready..."

                    if (isUnix())
                    {
                        sh """
                    kubectl wait --for=condition=available --timeout=300s \
                        deployment/${serviceName} -n ${namespace}
                """
                    }
                    else
                    {
                        bat """
                    kubectl wait --for=condition=available --timeout=300s ^
                        deployment/${serviceName} -n ${namespace}
                """
                    }

                    echo "✅ Stable deployment infrastructure ready"
                }
    }
    catch (Exception e)
    {
        echo "❌ Error ensuring stable deployment: ${e.getMessage()}"
        throw e
    }
}