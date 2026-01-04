
import org.jenkinsPipeline.Constants

/**
 * Executa um health check in-cluster chamando /actuator/health via curl
 *
 * @param serviceName  Nome do Service Kubernetes (ex.: 'lendings-c-service')
 * @param namespace    Namespace (ex.: 'dev')
 * @param port         Porta exposta pelo Service (ex.: 8881)
 * @param healthPath   Caminho do health (default: '/actuator/health')
 * @param timeoutSec   Timeout total do curl (segundos, default: 10)
 * @param retries      Nº de tentativas (default: 3)
 */
def call(String serviceName, String namespace, int port,
         String healthPath = '/actuator/health', int timeoutSec = 10, int retries = 3) {

    // URL no cluster (FQDN do service)
    def url = "http://${serviceName}.${namespace}.svc.cluster.local:${port}${healthPath}"

    echo """
        ==========================================
        IN-CLUSTER HEALTH CHECK
        ==========================================
        Service:   ${serviceName}
        Namespace: ${namespace}
        URL:       ${url}
        Timeout:   ${timeoutSec}s   Retries: ${retries}
        ==========================================
    """

    withCredentials([
            file(credentialsId: Constants.ENVIRONMENT_2_CREDENTIALS_ID[namespace], variable: 'KUBECONFIG')
    ]) {
        try {
            if (isUnix()) {
                // Fluxo robusto: criar → wait → exec → delete
                sh """
                    set -e

                    # 1) Cria pod 'curl-smoke' que permanece vivo
                    kubectl run curl-smoke --restart=Never --image=curlimages/curl -n ${namespace} -- \\
                      sh -c 'sleep 3600'

                    # 2) Espera o Pod ficar Ready
                    kubectl wait --for=condition=Ready pod/curl-smoke -n ${namespace} --timeout=90s

                    # 3) Executa o curl (fail-fast -f, silencioso -s, mostra erros -S, timeout -m, retries)
                    kubectl exec -n ${namespace} curl-smoke -- \\
                      sh -c 'echo "Calling ${url} ..." && \\
                             curl -f -s -S -m ${timeoutSec} --retry ${retries} --retry-connrefused --retry-delay 1 \\
                                  -o /dev/null "${url}"'

                    # 4) Limpa
                    kubectl delete pod curl-smoke -n ${namespace} --grace-period=0 --force
                """
            } else {
                // Windows CMD
                bat """
                    @echo on

                    REM 1) Cria pod 'curl-smoke' que permanece vivo
                    kubectl run curl-smoke --restart=Never --image=curlimages/curl -n ${namespace} -- ^
                      sh -c "sleep 3600"

                    REM 2) Espera o Pod ficar Ready
                    kubectl wait --for=condition=Ready pod/curl-smoke -n ${namespace} --timeout=90s

                    REM 3) Executa o curl (sem HTML escapes)
                    kubectl exec -n ${namespace} curl-smoke -- ^
                      sh -c "echo Calling ${url} ... && curl -f -s -S -m ${timeoutSec} --retry ${retries} --retry-connrefused --retry-delay 1 -o /dev/null \\"${url}\\""
                    IF %ERRORLEVEL% NEQ 0 (
                        ECHO Health check FAILED for ${url}
                        kubectl delete pod curl-smoke -n ${namespace} --grace-period=0 --force
                        EXIT /B 1
                    )

                    REM 4) Limpa
                    kubectl delete pod curl-smoke -n ${namespace} --grace-period=0 --force
                """
            }

            echo "✅ Health check PASSED for ${url}"
        } catch (Exception e) {
            echo "❌ Health check FAILED for ${url}: ${e.getMessage()}"
            // tenta garantir limpeza do pod em caso de erro
            try {
                if (isUnix()) {
                    sh "kubectl delete pod curl-smoke -n ${namespace} --grace-period=0 --force || true"
                } else {
                    bat "kubectl delete pod curl-smoke -n ${namespace} --grace-period=0 --force"
                }
            } catch (ignore) { /* noop */ }
            throw e
        }
    }
}
