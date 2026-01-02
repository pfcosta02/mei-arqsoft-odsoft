import org.jenkinsPipeline.Constants

def call(String serviceName, String color, String namespace) {
    /*
     * Aguarda que o deployment esteja completamente pronto
     * Usa kubectl rollout status para verificar
     * 
     * Timeout: 5 minutos
     */
    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
    {
        def deploymentName = "${serviceName}-${color}"
        echo "Waiting for deployment ${deploymentName} to be ready..."
        
        timeout(time: 5, unit: 'MINUTES') 
        {
            if (isUnix()) 
            {
                sh """
                    kubectl rollout status deployment/${deploymentName} -n ${namespace} --timeout=5m
                """
            } 
            else 
            {
                bat """
                    kubectl rollout status deployment/${deploymentName} -n ${namespace} --timeout=5m
                """
            }
        }
        
        echo "Deployment ${deploymentName} is ready!"
        
        // Verifica adicionalmente o número de réplicas prontas
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
    }
}
