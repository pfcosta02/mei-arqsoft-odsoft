import org.jenkinsPipeline.Constants

def call(String serviceName, String namespace) 
{
    /*
     * BLUE/GREEN DEPLOYMENT STRATEGY - Determina Cor Atual
     * 
     * Obtém a cor (blue/green) do deployment atualmente em produção
     * através da análise do selector do Service
     * 
     * EVIDÊNCIA: Mostra qual cor está recebendo tráfego de produção
     * 
     * Retorna: 'blue', 'green', ou 'none' se nenhum deployment existir
     */
    
    echo """==========================================
            BLUE/GREEN - Detecting Current Color
            ==========================================
            Service: ${serviceName}-service
            Namespace: ${namespace}"""
    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
    {
        try 
        {
            def selector = ""
            
            if (isUnix()) 
            {
                selector = sh(
                    script: "kubectl get service ${serviceName}-service -n ${namespace} -o jsonpath='{.spec.selector.color}' 2>/dev/null || echo 'none'",
                    returnStdout: true
                ).trim()
            } 
            else 
            {
                selector = bat(
                    script: """
                            @echo off
                            kubectl get service ${serviceName}-service -n ${namespace} -o jsonpath="{.spec.selector.color}" 2>nul || echo none
                            """,
                    returnStdout: true
                ).trim()
            }
            
            if (selector == '' || selector == 'none') 
            {
                echo """  No active deployment found
                    This appears to be the FIRST deployment
                    Next deployment will be: BLUE
                =========================================="""
                return 'none'
            }
            
            echo """  Current PRODUCTION color: ${selector.toUpperCase()}
            Next deployment will be: ${selector == 'blue' ? 'GREEN' : 'BLUE'}
            =========================================="""
            return selector
            
        } 
        catch (Exception e) 
        {
            echo "Warning: Could not determine current color. Assuming this is the first deployment."
            echo "Error: ${e.getMessage()}"
            return 'none'
        }
    }
}
