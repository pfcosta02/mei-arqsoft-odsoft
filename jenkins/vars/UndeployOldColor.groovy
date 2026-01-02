import org.jenkinsPipeline.Constants

def call(String serviceName, String oldColor, String namespace) {
    /*
     * Remove o deployment da cor antiga após o switch bem-sucedido
     * 
     * IMPLEMENTAÇÃO:
     * - Apenas deleta o deployment (não o Service)
     * - Mantém configurações e secrets intactos
     * - Libera recursos do cluster
     * 
     * NOTA: Este step pode ser configurado para esperar um período
     * antes de deletar, permitindo rollback manual se necessário
     */
    
    def deploymentName = "${serviceName}-${oldColor}"
    echo "Removing old deployment: ${deploymentName}"
    
    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
    {
        try 
        {
            // Opcional: Aguardar um período de "soak time" antes de deletar
            // Descomente as linhas abaixo se quiser um período de validação
            // echo "Waiting 2 minutes soak time before removing old deployment..."
            // sleep(time: 2, unit: 'MINUTES')
            
            if (isUnix()) 
            {
                sh """
                    kubectl delete deployment ${deploymentName} -n ${namespace} --ignore-not-found=true
                """
            } 
            else 
            {
                bat """
                    kubectl delete deployment ${deploymentName} -n ${namespace} --ignore-not-found=true
                """
            }
            
            echo "Old deployment ${deploymentName} removed successfully"
            
            // Lista deployments restantes
            echo "Remaining deployments:"
            if (isUnix()) 
            {
                sh "kubectl get deployments -n ${namespace} -l app=${serviceName}"
            } 
            else 
            {
                bat "kubectl get deployments -n ${namespace} -l app=${serviceName}"
            }
            
        } 
        catch (Exception e) 
        {
            echo "Warning: Failed to remove old deployment: ${e.getMessage()}"
            echo "This is not critical - old deployment may need manual cleanup"
        }
    }
}
