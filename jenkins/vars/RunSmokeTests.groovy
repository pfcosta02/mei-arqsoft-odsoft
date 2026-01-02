import org.jenkinsPipeline.Constants

def call(String serviceName, String color, String namespace) 
{
    /*
     * Executa testes de smoke na nova instância antes de redirecionar o tráfego
     * 
     * TESTES INCLUÍDOS:
     * 1. Health check endpoint
     * 2. Readiness probe
     * 3. Conectividade básica
     * 4. Validação de endpoints críticos
     */
    
    def deploymentName = "${serviceName}-${color}"
    echo "Running smoke tests on ${deploymentName}..."
    
    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')]) 
    {
        try 
        {
            // Obtém o IP do pod para testes diretos (bypass do service)
            def podIP = ""
            if (isUnix()) 
            {
                podIP = sh(
                    script: "kubectl get pod -l app=${serviceName},color=${color} -n ${namespace} -o jsonpath='{.items[0].status.podIP}'",
                    returnStdout: true
                ).trim()
            } 
            else 
            {
                podIP = bat(
                    script: "@kubectl get pod -l app=${serviceName},color=${color} -n ${namespace} -o jsonpath=\"{.items[0].status.podIP}\"",
                    returnStdout: true
                ).trim()
            }
            
            echo "Pod IP: ${podIP}"
            
            // Teste 1: Health Check
            echo "Test 1: Health Check..."
            if (isUnix()) 
            {
                sh """
                    kubectl run smoke-test-health-${color} --rm -i --restart=Never \
                        --image=curlimages/curl:latest -n ${namespace} \
                        -- curl -f http://${podIP}:8083/actuator/health || exit 1
                """
            } 
            else 
            {
                bat """
                    kubectl run smoke-test-health-${color} --rm -i --restart=Never ^
                        --image=curlimages/curl:latest -n ${namespace} ^
                        -- curl -f http://${podIP}:8083/actuator/health || exit 1
                """
            }
            echo "Health check passed"
            
            // Teste 2: Readiness
            echo "Test 2: Readiness Check..."
            if (isUnix()) 
            {
                sh """
                    kubectl run smoke-test-ready-${color} --rm -i --restart=Never \
                        --image=curlimages/curl:latest -n ${namespace} \
                        -- curl -f http://${podIP}:8083/actuator/health/readiness || exit 1
                """
            } 
            else 
            {
                bat """
                    kubectl run smoke-test-ready-${color} --rm -i --restart=Never ^
                        --image=curlimages/curl:latest -n ${namespace} ^
                        -- curl -f http://${podIP}:8083/actuator/health/readiness || exit 1
                """
            }
            echo "Readiness check passed"
            
            // Teste 3: Endpoint crítico (exemplo: info endpoint)
            echo "Test 3: Application Info endpoint..."
            if (isUnix()) 
            {
                sh """
                    kubectl run smoke-test-info-${color} --rm -i --restart=Never \
                        --image=curlimages/curl:latest -n ${namespace} \
                        -- curl -f http://${podIP}:8083/actuator/info || exit 1
                """
            } 
            else 
            {
                bat """
                    kubectl run smoke-test-info-${color} --rm -i --restart=Never ^
                        --image=curlimages/curl:latest -n ${namespace} ^
                        -- curl -f http://${podIP}:8083/actuator/info || exit 1
                """
            }
            echo "Info endpoint check passed"
            
            echo "All smoke tests passed successfully!"
            
        } 
        catch (Exception e) 
        {
            error "Smoke tests failed: ${e.getMessage()}"
        }
    }
}
