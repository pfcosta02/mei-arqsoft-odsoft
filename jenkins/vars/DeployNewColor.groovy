import org.jenkinsPipeline.Constants

def call(String serviceName, String color, String dockerImage, String namespace) 
{
    /*
     * BLUE/GREEN DEPLOYMENT STRATEGY - Deploy da Nova Versão
     * 
     * Esta função implementa a primeira fase da estratégia Blue/Green:
     * 1. Deploy da nova versão com uma cor específica (blue ou green)
     * 2. A nova versão fica isolada até o switch de tráfego
     * 
     * EVIDÊNCIA: Logs mostram qual cor está sendo deployada e que ela
     *            está separada da versão em produção (cor oposta)
     * 
     * Parâmetros:
     * - serviceName: Nome base do serviço (ex: lms-authnusers-command)
     * - color: Cor do deployment (blue ou green)
     * - dockerImage: Imagem Docker completa com tag (ex: registry/image:version)
     * - namespace: Namespace do Kubernetes
     */
    
    echo "=========================================="
    echo "BLUE/GREEN DEPLOYMENT - PHASE 1: DEPLOY"
    echo "=========================================="
    echo "Service: ${serviceName}"
    echo "Target Color: ${color}"
    echo "Image: ${dockerImage}"
    echo "Namespace: ${namespace}"
    echo "=========================================="
    
    def deploymentName = "${serviceName}-${color}"
    
    // STEP 0: Garante que a infraestrutura base existe (Service e HPAs)
    echo "Ensuring Blue/Green infrastructure exists..."
    EnsureBlueGreenInfrastructure(serviceName, namespace)
    
    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
    {   
        // Verifica se o deployment já existe
        def deploymentExists = false
        try 
        {
            if (isUnix()) 
            {
                sh "kubectl get deployment ${deploymentName} -n ${namespace}"
                deploymentExists = true
            } 
            else 
            {
                bat "kubectl get deployment ${deploymentName} -n ${namespace}"
                deploymentExists = true
            }
        } 
        catch (Exception e) 
        {
            echo "Deployment ${deploymentName} does not exist yet. Will create new one."
            deploymentExists = false
        }
        
        if (deploymentExists) 
        {
            // Deployment existe - atualiza apenas a imagem
            echo "Deployment ${deploymentName} already exists - updating image..."
            echo "Previous deployment will be updated with new image"
            
            if (isUnix()) 
            {
                sh """
                    kubectl set image deployment/${deploymentName} \
                        ${serviceName}=${dockerImage} \
                        -n ${namespace} \
                        --record
                """
            } 
            else 
            {
                bat """
                    kubectl set image deployment/${deploymentName} ^
                        ${serviceName}=${dockerImage} ^
                        -n ${namespace} ^
                        --record
                """
            }
            
            echo "Image updated successfully for ${deploymentName}"
            
        } 
        else 
        {
            // Deployment não existe - cria um novo
            echo "Creating new deployment ${deploymentName}..."
            echo "This is a NEW ${color.toUpperCase()} environment deployment"
            
            // Aplica o manifesto do deployment
            def deploymentFile = "infra/${serviceName}-deployment.yaml"
            
            if (isUnix()) 
            {
                sh """
                    # Cria uma cópia do deployment substituindo a cor e a imagem
                    sed -e 's/{{COLOR}}/${color}/g' \
                        -e 's|{{IMAGE}}|${dockerImage}|g' \
                        ${deploymentFile} | kubectl apply -n ${namespace} -f -
                """
            } 
            else 
            {
                // No Windows, lê o arquivo, substitui os placeholders e aplica
                def yamlContent = readFile(deploymentFile)
                yamlContent = yamlContent.replace('{{COLOR}}', color)
                yamlContent = yamlContent.replace('{{IMAGE}}', dockerImage)
                yamlContent = yamlContent.replace('{{NAMESPACE}}', namespace)
                
                // Escreve o YAML processado em um arquivo temporário
                def tempFile = "temp-deployment-${color}.yaml"
                writeFile file: tempFile, text: yamlContent
                
                bat """
                    @echo off
                    echo Applying deployment manifest for ${color}...
                    kubectl apply -f ${tempFile} -n ${namespace}
                    del ${tempFile}
                """
            }
            
            echo "Deployment ${deploymentName} created successfully"
        }
    }

    echo """
        ==========================================
        BLUE/GREEN DEPLOYMENT STATUS
        ==========================================
            ${color.toUpperCase()} deployment ready
            Deployment Name: ${deploymentName}
            Image: ${dockerImage}
            Traffic NOT yet switched to ${color}
            (Use SwitchTraffic to complete deployment)
        ==========================================
        Deployment ${deploymentName} created successfully"""
}

