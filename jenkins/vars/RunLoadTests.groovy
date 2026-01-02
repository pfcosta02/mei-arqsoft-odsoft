#!/usr/bin/env groovy

/**
 * Run load tests against deployed service and monitor pod scaling
 * 
 * This script:
 * 1. Runs k6 load tests against the target service
 * 2. Monitors pod count during the test
 * 3. Verifies that HPA is scaling pods based on load
 * 4. Generates report with scaling metrics
 * 
 * @param serviceName - Name of the service to test
 * @param color - Color of the deployment (blue/green)
 * @param namespace - Kubernetes namespace
 */
def call(String serviceName, String color, String namespace) 
{
    echo """==========================================
            LOAD TEST EXECUTION
            ==========================================
            Service: ${serviceName}
            Color: ${color}
            Namespace: ${namespace}
            ==========================================
        """
    
    // Get service endpoint
    def nodePort = ""
    if (isUnix()) 
    {
        nodePort = sh(
            script: "kubectl get svc ${serviceName} -n ${namespace} -o jsonpath='{.spec.ports[0].nodePort}'",
            returnStdout: true
        ).trim()
    } 
    else 
    {
        nodePort = bat(
            script: "@kubectl get svc ${serviceName} -n ${namespace} -o jsonpath=\"{.spec.ports[0].nodePort}\"",
            returnStdout: true
        ).trim()
    }
    
    if (!nodePort) {
        error "Could not get service NodePort"
    }
    
    // Get Minikube IP
    def minikubeIP = ""
    if (isUnix()) 
    {
        minikubeIP = sh(script: "minikube ip", returnStdout: true).trim()
    } 
    else 
    {
        minikubeIP = bat(script: "@minikube ip", returnStdout: true).trim()
    }
    
    if (!minikubeIP) {
        error "Could not get Minikube IP"
    }
    
    def baseURL = "http://${minikubeIP}:${nodePort}"
    echo "Target URL: ${baseURL}"
    
    // Get initial pod count
    def initialPods = 0
    if (isUnix()) 
    {
        initialPods = sh(
            script: "kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} --no-headers | wc -l",
            returnStdout: true
        ).trim().toInteger()
    } 
    else 
    {
        def podsOutput = bat(
            script: "@kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} --no-headers",
            returnStdout: true
        ).trim()
        initialPods = podsOutput ? podsOutput.split('\n').size() : 0
    }
    
    echo "Initial pod count: ${initialPods}"
    
    // Run load test
    echo "Starting k6 load test..."
    
    def timestamp = new Date().format('yyyyMMdd_HHmmss')
    def resultFile = "load-test-${timestamp}.txt"
    
    try 
    {
        if (isUnix()) 
        {
            // Try k6 directly first, fallback to Docker
            def k6Available = sh(script: "command -v k6", returnStatus: true) == 0
            
            if (k6Available) 
            {
                sh """
                    k6 run --out json=load-test-${timestamp}.json \\
                         --env BASE_URL=${baseURL} \\
                         load-tests/scalability-test.js | tee ${resultFile}
                """
            } 
            else 
            {
                sh """
                    docker run --rm -i \\
                        -v \$(pwd)/load-tests:/scripts \\
                        -e BASE_URL=${baseURL} \\
                        grafana/k6 run /scripts/scalability-test.js | tee ${resultFile}
                """
            }
        } 
        else 
        {
            // Windows - use Docker
            bat """
                docker run --rm -i ^
                    -v "%CD%\\load-tests:/scripts" ^
                    -e BASE_URL=${baseURL} ^
                    grafana/k6 run /scripts/scalability-test.js > ${resultFile} 2>&1
                
                type ${resultFile}
            """
        }
    } 
    catch (Exception e) 
    {
        echo "WARNING: Load test execution had issues: ${e.message}"
        echo "Continuing to check scaling behavior..."
    }
    
    // Wait a bit for scaling to occur
    echo "Waiting 30 seconds for potential scaling..."
    sleep 30
    
    // Get final pod count
    def finalPods = 0
    if (isUnix()) 
    {
        finalPods = sh(
            script: "kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} --no-headers | wc -l",
            returnStdout: true
        ).trim().toInteger()
    } 
    else 
    {
        def podsOutput = bat(
            script: "@kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} --no-headers",
            returnStdout: true
        ).trim()
        finalPods = podsOutput ? podsOutput.split('\n').size() : 0
    }
    
    echo "Final pod count: ${finalPods}"
    
    // Display scaling results
    echo """==========================================
            LOAD TEST RESULTS
            ==========================================
            Initial Pods: ${initialPods}
            Final Pods: ${finalPods}"""
    
    if (finalPods > initialPods) 
    {
        echo "SCALING DETECTED: Pods increased by ${finalPods - initialPods}"
        echo "HPA is working correctly - system scales under load"
    } 
    else if (finalPods < initialPods) 
    {
        echo "○ Pods decreased by ${initialPods - finalPods}"
    } 
    else 
    {
        echo "○ No scaling observed (may need higher load or HPA not configured)"
    }
    echo "=========================================="
    
    // Archive results
    archiveArtifacts artifacts: resultFile, allowEmptyArchive: true
    
    return [
        initialPods: initialPods,
        finalPods: finalPods,
        scaled: finalPods > initialPods
    ]
}
