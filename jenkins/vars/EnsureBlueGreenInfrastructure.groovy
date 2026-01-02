import org.jenkinsPipeline.Constants

def call(String serviceName, String namespace) 
{
    /*
     * BLUE/GREEN DEPLOYMENT - Setup de HPAs
     * 
     * Garante que os HPAs (Horizontal Pod Autoscalers) para Blue/Green existem.
     * 
     * NOTA: Namespace e Service já foram criados por EnsureK8sStructure
     */
    
    echo """==========================================
            BLUE/GREEN - HPA Setup
        ==========================================
            Service: ${serviceName}
            Namespace: ${namespace}
        =========================================="""
    
    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
    {
        // Verifica/Cria HPAs para Blue e Green
        def hpaExists = false
        try 
        {
            if (isUnix()) 
            {
                sh "kubectl get hpa ${serviceName}-scaler-blue -n ${namespace}"
                hpaExists = true
            } 
            else 
            {
                bat "@kubectl get hpa ${serviceName}-scaler-blue -n ${namespace} >nul 2>&1"
                hpaExists = true
            }
        } 
        catch (Exception e) 
        {
            hpaExists = false
        }
        
        if (!hpaExists) 
        {
            echo "Creating HPAs (blue and green)..."
            
            def hpaFile = "infra/${serviceName}-scaler.yaml"
            
            if (isUnix()) 
            {
                sh "kubectl apply -f ${hpaFile} -n ${namespace}"
            } 
            else 
            {
                bat "kubectl apply -f ${hpaFile}"
            }
            
            echo "HPAs created successfully"
        } 
        else 
        {
            echo "HPAs already exist"
        }
        
        echo """==========================================
                Blue/Green HPAs Ready
            ==========================================
                HPAs: ${serviceName}-scaler-blue/green
            =========================================="""
    }
}
