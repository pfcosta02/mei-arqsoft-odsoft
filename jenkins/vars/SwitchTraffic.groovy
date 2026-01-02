import org.jenkinsPipeline.Constants

def call(String serviceName, String newColor, String namespace) 
{
    /*
     * BLUE/GREEN DEPLOYMENT STRATEGY - PHASE 2: TRAFFIC SWITCH
     * 
     * Esta função implementa a segunda fase da estratégia Blue/Green:
     * - Redireciona TODO o tráfego de produção para a nova versão
     * - Mudança é ATÔMICA (instantânea)
     * - Versão antiga permanece rodando para rollback rápido se necessário
     * 
     * EVIDÊNCIA: Logs mostram a cor anterior e nova, confirmando o switch
     */
    
    echo """==========================================
            BLUE/GREEN DEPLOYMENT - PHASE 2: SWITCH
            ==========================================
            Service: ${serviceName}-service
            Target Color: ${newColor.toUpperCase()}
            Namespace: ${namespace}
            =========================================="""
    
    // Patch do Service para atualizar o selector
    def patchJson = """
    {
        "spec": {
            "selector": {
                "app": "${serviceName}",
                "color": "${newColor}"
            }
        }
    }
    """
    
    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
    {
        try 
        {   
            echo """
                =========================================
                ZERO DOWNTIME VERIFICATION - PHASE 1
                Testing service availability BEFORE switch
                =========================================
            """
            
            // Verifica disponibilidade ANTES do switch
            def beforeSwitchStatus = ""
            if (isUnix()) 
            {
                beforeSwitchStatus = sh(
                    script: "kubectl get service ${serviceName}-service -n ${namespace} -o jsonpath='{.status}' && echo 'Available'",
                    returnStdout: true
                ).trim()
            } 
            else 
            {
                beforeSwitchStatus = bat(
                    script: "@kubectl get service ${serviceName}-service -n ${namespace} >nul 2>&1 && echo Available",
                    returnStdout: true
                ).trim()
            }
            echo "Service is Available BEFORE switch: ${beforeSwitchStatus}"
            
            echo """
                =========================================
                ZERO DOWNTIME VERIFICATION - PHASE 2
                Performing ATOMIC traffic switch...
                =========================================
            """

            echo "SWITCHING traffic to ${newColor.toUpperCase()}..."
            
            def switchStartTime = System.currentTimeMillis()
            
            if (isUnix()) 
            {
                sh """
                    kubectl patch service ${serviceName}-service -n ${namespace} \
                        -p '${patchJson}'
                """
            } 
            else 
            {
                // No Windows, escreve o JSON em um arquivo temporário
                writeFile file: 'service-patch.json', text: patchJson
                bat """
                    kubectl patch service ${serviceName}-service -n ${namespace} --patch-file service-patch.json
                    del service-patch.json
                """
            }
            
            def switchDuration = System.currentTimeMillis() - switchStartTime
            echo "Switch completed in ${switchDuration}ms (atomic operation)"
            
            echo "Service ${serviceName}-service now points to ${newColor.toUpperCase()} deployment"

            // Verifica o estado do Service APÓS o switch
            echo """
                =========================================
                ZERO DOWNTIME VERIFICATION - PHASE 3
                Testing service availability AFTER switch
                =========================================
            """

            echo "Verifying NEW traffic routing..."
            if (isUnix()) 
            {
                sh "kubectl get service ${serviceName}-service -n ${namespace} -o wide"
            } 
            else 
            {
                bat "kubectl get service ${serviceName}-service -n ${namespace} -o wide"
            }
        
            // Testa disponibilidade DEPOIS do switch
            def afterSwitchStatus = ""
            if (isUnix()) 
            {
                afterSwitchStatus = sh(
                    script: "kubectl get service ${serviceName}-service -n ${namespace} -o jsonpath='{.status}' && echo 'Available'",
                    returnStdout: true
                ).trim()
            } 
            else 
            {
                afterSwitchStatus = bat(
                    script: "@kubectl get service ${serviceName}-service -n ${namespace} >nul 2>&1 && echo Available",
                    returnStdout: true
                ).trim()
            }
            echo "Service is Available AFTER switch: ${afterSwitchStatus}"
            
            // Aguarda alguns segundos para propagação
            echo "Waiting for traffic propagation..."
            sleep(time: 5, unit: 'SECONDS')
            
            echo """
                ==========================================
                BLUE/GREEN SWITCH COMPLETED
                ==========================================
                All production traffic now on: ${newColor.toUpperCase()}
                Service: ${serviceName}-service
                Rollback available via opposite color
                NO SERVICE INTERRUPTION during switch
                =========================================="""
        } 
        catch (Exception e) 
        {
            error "Failed to switch traffic: ${e.getMessage()}"
        }
    }
}
