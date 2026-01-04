import org.jenkinsPipeline.Constants
/**
 * Runs health checks on a specific deployment color
 *
 * @param serviceName Nome do serviço (ex: 'lms-authnusers-command')
 * @param color Cor do deployment a testar ('blue' ou 'green')
 * @param namespace Namespace do Kubernetes
 */
def call(String serviceName, String color, String namespace)
{
    echo """==================== Health Checks ===================="
        Service: ${serviceName}
        Color: ${color}
        Namespace: ${namespace}
        ======================================================="""

    withCredentials([file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')])
            {
                try
                {
                    // Obter a porta do deployment YAML
                    def servicePort = ""
                    def deploymentFile = "infra/${serviceName}-deployment.yaml"

                    if (fileExists(deploymentFile)) {
                        def deploymentYaml = readFile(deploymentFile)
                        def portMatch = (deploymentYaml =~ /containerPort:\s*(\d+)/)
                        if (portMatch)
                        {
                            servicePort = portMatch[0][1]
                            echo "Port detected from deployment YAML: ${servicePort}"
                        }
                        else
                        {
                            error("Could not find containerPort in ${deploymentFile}")
                        }
                    }
                    else
                    {
                        error("Deployment file not found: ${deploymentFile}")
                    }
                    // 1. Verificar se o deployment existe e está pronto
                    echo "Step 1: Checking if deployment exists and is ready..."
                    def deploymentName = "${serviceName}-${color}"

                    if (isUnix()) {
                        sh """
                    export KUBECONFIG=\${KUBECONFIG}
                    kubectl get deployment ${deploymentName} -n ${namespace}
                """
                    } else {
                        bat """
                    set KUBECONFIG=%KUBECONFIG%
                    kubectl get deployment ${deploymentName} -n ${namespace}
                """
                    }

                    // 2. Verificar se os pods estão em estado Running
                    echo "Step 2: Checking if pods are in Running state..."
                    if (isUnix())
                    {
                        def podStatus = sh(
                                script: """
                        export KUBECONFIG=\${KUBECONFIG}
                        kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} \
                        -o jsonpath='{.items[*].status.phase}' | tr ' ' '\\n' | sort -u
                    """,
                                returnStdout: true
                        ).trim()

                        if (podStatus != 'Running')
                        {
                            error("Pods are not in Running state. Current state: ${podStatus}")
                        }
                    }
                    else
                    {
                        def podStatus = bat(
                                script: """
                        @echo off
                        set KUBECONFIG=%KUBECONFIG%
                        kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} -o jsonpath="{.items[*].status.phase}"
                    """,
                                returnStdout: true
                        ).trim()

                        // Remove "echo off" output and get last line
                        def lines = podStatus.split('\n')
                        podStatus = lines[-1].trim()

                        if (!podStatus.contains('Running'))
                        {
                            error("Pods are not in Running state. Current state: ${podStatus}")
                        }
                    }
                    echo "All pods are Running"

                    // 3. Verificar readiness dos pods
                    echo "Step 3: Checking pod readiness..."
                    if (isUnix())
                    {
                        def readyCount = sh(
                                script: """
                        export KUBECONFIG=\${KUBECONFIG}
                        kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} \
                        -o jsonpath='{.items[*].status.conditions[?(@.type=="Ready")].status}' | \
                        grep -o True | wc -l
                    """,
                                returnStdout: true
                        ).trim()

                        def totalPods = sh(
                                script: """
                        export KUBECONFIG=\${KUBECONFIG}
                        kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} \
                        --no-headers | wc -l
                    """,
                                returnStdout: true
                        ).trim()

                        if (readyCount != totalPods) {
                            error("Not all pods are ready. Ready: ${readyCount}/${totalPods}")
                        }
                    } else {
                        bat """
                    set KUBECONFIG=%KUBECONFIG%
                    kubectl get pods -n ${namespace} -l app=${serviceName},color=${color}
                """
                    }
                    echo "All pods are ready"

                    // 4. Testar endpoint de health diretamente no pod da nova cor
                    echo "Step 4: Testing health endpoint on ${color} pod (before traffic switch)..."

                    // Obter o nome de um pod da cor especificada
                    def podName = ""
                    if (isUnix()) {
                        podName = sh(
                                script: """
                        export KUBECONFIG=\${KUBECONFIG}
                        kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} \
                        -o jsonpath='{.items[0].metadata.name}'
                    """,
                                returnStdout: true
                        ).trim()
                    } else {
                        def output = bat(
                                script: """
                        @echo off
                        set KUBECONFIG=%KUBECONFIG%
                        kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} -o jsonpath="{.items[0].metadata.name}"
                    """,
                                returnStdout: true
                        ).trim()

                        def lines = output.split('\n')
                        podName = lines[-1].trim()
                    }

                    echo "Testing health endpoint inside pod: ${podName}"

                    // Executar curl DENTRO do pod para testar health endpoint
                    if (isUnix()) {
                        sh """
                    export KUBECONFIG=\${KUBECONFIG}
                    
                    # Executar health check dentro do pod
                    HEALTH_RESPONSE=\$(kubectl exec ${podName} -n ${namespace} -- \
                        wget -q -O /dev/null -S http://localhost:${servicePort}/actuator/health 2>&1 | \
                        grep "HTTP/" | awk '{print \$2}' || echo "000")
                    
                    # Validar resposta
                    if [ "\$HEALTH_RESPONSE" = "200" ]; then
                        echo "✓ Health check passed (HTTP 200)"
                        exit 0
                    else
                        echo "✗ Health check failed (HTTP \$HEALTH_RESPONSE)"
                        exit 1
                    fi
                """
                    } else {
                        bat """
                    set KUBECONFIG=%KUBECONFIG%
                    
                    REM Executar health check dentro do pod usando wget (disponível na imagem Java)
                    kubectl exec ${podName} -n ${namespace} -- wget -q -O /dev/null -S http://localhost:${servicePort}/actuator/health > health_output.txt 2>&1
                    
                    REM Verificar se o comando foi bem sucedido (exit code 0 = HTTP 200)
                    if %ERRORLEVEL% EQU 0 (
                        echo Health check passed (HTTP 200)
                        del health_output.txt
                        exit /b 0
                    ) else (
                        echo Health check failed
                        type health_output.txt
                        del health_output.txt
                        exit /b 1
                    )
                """
                    }

                    // 5. Verificar se não há restart loops
                    echo "Step 5: Checking for restart loops..."
                    if (isUnix()) {
                        def restartCount = sh(
                                script: """
                        export KUBECONFIG=\${KUBECONFIG}
                        kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} \
                        -o jsonpath='{.items[].status.containerStatuses[].restartCount}' | \
                        awk '{for(i=1;i<=NF;i++) sum+=\$i} END {print sum}'
                    """,
                                returnStdout: true
                        ).trim()

                        if (restartCount.toInteger() > 0) {
                            echo "⚠ Warning: Pods have restarted ${restartCount} times"
                        }
                    } else {
                        bat """
                    set KUBECONFIG=%KUBECONFIG%
                    kubectl get pods -n ${namespace} -l app=${serviceName},color=${color} -o wide
                """
                    }
                    echo "Restart check completed"

                    echo """======================================================
                    ALL HEALTH CHECKS PASSED
                    ======================================================"""

                } catch (Exception e) {
                    echo """======================================================
                    HEALTH CHECKS FAILED
                    ======================================================"""
                    error("Health checks failed for ${serviceName}-${color} in ${namespace}: ${e.message}")
                }
            }
}