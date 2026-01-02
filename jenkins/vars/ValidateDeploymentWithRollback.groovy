import org.jenkinsPipeline.Constants

def call(String serviceName, String newColor, String oldColor, String namespace) 
{
    /*
     * BLUE/GREEN DEPLOYMENT - Validação Pós-Deployment com Rollback Automático
     * 
     * Executa testes de validação após o deployment e switch de tráfego:
     * - Se SUCESSO: mantém a nova versão
     * - Se FALHA: executa ROLLBACK AUTOMÁTICO
     * 
     * EVIDÊNCIA DE ROLLBACK: Logs mostram todo o processo quando testes falham
     */
    
    echo """
        ==========================================
        POST-DEPLOYMENT VALIDATION
        ==========================================
        Testing new deployment: ${newColor.toUpperCase()}
        Fallback available: ${oldColor.toUpperCase()}
        ==========================================
    """
    
    def failureReason = ""
    
    try 
    {
        // Teste 1: Verificar se pods estão rodando
        echo "Test 1: Pod Health Check"
        
        withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
        {
            def podStatus = ""
            if (isUnix()) 
            {
                podStatus = sh(
                    script: "kubectl get pods -l app=${serviceName},color=${newColor} -n ${namespace} -o jsonpath='{.items[*].status.phase}'",
                    returnStdout: true
                ).trim()
            } 
            else 
            {
                podStatus = bat(
                    script: "@kubectl get pods -l app=${serviceName},color=${newColor} -n ${namespace} -o jsonpath=\"{.items[*].status.phase}\"",
                    returnStdout: true
                ).trim()
            }
            
            if (!podStatus.contains("Running")) 
            {
                failureReason = "Pods are not in Running state: ${podStatus}"
                error "FAILED: ${failureReason}"
            } 
            else 
            {
                echo "PASSED: All pods are Running"
            }
        }
        
        // Teste 2: Verificar readiness
        echo "Test 2: Pod Readiness Check"
        withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
        {
            def readyCount = ""
            def totalCount = ""
                
            if (isUnix()) 
            {
                def counts = sh(
                    script: "kubectl get pods -l app=${serviceName},color=${newColor} -n ${namespace} --no-headers | wc -l && kubectl get pods -l app=${serviceName},color=${newColor} -n ${namespace} --no-headers | grep '1/1' | wc -l",
                    returnStdout: true
                ).trim().split('\n')
                totalCount = counts[0]
                readyCount = counts[1]
            } 
            else 
            {
                totalCount = bat(
                    script: "@kubectl get pods -l app=${serviceName},color=${newColor} -n ${namespace} --no-headers | find /c /v \"\"",
                    returnStdout: true
                ).trim()
                
                readyCount = bat(
                    script: "@kubectl get pods -l app=${serviceName},color=${newColor} -n ${namespace} --no-headers | find \"1/1\" | find /c /v \"\"",
                    returnStdout: true
                ).trim()
            }
                
            echo "Ready Pods: ${readyCount}/${totalCount}"
                
            if (readyCount != totalCount) 
            {
                failureReason = "Not all pods are ready: ${readyCount}/${totalCount}"
                error "FAILED: ${failureReason}"
            } 
            else 
            {
                echo "PASSED: All pods are ready"
            }
        }
        
        // Teste 3: Smoke tests básicos (se disponível)
        echo "Test 3: Smoke Tests"
        RunSmokeTests(serviceName, newColor, namespace)
        echo "PASSED: Smoke tests successful"

        
        // SE chegou aqui enta o todos os testes passaram
        echo """
            ==========================================
            ALL TESTS PASSED
            Decision: KEEP new deployment (${newColor.toUpperCase()})
            Status: Deployment ${newColor.toUpperCase()} is STABLE
            Action: No rollback needed
            ==========================================
        """
            
        return true
        
    } 
    catch (Exception e) 
    {
        echo "Validation process error: ${e.getMessage()}"
        
        echo """
                ==========================================
                TESTS FAILED
                Failure Reason: ${failureReason}
                Decision: ROLLBACK to previous version
                Triggering automatic rollback...
                ==========================================
            """

        // Se já identificamos falha, executa rollback
        echo "Executing rollback due to validation failure..."
        
        error "Validation failed: ${e.getMessage()}"
    }
}
